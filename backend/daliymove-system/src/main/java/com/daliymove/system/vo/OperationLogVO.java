package com.daliymove.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogVO {

    private Long id;

    private Long userId;

    private String username;

    private String operation;

    private String method;

    private String params;

    private String ip;

    private Integer status;

    private String errorMsg;

    private Integer executeTime;

    private LocalDateTime createTime;
}