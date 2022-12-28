package top.sheldon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.sheldon.reggie.common.R;
import top.sheldon.reggie.domain.Category;
import top.sheldon.reggie.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("新增分类：{}", category.toString());
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 查询分类列表
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        Page<Category> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除分类
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
//        categoryService.removeById(id);
        categoryService.remove(ids);
        return R.success("删除分类成功");
    }

    /**
     * 修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}", category.toString());
        categoryService.updateById(category);
        return R.success("分类信息修改成功");
    }

    /**
     * 查询菜品分类列表
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> categoryList(Category category){
        // 创建查询条件
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加条件
        categoryLambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        // 排序
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getCreateTime);

        List<Category> categories = categoryService.list(categoryLambdaQueryWrapper);

        return R.success(categories);
    }

}
