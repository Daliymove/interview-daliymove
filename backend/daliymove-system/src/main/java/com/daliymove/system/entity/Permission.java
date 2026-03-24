package com.daliymove.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_permission")
public class Permission {

    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}