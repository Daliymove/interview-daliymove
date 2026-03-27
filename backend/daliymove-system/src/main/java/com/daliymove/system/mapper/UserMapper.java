package com.daliymove.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.system.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<String> selectPermissionsByUserId(Long userId);

    List<String> selectRolesByUserId(Long userId);

    List<Long> selectMenuIdsByUserId(Long userId);

    /**
     * 批量查询用户的角色列表
     * 
     * @param userIds 用户ID列表
     * @return Map<用户ID, 角色编码列表>
     */
    List<Map<String, Object>> selectRolesByUserIds(@Param("userIds") List<Long> userIds);
}