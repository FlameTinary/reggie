package top.sheldon.reggie.dto;

import lombok.Data;
import top.sheldon.reggie.domain.Setmeal;
import top.sheldon.reggie.domain.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
