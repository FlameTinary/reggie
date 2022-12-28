package top.sheldon.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.sheldon.reggie.domain.ShoppingCart;

@Mapper
public interface ShoppingCartDao extends BaseMapper<ShoppingCart> {
}
