package com.daliymove.admin.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;

    private Integer status;

    private Long deptId;

    private List<Long> roleIds;

    private List<Long> permissionIds;

    private List<Long> menuIds;
}