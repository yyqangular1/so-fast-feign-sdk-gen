/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package com.sofast.cloud.plugin.feignsdkgen.ui;

import lombok.Data;

import java.util.Set;

/**
 * 自定义list cell
 *
 * @author : NCIT.SOL1.YYQ
 * @date : 2021/9/29 5:10 PM
 */
@Data
public class MethodListItem {

    private int id;
    private String value;
    private String method;
    private String methodAnnotation;
    private String methodDescription;

    /**
     * method mapping path
     */
    private String methodMappingPath;
    /**
     * 常量文件中用于定义方法的mappingPath的变量值，（因为在mappingPath中会带有"/{a}"这种参数，所以需要特殊处理）
     */
    private String methodMappingPathConst;

    /**
     * 方法返回值类型，包括泛型多层嵌套（包括参数的类型）
     */
    Set<String> importSet;

    /**
     * 方法体全部内容，包括双括号
     */
    private String body;


}
