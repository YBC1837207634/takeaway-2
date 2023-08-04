package com.example.controller;

import com.example.common.Result;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.utils.JWTUtil;
import com.example.utils.VerificationCode;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if (StringUtils.hasText(phone)) {
            String code = VerificationCode.verification(4);
            log.info("{}", code);
            // 发送验证码

            // 记录手机与验证码
//            HttpSession session = request.getSession();
            session.setAttribute(phone, code);
            return Result.success("验证码发送成功");
        }

        return Result.error("验证码发送失败！");
    }

    /**
     * 登陆密钥 utoken
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody Map<String, String> map, HttpSession session) {
        String phone = map.get("phone");
        String code = map.get("code");
        if (StringUtils.hasText(phone) && StringUtils.hasText(code)) {
            String c = (String) session.getAttribute(phone);
            if (!StringUtils.hasLength(c)) return Result.error("验证码无效");
            // 验证码正确
            if (c.equals(code)) {
                // 清除 session 中保存的 phone
                session.removeAttribute(phone);
                // 注册，如果已经注册过则返回用户信息
                User u = new User();
                u.setPhone(phone);
                User user = userService.registerLogin(u);  // 登陆注册
                // 获取 token
                Map<String,Object> claims = new HashMap<>();
                Long id = user.getId();
                claims.put("phone",phone);
                claims.put("id", id);
                String token = JWTUtil.produce(claims);
                return Result.success("utoken: " + token);
            }
        }
        return Result.error("验证码错误！");
    }


    @GetMapping("/test")
    public Result<String> test() {
        return null;
    }
}
