package com.daliymove.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daliymove.common.response.PageResult;
import com.daliymove.common.dto.PageDTO;
import com.daliymove.system.dto.PermissionDTO;
import com.daliymove.system.entity.Permission;
import com.daliymove.system.mapper.PermissionMapper;
import com.daliymove.system.vo.PermissionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionMapper permissionMapper;

    public PageResult<PermissionVO> getPage(PageDTO dto) {
        Page<Permission> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getDeleted, 0);

        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w
                .like(Permission::getPermissionName, dto.getKeyword())
                .or()
                .like(Permission::getPermissionCode, dto.getKeyword())
            );
        }

        if (dto.getStatus() != null) {
            wrapper.eq(Permission::getStatus, dto.getStatus());
        }

        wrapper.orderByAsc(Permission::getParentId);
        wrapper.orderByAsc(Permission::getSort);

        Page<Permission> permissionPage = permissionMapper.selectPage(page, wrapper);

        return PageResult.of(permissionPage, this::convertToVO);
    }

    public List<PermissionVO> listAll() {
        List<Permission> permissions = permissionMapper.selectList(
            new LambdaQueryWrapper<Permission>()
                .eq(Permission::getDeleted, 0)
                .eq(Permission::getStatus, 1)
                .orderByAsc(Permission::getSort)
                .orderByDesc(Permission::getCreateTime)
        );

        return permissions.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    public PermissionVO getById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null || permission.getDeleted() == 1) {
            throw new IllegalArgumentException("权限不存在");
        }
        return convertToVO(permission);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(PermissionDTO dto) {
        Permission existPermission = permissionMapper.selectOne(
            new LambdaQueryWrapper<Permission>()
                .eq(Permission::getPermissionCode, dto.getPermissionCode())
                .eq(Permission::getDeleted, 0)
        );

        if (existPermission != null) {
            throw new IllegalArgumentException("权限编码已存在");
        }

        Permission permission = new Permission();
        BeanUtils.copyProperties(dto, permission);
        permission.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        permission.setSort(dto.getSort() != null ? dto.getSort() : 0);
        permission.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);

        permissionMapper.insert(permission);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(PermissionDTO dto) {
        Permission permission = permissionMapper.selectById(dto.getId());
        if (permission == null || permission.getDeleted() == 1) {
            throw new IllegalArgumentException("权限不存在");
        }

        Permission existPermission = permissionMapper.selectOne(
            new LambdaQueryWrapper<Permission>()
                .eq(Permission::getPermissionCode, dto.getPermissionCode())
                .ne(Permission::getId, dto.getId())
                .eq(Permission::getDeleted, 0)
        );

        if (existPermission != null) {
            throw new IllegalArgumentException("权限编码已存在");
        }

        BeanUtils.copyProperties(dto, permission);
        permissionMapper.updateById(permission);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null || permission.getDeleted() == 1) {
            throw new IllegalArgumentException("权限不存在");
        }

        permissionMapper.deleteById(id);
    }

    private PermissionVO convertToVO(Permission permission) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }
}