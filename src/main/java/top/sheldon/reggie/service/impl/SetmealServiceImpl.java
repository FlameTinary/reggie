package top.sheldon.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sheldon.reggie.common.CustomException;
import top.sheldon.reggie.dao.SetmealDao;
import top.sheldon.reggie.domain.Setmeal;
import top.sheldon.reggie.domain.SetmealDish;
import top.sheldon.reggie.dto.SetmealDto;
import top.sheldon.reggie.service.SetmealDishService;
import top.sheldon.reggie.service.SetmealService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealDao, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐信息到数据库
        this.save(setmealDto);

        // 获取套餐的id
        Long setmealId = setmealDto.getId();

        // 拼接套餐和菜品的关联信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes().stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids) {
        // 查询套餐售卖状态
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        setmealLambdaQueryWrapper.in(Setmeal::getId, ids).eq(Setmeal::getStatus, 1);
        int count = this.count(setmealLambdaQueryWrapper);
        // 如果是在售状态不能删除
        if (count > 0) {
            throw new CustomException("有在售状态的套餐，不能删除");
        }
        // 删除套餐数据
        this.removeByIds(ids);
        // 删除套餐关联的菜品数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // delete from setmeal_dish where setmeal_id in (1,2,3)
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }
}
