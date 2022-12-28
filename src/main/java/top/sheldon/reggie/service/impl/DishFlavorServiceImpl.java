package top.sheldon.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.sheldon.reggie.dao.DishFlavorDao;
import top.sheldon.reggie.domain.DishFlavor;
import top.sheldon.reggie.service.DishFlavorService;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorDao, DishFlavor> implements DishFlavorService {
}
