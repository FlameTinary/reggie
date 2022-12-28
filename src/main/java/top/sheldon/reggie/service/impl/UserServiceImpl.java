package top.sheldon.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.sheldon.reggie.dao.UserDao;
import top.sheldon.reggie.domain.User;
import top.sheldon.reggie.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
}
