package top.sheldon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import top.sheldon.reggie.common.R;
import top.sheldon.reggie.domain.Employee;
import top.sheldon.reggie.service.EmployeeService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // 密码md5处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee employee1 = employeeService.getOne(queryWrapper);
        // 如果没有查询到，则返回失败
        if (employee1 == null) {
            return R.error("没有该员工");
        }
        // 如果密码比对不一致则返回失败
        if (!employee1.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        // 如果员工状态为禁止登录，则返回员工禁用结果
        if (employee1.getStatus() == 0) {
            return R.error("该员工禁止登录");
        }
        // 登录成功，将员工的id存入session中，并返回成功登录的结果
        Long employee1Id = employee1.getId();
        request.getSession().setAttribute("employee", employee1Id);
        return R.success(employee1);
    }

    /**
     * 登出账户
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 添加员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){

        log.info("新增员工，员工信息：{}", employee.toString());

        // 设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 设置创建时间和更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        // 获取当前登录人信息
//        Long empId = (Long) request.getSession().getAttribute("employee");
        // 设置创建人和更新人
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        // 保存数据库
        employeeService.save(employee);

        return R.success("新增员工成功");
    }


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        // 分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper();
        // 添加过滤条件
        wrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 添加排序条件
        wrapper.orderByDesc(Employee::getCreateTime);

        // 执行查询
        employeeService.page(pageInfo, wrapper);

        return R.success(pageInfo);
    }

    /**
     * 修改员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);

        long id = Thread.currentThread().getId();
        log.info("当前线程为：{}", id);

        employeeService.updateById(employee);
        return R.success("员工信息更新成功");
    }


    /**
     * 根据id查询员工数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}
