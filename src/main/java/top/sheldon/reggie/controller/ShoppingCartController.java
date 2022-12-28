package top.sheldon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.sheldon.reggie.common.R;
import top.sheldon.reggie.domain.ShoppingCart;
import top.sheldon.reggie.service.ShoppingCartService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List> list(HttpSession session){
        // 获取用户id
        Long userId = (Long)session.getAttribute("user");

        // 查询数据库，如果已经有菜品或者套餐，就在基础上加1，如果没有，则新增
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(shoppingCarts);
    }

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session){

        // 获取用户id
        Long userId = (Long)session.getAttribute("user");
        shoppingCart.setUserId(userId);

        // 查询数据库，如果已经有菜品或者套餐，就在基础上加1，如果没有，则新增
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);

        if (shoppingCart.getDishId() != null) {
            // 如果是菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());

        } else {
            // 如果是套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart one = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        if (one != null) {
            // 不为空，更新数据库
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);
        } else {
            shoppingCart.setNumber(1);
            LocalDateTime localDateTime = LocalDateTime.now();
            shoppingCart.setCreateTime(localDateTime);
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }


        return R.success(one);
    }

    @DeleteMapping("/clean")
    public R<String> clean(HttpSession session){
        // 获取用户id
        Long userId = (Long)session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return R.success("清空购物车成功");
    }

    @PostMapping("//sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        // 获取用户id
        Long userId = (Long)session.getAttribute("user");
        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        if (shoppingCart.getDishId() != null) {
            // 如果是菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());

        } else {
            // 如果是套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        Integer number = one.getNumber();
        one.setNumber(number - 1);
        if (one.getNumber() == 0) {
            shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        } else {
            shoppingCartService.updateById(one);
        }
        return R.success("success");
    }
}
