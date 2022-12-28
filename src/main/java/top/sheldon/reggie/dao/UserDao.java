package top.sheldon.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.sheldon.reggie.domain.User;

@Mapper
public interface UserDao extends BaseMapper<User> {
}
