package top.sheldon.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.sheldon.reggie.dao.ShoppingCartDao;
import top.sheldon.reggie.domain.ShoppingCart;
import top.sheldon.reggie.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartDao, ShoppingCart> implements ShoppingCartService {
}
