package com.daliymove.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MenuDTO {

    private Long id;

    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    @NotBlank(message = "菜单编码不能为空")
    private String menuCode;

    private Long parentId;

    private String path;

    private String component;

    private String icon;

    private Integer menuType;

    private Integer visible;

    private Integer status;

    private Integer sort;
}