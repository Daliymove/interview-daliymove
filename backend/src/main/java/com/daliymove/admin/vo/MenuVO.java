package com.daliymove.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MenuVO {

    private Long id;
    private String menuName;
    private String menuCode;
    private Long parentId;
    private String path;
    private String component;
    private String icon;
    private Integer menuType;
    private Integer visible;
    private Integer status;
    private Integer sort;
    private List<MenuVO> children;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}