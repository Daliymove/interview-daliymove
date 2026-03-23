package com.daliymove.admin.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daliymove.admin.common.LoginUser;
import com.daliymove.admin.common.PageResult;
import com.daliymove.admin.dto.LoginDTO;
import com.daliymove.admin.dto.PageDTO;
import com.daliymove.admin.dto.UserDTO;
import com.daliymove.admin.entity.User;
import com.daliymove.admin.entity.UserRole;
import com.daliymove.admin.mapper.UserMapper;
import com.daliymove.admin.mapper.UserRoleMapper;
import com.daliymove.admin.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    public LoginUser login(LoginDTO dto) {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername())
                .eq(User::getDeleted, 0)
        );

        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new IllegalArgumentException("账号已被禁用");
        }

        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        StpUtil.login(user.getId());

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getNickname());
        loginUser.setAvatar(user.getAvatar());
        loginUser.setToken(StpUtil.getTokenValue());

        return loginUser;
    }

    public void logout() {
        StpUtil.logout();
    }

    public LoginUser getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(userId);

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getNickname());
        loginUser.setAvatar(user.getAvatar());

        return loginUser;
    }

    public PageResult<UserVO> getPage(PageDTO dto) {
        Page<User> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);

        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w
                .like(User::getUsername, dto.getKeyword())
                .or()
                .like(User::getNickname, dto.getKeyword())
                .or()
                .like(User::getEmail, dto.getKeyword())
                .or()
                .like(User::getPhone, dto.getKeyword())
            );
        }

        if (dto.getStatus() != null) {
            wrapper.eq(User::getStatus, dto.getStatus());
        }

        if (dto.getDeptId() != null) {
            wrapper.eq(User::getDeptId, dto.getDeptId());
        }

        wrapper.orderByDesc(User::getCreateTime);

        Page<User> userPage = userMapper.selectPage(page, wrapper);

        PageResult<UserVO> result = new PageResult<>();
        result.setTotal(userPage.getTotal());
        result.setSize(userPage.getSize());
        result.setCurrent(userPage.getCurrent());
        result.setPages(userPage.getPages());
        result.setRecords(userPage.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList()));

        return result;
    }

    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new IllegalArgumentException("用户不存在");
        }
        return convertToVO(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(UserDTO dto) {
        User existUser = userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername())
                .eq(User::getDeleted, 0)
        );

        if (existUser != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        userMapper.insert(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(UserDTO dto) {
        User user = userMapper.selectById(dto.getId());
        if (user == null || user.getDeleted() == 1) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(BCrypt.hashpw(dto.getPassword()));
        }

        if (StringUtils.hasText(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }

        if (StringUtils.hasText(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }

        if (StringUtils.hasText(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }

        if (StringUtils.hasText(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }

        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }

        if (dto.getDeptId() != null) {
            user.setDeptId(dto.getDeptId());
        }

        userMapper.updateById(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id.equals(StpUtil.getLoginIdAsLong())) {
            throw new IllegalArgumentException("不能删除当前登录用户");
        }

        User user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new IllegalArgumentException("用户不存在");
        }

        userMapper.deleteById(id);

        userRoleMapper.delete(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, id)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.delete(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
        );

        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        List<String> roles = userMapper.selectRolesByUserId(user.getId());
        vo.setRoles(roles);

        return vo;
    }
}