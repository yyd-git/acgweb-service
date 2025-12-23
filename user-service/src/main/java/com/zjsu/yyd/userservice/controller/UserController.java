package com.zjsu.yyd.userservice.controller;

import com.zjsu.yyd.userservice.model.Result;
import com.zjsu.yyd.userservice.model.User;
import com.zjsu.yyd.userservice.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 注册 */
    @PostMapping("/register")
    public Result register(@RequestParam String username,
                           @RequestParam String password) {
        return userService.register(username, password)
                ? Result.success("注册成功")
                : Result.error("用户名已存在");
    }

    /** 登录，返回 token（放在 data 字段） */
    @PostMapping("/login")
    public Result login(@RequestParam String username,
                        @RequestParam String password) {

        String token = userService.loginAndGetToken(username, password);

        // ✅ token 放到 data，msg 返回 "success"
        return token != null
                ? new Result(1, "success", token)
                : Result.error("用户名或密码错误");

    }

    /** 删除用户（软删除） */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        return userService.delete(id)
                ? Result.success("删除成功")
                : Result.error("用户不存在");
    }

    /** 根据 ID 查询用户（供 Feign 调用） */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return user != null
                ? Result.success(user)
                : Result.error("用户不存在");
    }
}
