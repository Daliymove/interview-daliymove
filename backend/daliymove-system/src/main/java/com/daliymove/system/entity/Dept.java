package com.daliymove.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_dept")
public class Dept {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String deptName;

    private Long parentId;

    private String leader;

    private String phone;

    private String email;

    private Integer status;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}