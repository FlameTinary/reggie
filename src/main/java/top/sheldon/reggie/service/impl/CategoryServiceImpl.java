package top.sheldon.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.sheldon.reggie.common.CustomException;
import top.sheldon.reggie.dao.CategoryDao;
import top.sheldon.reggie.domain.Category;
import top.sheldon.reggie.domain.Dish;
import top.sheldon.reggie.domain.Setmeal;
import top.sheldon.reggie.service.CategoryService;
import top.sheldon.reggie.service.DishService;
import top.sheldon.reggie.service.SetmealService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 删除分类
     * 先查询当前分类是否关联了菜品和套餐，如果关联了，则不能删除，否则可以删除
     * @param id
     */
    @Override
    public void remove(Long id) {

        // 查询是否关联了菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int disCount = dishService.count(dishLambdaQueryWrapper);
        if (disCount > 0) {
            // 已经关联了菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        // 查询是否关联了套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setmealCount > 0) {
            // 已经关联了套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        // 删除分类
        super.removeById(id);
    }
}
