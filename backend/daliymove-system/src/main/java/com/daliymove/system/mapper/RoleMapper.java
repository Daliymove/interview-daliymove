package com.daliymove.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.system.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 批量查询角色的权限ID列表
     * 
     * @param roleIds 角色ID列表
     * @return Map<角色ID, 权限ID>
     */
    List<Map<String, Object>> selectPermissionIdsByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 批量查询角色的菜单ID列表
     * 
     * @param roleIds 角色ID列表
     * @return Map<角色ID, 菜单ID>
     */
    List<Map<String, Object>> selectMenuIdsByRoleIds(@Param("roleIds") List<Long> roleIds);
}