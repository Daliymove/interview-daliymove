package com.daliymove.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.system.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /**
     * 批量插入角色菜单关联
     * 
     * @param roleMenus 角色菜单列表
     */
    void insertBatch(List<RoleMenu> roleMenus);
}