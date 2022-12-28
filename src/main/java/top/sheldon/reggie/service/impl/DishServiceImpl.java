package top.sheldon.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sheldon.reggie.dao.DishDao;
import top.sheldon.reggie.domain.Dish;
import top.sheldon.reggie.domain.DishFlavor;
import top.sheldon.reggie.dto.DishDto;
import top.sheldon.reggie.service.DishFlavorService;
import top.sheldon.reggie.service.DishService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品信息
        this.save(dishDto);

        // 获取菜品id
        Long dishId = dishDto.getId();

        // 在口味列表重添加菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        // 保存口味信息
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto selectWithFlavor(Long id) {
        Dish dish = this.getById(id);

        // 构造DishDto，并将dish的属性赋值给它
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        // 通过dish的id查询dish_flavor表
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithDishDto(DishDto dishDto) {
        // 保存菜品信息
        this.updateById(dishDto);

        // 获取菜品id
        Long dishId = dishDto.getId();

        // 清理当前菜品对应的口味
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        // 在口味列表重添加菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        // 保存口味信息
        dishFlavorService.saveBatch(flavors);
    }
}
