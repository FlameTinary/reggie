package top.sheldon.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.sheldon.reggie.domain.DishFlavor;

@Mapper
public interface DishFlavorDao extends BaseMapper<DishFlavor> {
}
