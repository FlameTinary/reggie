package top.sheldon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.sheldon.reggie.common.R;
import top.sheldon.reggie.domain.Category;
import top.sheldon.reggie.domain.Dish;
import top.sheldon.reggie.domain.DishFlavor;
import top.sheldon.reggie.dto.DishDto;
import top.sheldon.reggie.service.CategoryService;
import top.sheldon.reggie.service.DishFlavorService;
import top.sheldon.reggie.service.DishService;

import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 菜品列表查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        // 创建Page对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);

        // 创建查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        // 执行分页查询
        dishService.page(pageInfo, dishLambdaQueryWrapper);

        /**
         * 因为我们返回的内容中除了有Dish对象中的内容，还有分类信息，所以在这里我们不能直接返回pageInfo
         * 需要将pageInfo中的records修改为带categoryName的内容
         * 所以我们需要创建一个新的DishDto的page
         */
        // 创建DishDto的page
        Page<DishDto> dishDtoPage = new Page<>();

        // 拷贝Dish的page中除了records属性内容的其他属性内容属性到dishDtoPage中
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        // 取出pageInfo中的records
        List<Dish> records = pageInfo.getRecords();

        // 1. 通过遍历records中的Dish，拿到categoryId
        // 2. 通过categoryId查询category的内容
        // 3. 然后将category中的name和Dish重新组装成DishDto
        List<DishDto> dishDtos = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());

        // 4. 将组装好的DishDto列表赋值给dishDtoPage的records字段
        dishDtoPage.setRecords(dishDtos);

        return R.success(dishDtoPage);
    }

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);

        // 精确删除redis缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        
        return R.success("菜品添加成功");
    }

    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable Long id){

        DishDto dishDto = dishService.selectWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithDishDto(dishDto);

        // 精确删除redis缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("更新菜品成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> listById(Dish dish){
        List<DishDto> dishDtos;
        // 构造redis需要的key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        // 查询redis数据库
        dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);

        // 如果redis中没有查询的数据，则去mysql中查询
        if (dishDtos == null) {
            LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
            // 添加条件，查询状态是1 在售的菜品
            dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
            dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
            List<Dish> dishes = dishService.list(dishLambdaQueryWrapper);

            dishDtos = dishes.stream().map((item)->{
                DishDto dishDto = new DishDto();
                BeanUtils.copyProperties(item, dishDto);
                Long categoryId = item.getCategoryId();
                Category category = categoryService.getById(categoryId);
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);

                // 根据dishID查询口味信息
                LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
                dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, item.getId());
                List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
                dishDto.setFlavors(dishFlavors);

                return dishDto;
            }).collect(Collectors.toList());

            // 将数据缓存到redis
            redisTemplate.opsForValue().set(key, dishDtos, 60, TimeUnit.MINUTES);
        }

        return R.success(dishDtos);
    }
}
