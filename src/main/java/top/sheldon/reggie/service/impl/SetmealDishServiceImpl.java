package top.sheldon.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.sheldon.reggie.dao.SetmealDishDao;
import top.sheldon.reggie.domain.SetmealDish;
import top.sheldon.reggie.service.SetmealDishService;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishDao, SetmealDish> implements SetmealDishService {
}
