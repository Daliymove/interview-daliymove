package com.daliymove.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daliymove.common.response.PageResult;
import com.daliymove.common.dto.PageDTO;
import com.daliymove.system.dto.RoleDTO;
import com.daliymove.system.entity.Role;
import com.daliymove.system.entity.RoleMenu;
import com.daliymove.system.entity.RolePermission;
import com.daliymove.system.mapper.RoleMapper;
import com.daliymove.system.mapper.RoleMenuMapper;
import com.daliymove.system.mapper.RolePermissionMapper;
import com.daliymove.system.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色服务类
 * - 角色的增删改查
 * - 角色权限分配
 * - 角色菜单分配
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final RoleMenuMapper roleMenuMapper;

    /**
     * 分页查询角色列表
     * 
     * @param dto 分页查询参数
     * @return 角色分页列表
     */
    public PageResult<RoleVO> getPage(PageDTO dto) {
        Page<Role> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getDeleted, 0);

        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w
                .like(Role::getRoleName, dto.getKeyword())
                .or()
                .like(Role::getRoleCode, dto.getKeyword())
            );
        }

        if (dto.getStatus() != null) {
            wrapper.eq(Role::getStatus, dto.getStatus());
        }

        wrapper.orderByDesc(Role::getCreateTime);

        Page<Role> rolePage = roleMapper.selectPage(page, wrapper);

        List<Role> roles = rolePage.getRecords();
        Map<Long, List<Long>> permissionIdsMap = new HashMap<>();
        Map<Long, List<Long>> menuIdsMap = new HashMap<>();
        
        if (!roles.isEmpty()) {
            List<Long> roleIds = roles.stream()
                .map(Role::getId)
                .collect(Collectors.toList());
            permissionIdsMap = batchGetPermissionIdsByRoleIds(roleIds);
            menuIdsMap = batchGetMenuIdsByRoleIds(roleIds);
        }

        final Map<Long, List<Long>> finalPermissionIdsMap = permissionIdsMap;
        final Map<Long, List<Long>> finalMenuIdsMap = menuIdsMap;
        List<RoleVO> voList = roles.stream()
            .map(role -> convertToVOWithRelations(
                role, 
                finalPermissionIdsMap.getOrDefault(role.getId(), new ArrayList<>()),
                finalMenuIdsMap.getOrDefault(role.getId(), new ArrayList<>())
            ))
            .collect(Collectors.toList());

        PageResult<RoleVO> result = new PageResult<>();
        result.setTotal(rolePage.getTotal());
        result.setSize(rolePage.getSize());
        result.setCurrent(rolePage.getCurrent());
        result.setPages(rolePage.getPages());
        result.setRecords(voList);

        return result;
    }

    public List<RoleVO> listAll() {
        List<Role> roles = roleMapper.selectList(
            new LambdaQueryWrapper<Role>()
                .eq(Role::getDeleted, 0)
                .eq(Role::getStatus, 1)
                .orderByDesc(Role::getCreateTime)
        );

        return roles.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    public RoleVO getById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null || role.getDeleted() == 1) {
            throw new IllegalArgumentException("角色不存在");
        }
        return convertToVO(role);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(RoleDTO dto) {
        Role existRole = roleMapper.selectOne(
            new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, dto.getRoleCode())
                .eq(Role::getDeleted, 0)
        );

        if (existRole != null) {
            throw new IllegalArgumentException("角色编码已存在");
        }

        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        roleMapper.insert(role);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(RoleDTO dto) {
        Role role = roleMapper.selectById(dto.getId());
        if (role == null || role.getDeleted() == 1) {
            throw new IllegalArgumentException("角色不存在");
        }

        Role existRole = roleMapper.selectOne(
            new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, dto.getRoleCode())
                .ne(Role::getId, dto.getId())
                .eq(Role::getDeleted, 0)
        );

        if (existRole != null) {
            throw new IllegalArgumentException("角色编码已存在");
        }

        BeanUtils.copyProperties(dto, role);
        roleMapper.updateById(role);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null || role.getDeleted() == 1) {
            throw new IllegalArgumentException("角色不存在");
        }

        roleMapper.deleteById(id);

        rolePermissionMapper.delete(
            new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getRoleId, id)
        );

        roleMenuMapper.delete(
            new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, id)
        );
    }

    /**
     * 为角色分配权限
     * 
     * 先删除角色原有的权限关联，再重新分配新权限。
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.delete(
            new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getRoleId, roleId)
        );

        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<RolePermission> rolePermissions = permissionIds.stream()
                .map(permissionId -> {
                    RolePermission rp = new RolePermission();
                    rp.setRoleId(roleId);
                    rp.setPermissionId(permissionId);
                    return rp;
                })
                .collect(Collectors.toList());
            rolePermissionMapper.insertBatch(rolePermissions);
        }
    }

    /**
     * 为角色分配菜单
     * 
     * 先删除角色原有的菜单关联，再重新分配新菜单。
     * 
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.delete(
            new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId)
        );

        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenu> roleMenus = menuIds.stream()
                .map(menuId -> {
                    RoleMenu rm = new RoleMenu();
                    rm.setRoleId(roleId);
                    rm.setMenuId(menuId);
                    return rm;
                })
                .collect(Collectors.toList());
            roleMenuMapper.insertBatch(roleMenus);
        }
    }

    /**
     * 将角色实体转换为视图对象
     * 
     * @param role 角色实体
     * @return 角色视图对象
     */
    private RoleVO convertToVO(Role role) {
        List<RolePermission> permissions = rolePermissionMapper.selectList(
            new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getRoleId, role.getId())
        );
        List<Long> permissionIds = permissions.stream()
            .map(RolePermission::getPermissionId)
            .collect(Collectors.toList());

        List<RoleMenu> menus = roleMenuMapper.selectList(
            new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, role.getId())
        );
        List<Long> menuIds = menus.stream()
            .map(RoleMenu::getMenuId)
            .collect(Collectors.toList());

        return convertToVOWithRelations(role, permissionIds, menuIds);
    }

    /**
     * 将角色实体转换为视图对象（使用预加载的关联数据）
     * 
     * @param role 角色实体
     * @param permissionIds 权限ID列表
     * @param menuIds 菜单ID列表
     * @return 角色视图对象
     */
    private RoleVO convertToVOWithRelations(Role role, List<Long> permissionIds, List<Long> menuIds) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        vo.setPermissionIds(permissionIds);
        vo.setMenuIds(menuIds);
        return vo;
    }

    /**
     * 批量获取角色的权限ID列表
     * 
     * @param roleIds 角色ID列表
     * @return Map<角色ID, 权限ID列表>
     */
    private Map<Long, List<Long>> batchGetPermissionIdsByRoleIds(List<Long> roleIds) {
        List<Map<String, Object>> results = roleMapper.selectPermissionIdsByRoleIds(roleIds);
        Map<Long, List<Long>> permissionIdsMap = new HashMap<>();
        
        for (Map<String, Object> row : results) {
            Long roleId = ((Number) row.get("roleId")).longValue();
            Long permissionId = ((Number) row.get("permissionId")).longValue();
            permissionIdsMap.computeIfAbsent(roleId, k -> new ArrayList<>()).add(permissionId);
        }
        
        return permissionIdsMap;
    }

    /**
     * 批量获取角色的菜单ID列表
     * 
     * @param roleIds 角色ID列表
     * @return Map<角色ID, 菜单ID列表>
     */
    private Map<Long, List<Long>> batchGetMenuIdsByRoleIds(List<Long> roleIds) {
        List<Map<String, Object>> results = roleMapper.selectMenuIdsByRoleIds(roleIds);
        Map<Long, List<Long>> menuIdsMap = new HashMap<>();
        
        for (Map<String, Object> row : results) {
            Long roleId = ((Number) row.get("roleId")).longValue();
            Long menuId = ((Number) row.get("menuId")).longValue();
            menuIdsMap.computeIfAbsent(roleId, k -> new ArrayList<>()).add(menuId);
        }
        
        return menuIdsMap;
    }
}