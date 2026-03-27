package com.daliymove.system.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daliymove.common.domain.LoginUser;
import com.daliymove.common.response.PageResult;
import com.daliymove.system.dto.LoginDTO;
import com.daliymove.common.dto.PageDTO;
import com.daliymove.system.dto.UserDTO;
import com.daliymove.system.entity.User;
import com.daliymove.system.entity.UserRole;
import com.daliymove.system.mapper.UserMapper;
import com.daliymove.system.mapper.UserRoleMapper;
import com.daliymove.system.vo.UserVO;
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
 * 用户服务类
 * - 用户登录、登出、获取当前用户信息
 * - 用户的增删改查
 * - 用户角色分配
 * - 用户密码加密和验证（使用 Sa-Token 和 BCrypt）
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    /**
     * 用户登录
     * 
     * 验证用户名和密码，登录成功后创建会话并返回用户信息和 Token。
     * 
     * @param dto 登录请求参数，包含用户名和密码
     * @return 登录用户信息，包含 Token
     * @throws IllegalArgumentException 用户名或密码错误、账号被禁用
     */
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

    /**
     * 用户登出
     * 
     * 清除当前用户的登录状态。
     */
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 获取当前登录用户信息
     * 
     * @return 当前登录用户信息
     * @throws IllegalArgumentException 用户不存在
     */
    public LoginUser getCurrentUser() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(userId);

        if (user == null || user.getDeleted() == 1) {
            throw new IllegalArgumentException("用户不存在");
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getNickname());
        loginUser.setAvatar(user.getAvatar());

        return loginUser;
    }

    /**
     * 分页查询用户列表
     * 
     * 支持按关键词、状态、部门进行筛选。
     * 
     * @param dto 分页查询参数
     * @return 用户分页列表
     */
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

        Page<User> userPage = userMapper.selectPage(page, wrapper);

        List<User> users = userPage.getRecords();
        Map<Long, List<String>> rolesMap = new HashMap<>();
        
        if (!users.isEmpty()) {
            List<Long> userIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toList());
            rolesMap = batchGetRolesByUserIds(userIds);
        }

        final Map<Long, List<String>> finalRolesMap = rolesMap;
        List<UserVO> voList = users.stream()
            .map(user -> convertToVOWithRoles(user, finalRolesMap.getOrDefault(user.getId(), new ArrayList<>())))
            .collect(Collectors.toList());

        PageResult<UserVO> result = new PageResult<>();
        result.setTotal(userPage.getTotal());
        result.setSize(userPage.getSize());
        result.setCurrent(userPage.getCurrent());
        result.setPages(userPage.getPages());
        result.setRecords(voList);

        return result;
    }

    /**
     * 根据ID获取用户详情
     * 
     * @param id 用户ID
     * @return 用户详情
     * @throws IllegalArgumentException 用户不存在
     */
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null || user.getDeleted() == 1) {
            throw new IllegalArgumentException("用户不存在");
        }
        return convertToVO(user);
    }

    /**
     * 新增用户
     * 
     * 用户密码使用 BCrypt 加密后存储。
     * 
     * @param dto 用户信息
     * @throws IllegalArgumentException 用户名已存在
     */
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

    /**
     * 更新用户信息
     * 
     * 如果提供了新密码，则使用 BCrypt 加密后更新。
     * 
     * @param dto 用户信息
     * @throws IllegalArgumentException 用户不存在
     */
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

    /**
     * 删除用户（逻辑删除）
     * 
     * 同时删除用户关联的角色信息。
     * 不允许删除当前登录用户。
     * 
     * @param id 用户ID
     * @throws IllegalArgumentException 用户不存在、尝试删除当前用户
     */
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

    /**
     * 为用户分配角色
     * 
     * 先删除用户原有的角色关联，再重新分配新角色。
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.delete(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
        );

        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = roleIds.stream()
                .map(roleId -> {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    return userRole;
                })
                .collect(Collectors.toList());
            userRoleMapper.insertBatch(userRoles);
        }
    }

    /**
     * 将用户实体转换为视图对象
     * 
     * @param user 用户实体
     * @return 用户视图对象
     */
    private UserVO convertToVO(User user) {
        return convertToVOWithRoles(user, userMapper.selectRolesByUserId(user.getId()));
    }

    /**
     * 将用户实体转换为视图对象（使用预加载的角色列表）
     * 
     * @param user 用户实体
     * @param roles 用户角色列表
     * @return 用户视图对象
     */
    private UserVO convertToVOWithRoles(User user, List<String> roles) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setRoles(roles);
        return vo;
    }

    /**
     * 批量获取用户的角色列表
     * 
     * @param userIds 用户ID列表
     * @return Map<用户ID, 角色编码列表>
     */
    private Map<Long, List<String>> batchGetRolesByUserIds(List<Long> userIds) {
        List<Map<String, Object>> results = userMapper.selectRolesByUserIds(userIds);
        Map<Long, List<String>> rolesMap = new HashMap<>();
        
        for (Map<String, Object> row : results) {
            Long userId = ((Number) row.get("userId")).longValue();
            String roleCode = (String) row.get("roleCode");
            rolesMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(roleCode);
        }
        
        return rolesMap;
    }
}