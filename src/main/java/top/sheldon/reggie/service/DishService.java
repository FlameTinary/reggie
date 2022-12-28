package top.sheldon.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.sheldon.reggie.domain.Dish;
import top.sheldon.reggie.dto.DishDto;

public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);
    public DishDto selectWithFlavor(Long id);
    public void updateWithDishDto(DishDto dishDto);
}
