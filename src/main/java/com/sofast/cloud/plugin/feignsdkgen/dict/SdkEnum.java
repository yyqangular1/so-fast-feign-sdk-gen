/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package com.sofast.cloud.plugin.feignsdkgen.dict;

/**
 * SDK 目录枚举
 *
 * @Date : 2021/9/26 9:00 PM
 * @Author : NCIT
 */
public enum SdkEnum {
    /**
     * sdk常量
     */
    CONSTANTS("constants"),
    /**
     * entity
     */
    ENTITY("entity"),
    /**
     * domain
     */
    DOMAIN("domain"),
    /**
     * dto
     */
    DTO("dto"),
    /**
     * vo
     */
    VO("vo"),
    /**
     * sdk fallback
     */
    FALLBACK("fallback"),
    /**
     * factory
     */
    FACTORY("factory"),
    /**
     * feign interface
     */
    FEIGN("feign");

    private String name;

    SdkEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
