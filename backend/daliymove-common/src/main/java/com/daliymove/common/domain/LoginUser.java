package com.daliymove.common.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUser implements Serializable {

    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String token;
}