package com.daliymove.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.daliymove.common.domain.LoginUser;
import com.daliymove.common.response.Result;
import com.daliymove.system.dto.LoginDTO;
import com.daliymove.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * - 用户登录
 * - 用户登出
 * - 获取当前用户信息
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户登录接口（无需认证）
     * @param dto 登录请求参数
     * @return 登录用户信息，包含 Token
     */
    @SaIgnore
    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginUser> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    /** 用户登出接口 */
    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }

    /** 获取当前用户信息接口 */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public Result<LoginUser> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }
}