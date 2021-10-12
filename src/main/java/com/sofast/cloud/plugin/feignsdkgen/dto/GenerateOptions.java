/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package com.sofast.cloud.plugin.feignsdkgen.dto;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiField;
import com.sofast.cloud.plugin.feignsdkgen.ui.MethodListItem;
import lombok.Data;

import java.util.List;

/**
 * TODO
 *
 * @Description: 参数
 * @Date : 2021/9/26 4:02 PM
 * @Author : NCIT
 */
@Data
public class GenerateOptions {

    private String classDescription;
    private String qualifiedName;
    private String classMappingPath;
    private String appName;
    private String className;
    private String superClassName;
    private String baseParentPackage;
    private String packageName;
    private String classPath;
    private String baseParentPath;
    private String feignConstantsClassName;
    private String feignFallbackClassName;
    private String feignFactoryClassName;
    private String feignProviderClassName;
    private String applicationNamePrefix;
    private boolean hasEntity;
    private boolean hasDTO;
    private boolean hasVO;
    private boolean hasProvider;
    private boolean hasFallback;
    private boolean hasFactory;
    private boolean hasSlf4j;
    private Project project;
    private List<MethodListItem> selectedList;
    /**
     * import feign constants 的完整包路径
     */
    private String feignConstantImport;

    /**
     * feign熔断后返回null，还是错误信息
     */
    private int feignReturnVal;

    /**
     * Feign Provider中需要引入的field
     */
    private List<PsiField> fields;

}
