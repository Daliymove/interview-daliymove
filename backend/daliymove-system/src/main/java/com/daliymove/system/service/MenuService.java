package com.daliymove.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daliymove.common.response.PageResult;
import com.daliymove.system.dto.MenuDTO;
import com.daliymove.common.dto.PageDTO;
import com.daliymove.system.entity.Menu;
import com.daliymove.system.mapper.MenuMapper;
import com.daliymove.system.mapper.UserMapper;
import com.daliymove.system.vo.MenuVO;
import com.daliymove.system.vo.RouterVO;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuMapper menuMapper;
    private final UserMapper userMapper;

    public List<MenuVO> getTree() {
        List<Menu> menus = menuMapper.selectList(
            new LambdaQueryWrapper<Menu>()
                .eq(Menu::getDeleted, 0)
                .orderByAsc(Menu::getSort)
                .orderByAsc(Menu::getCreateTime)
        );

        return buildTree(menus, 0L);
    }

    public List<RouterVO> getCurrentUserRouters() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<Long> menuIds = userMapper.selectMenuIdsByUserId(userId);

        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Menu> menus = menuMapper.selectList(
            new LambdaQueryWrapper<Menu>()
                .in(Menu::getId, menuIds)
                .eq(Menu::getStatus, 1)
                .eq(Menu::getDeleted, 0)
                .in(Menu::getMenuType, 1, 2)
                .orderByAsc(Menu::getSort)
        );

        return buildRouters(menus, 0L);
    }

    public PageResult<MenuVO> getPage(PageDTO dto) {
        Page<Menu> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getDeleted, 0);

        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w
                .like(Menu::getMenuName, dto.getKeyword())
                .or()
                .like(Menu::getMenuCode, dto.getKeyword())
            );
        }

        if (dto.getStatus() != null) {
            wrapper.eq(Menu::getStatus, dto.getStatus());
        }

        wrapper.orderByAsc(Menu::getSort);
        wrapper.orderByDesc(Menu::getCreateTime);

        Page<Menu> menuPage = menuMapper.selectPage(page, wrapper);

        PageResult<MenuVO> result = new PageResult<>();
        result.setTotal(menuPage.getTotal());
        result.setSize(menuPage.getSize());
        result.setCurrent(menuPage.getCurrent());
        result.setPages(menuPage.getPages());
        result.setRecords(menuPage.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList()));

        return result;
    }

    public MenuVO getById(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null || menu.getDeleted() == 1) {
            throw new IllegalArgumentException("菜单不存在");
        }
        return convertToVO(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(MenuDTO dto) {
        Menu existMenu = menuMapper.selectOne(
            new LambdaQueryWrapper<Menu>()
                .eq(Menu::getMenuCode, dto.getMenuCode())
                .eq(Menu::getDeleted, 0)
        );

        if (existMenu != null) {
            throw new IllegalArgumentException("菜单编码已存在");
        }

        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        menu.setVisible(dto.getVisible() != null ? dto.getVisible() : 1);
        menu.setSort(dto.getSort() != null ? dto.getSort() : 0);
        menu.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);

        menuMapper.insert(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(MenuDTO dto) {
        Menu menu = menuMapper.selectById(dto.getId());
        if (menu == null || menu.getDeleted() == 1) {
            throw new IllegalArgumentException("菜单不存在");
        }

        Menu existMenu = menuMapper.selectOne(
            new LambdaQueryWrapper<Menu>()
                .eq(Menu::getMenuCode, dto.getMenuCode())
                .ne(Menu::getId, dto.getId())
                .eq(Menu::getDeleted, 0)
        );

        if (existMenu != null) {
            throw new IllegalArgumentException("菜单编码已存在");
        }

        BeanUtils.copyProperties(dto, menu);
        menuMapper.updateById(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null || menu.getDeleted() == 1) {
            throw new IllegalArgumentException("菜单不存在");
        }

        Long childCount = menuMapper.selectCount(
            new LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, id)
                .eq(Menu::getDeleted, 0)
        );

        if (childCount > 0) {
            throw new IllegalArgumentException("存在子菜单，无法删除");
        }

        menuMapper.deleteById(id);
    }

    private List<MenuVO> buildTree(List<Menu> menus, Long parentId) {
        List<MenuVO> tree = new ArrayList<>();

        for (Menu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                MenuVO vo = convertToVO(menu);
                vo.setChildren(buildTree(menus, menu.getId()));
                tree.add(vo);
            }
        }

        return tree;
    }

    private List<RouterVO> buildRouters(List<Menu> menus, Long parentId) {
        List<RouterVO> routers = new ArrayList<>();

        for (Menu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                RouterVO router = new RouterVO();
                router.setId(menu.getId());
                router.setPath(menu.getPath());
                router.setName(menu.getMenuCode());
                router.setComponent(menu.getComponent() != null ? menu.getComponent() : "Layout");

                RouterVO.Meta meta = new RouterVO.Meta();
                meta.setTitle(menu.getMenuName());
                meta.setIcon(menu.getIcon());
                meta.setHidden(menu.getVisible() != 1);
                meta.setKeepAlive(true);
                router.setMeta(meta);

                List<RouterVO> children = buildRouters(menus, menu.getId());
                if (!children.isEmpty()) {
                    router.setChildren(children);
                }

                routers.add(router);
            }
        }

        return routers;
    }

    private MenuVO convertToVO(Menu menu) {
        MenuVO vo = new MenuVO();
        BeanUtils.copyProperties(menu, vo);
        return vo;
    }
}