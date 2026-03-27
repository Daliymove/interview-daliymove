package com.daliymove.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.daliymove.common.annotation.Log;
import com.daliymove.common.response.PageResult;
import com.daliymove.common.response.Result;
import com.daliymove.common.dto.PageDTO;
import com.daliymove.system.dto.PermissionDTO;
import com.daliymove.system.service.PermissionService;
import com.daliymove.system.vo.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器
 * - 权限的增删改查
 */
@Tag(name = "权限管理")
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @SaCheckPermission("system:permission:query")
    @Operation(summary = "分页查询权限")
    @GetMapping("/page")
    public Result<PageResult<PermissionVO>> getPage(PageDTO dto) {
        return Result.success(permissionService.getPage(dto));
    }

    @SaCheckPermission("system:permission:query")
    @Operation(summary = "查询所有权限")
    @GetMapping("/list")
    public Result<List<PermissionVO>> listAll() {
        return Result.success(permissionService.listAll());
    }

    @SaCheckPermission("system:permission:query")
    @Operation(summary = "根据ID查询权限")
    @GetMapping("/{id}")
    public Result<PermissionVO> getById(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }

    @SaCheckPermission("system:permission:add")
    @Operation(summary = "新增权限")
    @Log("新增权限")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody PermissionDTO dto) {
        permissionService.save(dto);
        return Result.success();
    }

    @SaCheckPermission("system:permission:edit")
    @Operation(summary = "更新权限")
    @Log("更新权限")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody PermissionDTO dto) {
        permissionService.update(dto);
        return Result.success();
    }

    @SaCheckPermission("system:permission:delete")
    @Operation(summary = "删除权限")
    @Log("删除权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return Result.success();
    }
}