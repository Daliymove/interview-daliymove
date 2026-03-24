package com.daliymove.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.daliymove.common.domain.LoginUser;
import com.daliymove.common.response.Result;
import com.daliymove.system.dto.LoginDTO;
import com.daliymove.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @SaIgnore
    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginUser> login(@RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public Result<LoginUser> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }
}