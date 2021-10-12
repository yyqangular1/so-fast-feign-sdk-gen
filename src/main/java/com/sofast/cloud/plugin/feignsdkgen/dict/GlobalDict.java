/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package com.sofast.cloud.plugin.feignsdkgen.dict;

import java.util.Arrays;
import java.util.List;

/**
 * 全局字典
 *
 * @author NCIT.SOL1.YYQ
 * @date : 2021/9/26 9:00 PM
 */
public interface GlobalDict {
    /**
     * 标题信息
     */
    String TITLE_INFO = "SoFast框架 Feign Client生成器";

    String SOFAST_PREFIX = "so-fast-";

    String FEIGN_SUFFIX = "Feign";
    String FALLBACK_SUFFIX = "Fallback";
    String FACTORY_SUFFIX = "FallbackFactory";
    String PROVIDER_SUFFIX = "Provider";

    String ANNOTATION_GET = "org.springframework.web.bind.annotation.GetMapping";
    String ANNOTATION_POST = "org.springframework.web.bind.annotation.PostMapping";
    String ANNOTATION_REQUEST = "org.springframework.web.bind.annotation.RequestMapping";
    String ANNOTATION_REQUEST_METHOD = "org.springframework.web.bind.annotation.RequestMethod";
    String SWAGGER_API = "io.swagger.annotations.Api";
    String SWAGGER_API_OPERATOR = "io.swagger.annotations.ApiOperation";

    /**
     * java.lang wrapper类型，无需import
     */
    List<String> JAVA_GENERAL_TYPE = Arrays.asList("java.lang.Boolean",
            "java.lang.String",
            "java.lang.Character",
            "java.lang.Short",
            "java.lang.Integer",
            "java.lang.Long",
            "java.lang.Float",
            "java.lang.Double",
            "java.lang.Byte",
            "java.lang.Object");

}
