package top.sheldon.reggie.dto;

import lombok.Data;
import top.sheldon.reggie.domain.Dish;
import top.sheldon.reggie.domain.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
