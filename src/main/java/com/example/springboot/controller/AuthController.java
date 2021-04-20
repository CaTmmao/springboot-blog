package com.example.springboot.controller;

import com.example.springboot.entities.Result;
import com.example.springboot.entities.User;
import com.example.springboot.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
class AuthController {
    // 用户鉴权管理服务
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    // 查看当前用户的登录状态
    @GetMapping("/auth")    // get 接口路径为 /auth
    @ResponseBody   // 将返回的数据作为 responseBody 的字符流形式传输回去
    public Object auth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication == null ? null : authentication.getName();
        User loggedUser = userService.getUserByUsername(username);

        // 用户未登录
        if (loggedUser == null) {
            return new Result(false, "ok", "unLog");
        } else {
            return Result.success("success", loggedUser);
        }
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public Result register(@RequestBody Map<String, String> param) {
        String username = param.get("username");
        String pwd = param.get("password");

        int uLength = username.length();
        int pLength = pwd.length();

        if (uLength < 1 || uLength > 15) {
            return Result.fail("用户名, 长度1到15个字符，只能是字母数字下划线中文");
        } else if (pLength < 6 || pLength > 16) {
            return Result.fail("密码, 长度6到16个任意字符");
        }

        try {
            userService.saveUser(username, pwd);
            return new Result(false, "ok", "注册成功");
        } catch (DuplicateKeyException e) {
            return Result.fail("已有该用户名");
        }
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public Result login(@RequestBody Map<String, String> usernameAndPwd) {
        // 拿出客户端传入的参数
        String username = usernameAndPwd.get("username");
        String pwd = usernameAndPwd.get("password");

        UserDetails userDetails = null;

        //　通过用户名查找用户
        try {
            userDetails = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return Result.fail("用户不存在");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, pwd, userDetails.getAuthorities());

        try {
            // 通过 token 鉴权
            authenticationManager.authenticate(token);
            // 把用户信息(cookie)保存在内存中的某个地方,便于下次拿到他
            SecurityContextHolder.getContext().setAuthentication(token);

            User user = userService.getUserByUsername(username);
            return Result.success("success", user);
        } catch (BadCredentialsException e) {
            return Result.fail("密码错误");
        }
    }

    @GetMapping("/auth/logout")
    @ResponseBody
    public Result logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.fail("该用户没有注册");
        }

        SecurityContextHolder.clearContext();
        return new Result(false, "ok", "注销成功");
    }
}