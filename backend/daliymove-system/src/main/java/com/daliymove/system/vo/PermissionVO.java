package com.daliymove.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PermissionVO {

    private Long id;
    private String permissionName;
    private String permissionCode;
    private Integer resourceType;
    private String resourceUrl;
    private String method;
    private Long parentId;
    private String description;
    private Integer status;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}