package top.sheldon.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.sheldon.reggie.domain.Employee;

@Mapper
public interface EmployeeDao extends BaseMapper<Employee> {
}
