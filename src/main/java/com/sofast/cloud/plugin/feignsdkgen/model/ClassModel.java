/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package com.sofast.cloud.plugin.feignsdkgen.model;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 类模型
 *
 * @author NCIT.SOL1.YYQ
 * @date : 2021/9/26 9:00 PM
 */
@Data
public class ClassModel {

    private String baseParentPackage;
    private String classDescription;
    private String qualifiedName;
    private String classMappingPath;
    private String applicationName;

    private String packageName;

    private String feignClassName;

    private String url;

    private List<MethodModel> methods;

    /**
     * 包括returnTypeList和ParameterTypeList
     */
    private Set<String> importList;

    private Set<String> sdkBeanSet;

    /**
     * GetMapping/PostMapping/RequestMapping的import
     */
    private Set<String> methodAnnotationImport;

    private String applicationNamePrefix;

    private String feignConstantsClassName;
    private String feignFallbackClassName;
    private String feignFactoryClassName;
    private String feignProviderClassName;
    private String feignConstantImport;

    private int feignReturnVal;

    private Set<String> fields = new HashSet<>(5);

    private Set<String> feignProviderImport;

    private boolean hasSlf4j;

    private Set<String> newSdkBeanImport;
    private boolean hasBean;
}
