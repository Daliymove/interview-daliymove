package com.daliymove.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.daliymove.common.annotation.Log;
import com.daliymove.common.response.PageResult;
import com.daliymove.common.response.Result;
import com.daliymove.common.dto.PageDTO;
import com.daliymove.system.dto.RoleDTO;
import com.daliymove.system.service.RoleService;
import com.daliymove.system.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 * - 角色的增删改查
 * - 角色权限分配
 * - 角色菜单分配
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @SaCheckPermission("system:role:query")
    @Operation(summary = "分页查询角色")
    @GetMapping("/page")
    public Result<PageResult<RoleVO>> getPage(PageDTO dto) {
        return Result.success(roleService.getPage(dto));
    }

    @SaCheckPermission("system:role:query")
    @Operation(summary = "查询所有角色")
    @GetMapping("/list")
    public Result<List<RoleVO>> listAll() {
        return Result.success(roleService.listAll());
    }

    @SaCheckPermission("system:role:query")
    @Operation(summary = "根据ID查询角色")
    @GetMapping("/{id}")
    public Result<RoleVO> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @SaCheckPermission("system:role:add")
    @Operation(summary = "新增角色")
    @Log("新增角色")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody RoleDTO dto) {
        roleService.save(dto);
        return Result.success();
    }

    @SaCheckPermission("system:role:edit")
    @Operation(summary = "更新角色")
    @Log("更新角色")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody RoleDTO dto) {
        roleService.update(dto);
        return Result.success();
    }

    @SaCheckPermission("system:role:delete")
    @Operation(summary = "删除角色")
    @Log("删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return Result.success();
    }

    @SaCheckPermission("system:role:edit")
    @Operation(summary = "分配权限")
    @Log("分配角色权限")
    @PostMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return Result.success();
    }

    @SaCheckPermission("system:role:edit")
    @Operation(summary = "分配菜单")
    @Log("分配角色菜单")
    @PostMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return Result.success();
    }
}