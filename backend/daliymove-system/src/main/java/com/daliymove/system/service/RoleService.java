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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final RoleMenuMapper roleMenuMapper;

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

        PageResult<RoleVO> result = new PageResult<>();
        result.setTotal(rolePage.getTotal());
        result.setSize(rolePage.getSize());
        result.setCurrent(rolePage.getCurrent());
        result.setPages(rolePage.getPages());
        result.setRecords(rolePage.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList()));

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

    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        rolePermissionMapper.delete(
            new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getRoleId, roleId)
        );

        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionMapper.insert(rolePermission);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.delete(
            new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId)
        );

        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }
    }

    private RoleVO convertToVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);

        List<RolePermission> permissions = rolePermissionMapper.selectList(
            new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getRoleId, role.getId())
        );
        vo.setPermissionIds(permissions.stream()
            .map(RolePermission::getPermissionId)
            .collect(Collectors.toList()));

        List<RoleMenu> menus = roleMenuMapper.selectList(
            new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, role.getId())
        );
        vo.setMenuIds(menus.stream()
            .map(RoleMenu::getMenuId)
            .collect(Collectors.toList()));

        return vo;
    }
}