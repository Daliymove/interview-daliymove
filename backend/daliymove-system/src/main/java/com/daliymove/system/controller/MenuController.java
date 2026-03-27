package com.daliymove.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.daliymove.common.annotation.Log;
import com.daliymove.common.response.PageResult;
import com.daliymove.common.response.Result;
import com.daliymove.system.dto.MenuDTO;
import com.daliymove.common.dto.PageDTO;
import com.daliymove.system.service.MenuService;
import com.daliymove.system.vo.MenuVO;
import com.daliymove.system.vo.RouterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 * - 获取当前用户路由
 * - 菜单的增删改查
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "获取当前用户路由")
    @GetMapping("/routers")
    public Result<List<RouterVO>> getCurrentUserRouters() {
        return Result.success(menuService.getCurrentUserRouters());
    }

    @SaCheckPermission("system:menu:query")
    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public Result<List<MenuVO>> getTree() {
        return Result.success(menuService.getTree());
    }

    @SaCheckPermission("system:menu:query")
    @Operation(summary = "分页查询菜单")
    @GetMapping("/page")
    public Result<PageResult<MenuVO>> getPage(PageDTO dto) {
        return Result.success(menuService.getPage(dto));
    }

    @SaCheckPermission("system:menu:query")
    @Operation(summary = "根据ID查询菜单")
    @GetMapping("/{id}")
    public Result<MenuVO> getById(@PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }

    @SaCheckPermission("system:menu:add")
    @Operation(summary = "新增菜单")
    @Log("新增菜单")
    @PostMapping
    public Result<Void> save(@Valid @RequestBody MenuDTO dto) {
        menuService.save(dto);
        return Result.success();
    }

    @SaCheckPermission("system:menu:edit")
    @Operation(summary = "更新菜单")
    @Log("更新菜单")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody MenuDTO dto) {
        menuService.update(dto);
        return Result.success();
    }

    @SaCheckPermission("system:menu:delete")
    @Operation(summary = "删除菜单")
    @Log("删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.success();
    }
}