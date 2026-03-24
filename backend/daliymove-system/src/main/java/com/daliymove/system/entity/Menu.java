package com.daliymove.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class Menu {

    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}