package com.daliymove.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionDTO {

    private Long id;

    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    private Integer resourceType;

    private String resourceUrl;

    private String method;

    private Long parentId;

    private String description;

    private Integer status;

    private Integer sort;
}