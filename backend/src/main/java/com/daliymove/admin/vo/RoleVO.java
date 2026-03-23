package com.daliymove.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleVO {

    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    private List<Long> permissionIds;
    private List<Long> menuIds;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}