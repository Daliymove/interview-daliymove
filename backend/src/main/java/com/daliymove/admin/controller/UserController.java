package com.daliymove.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.daliymove.admin.annotation.Log;
import com.daliymove.admin.common.PageResult;
import com.daliymove.admin.common.Result;
import com.daliymove.admin.dto.PageDTO;
import com.daliymove.admin.dto.UserDTO;
import com.daliymove.admin.service.UserService;
import com.daliymove.admin.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @SaCheckPermission("system:user:query")
    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<PageResult<UserVO>> getPage(PageDTO dto) {
        return Result.success(userService.getPage(dto));
    }

    @SaCheckPermission("system:user:query")
    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @SaCheckPermission("system:user:add")
    @Operation(summary = "新增用户")
    @Log("新增用户")
    @PostMapping
    public Result<Void> save(@RequestBody UserDTO dto) {
        userService.save(dto);
        return Result.success();
    }

    @SaCheckPermission("system:user:edit")
    @Operation(summary = "更新用户")
    @Log("更新用户")
    @PutMapping
    public Result<Void> update(@RequestBody UserDTO dto) {
        userService.update(dto);
        return Result.success();
    }

    @SaCheckPermission("system:user:delete")
    @Operation(summary = "删除用户")
    @Log("删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @SaCheckPermission("system:user:edit")
    @Operation(summary = "分配角色")
    @Log("分配用户角色")
    @PostMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success();
    }
}