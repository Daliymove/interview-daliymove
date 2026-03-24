package com.daliymove.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.system.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<String> selectPermissionsByUserId(Long userId);

    List<String> selectRolesByUserId(Long userId);

    List<Long> selectMenuIdsByUserId(Long userId);
}