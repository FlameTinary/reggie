package top.sheldon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.sheldon.reggie.common.R;
import top.sheldon.reggie.common.ValidateCodeUtils;
import top.sheldon.reggie.domain.User;
import top.sheldon.reggie.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        log.info(user.toString());
        String phone = user.getPhone();
        // 判断手机号是否发送过来
        if (StringUtils.isNotEmpty(phone)) {
            // 获取验证码
            String code = ValidateCodeUtils.generateValidateCode4String(4);
            log.info("验证码：{}", code);
            // 保存验证码到session
            session.setAttribute(phone, code);
            // 调用阿里云服务发送短信验证码
            return R.success(code);
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        // 获取手机号
        String phone = map.get("phone").toString();
        // 获取验证码
        String code = map.get("code").toString();
        // 从session中提取验证码
        Object codeInSession = session.getAttribute(phone);
        // 比对验证码
        if (codeInSession != null && codeInSession.equals(code)) {
            // 判断当前手机号是否已注册
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<User>();
            userLambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(userLambdaQueryWrapper);
            if (user == null) {
                // 证明用户未注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }


        return R.error("登录失败");
    }
}
