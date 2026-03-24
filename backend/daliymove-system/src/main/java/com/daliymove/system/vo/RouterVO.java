package com.daliymove.system.vo;

import lombok.Data;

import java.util.List;

@Data
public class RouterVO {

    private Long id;
    private String path;
    private String name;
    private String component;
    private String redirect;
    private Meta meta;
    private List<RouterVO> children;

    @Data
    public static class Meta {
        private String title;
        private String icon;
        private Boolean hidden;
        private Boolean keepAlive;
    }
}