package top.sheldon.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.sheldon.reggie.dao.EmployeeDao;
import top.sheldon.reggie.domain.Employee;
import top.sheldon.reggie.service.EmployeeService;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao, Employee> implements EmployeeService {
}
