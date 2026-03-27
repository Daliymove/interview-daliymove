package com.daliymove.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.system.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 批量插入角色权限关联
     * 
     * @param rolePermissions 角色权限列表
     */
    void insertBatch(List<RolePermission> rolePermissions);
}