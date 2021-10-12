package com.sofast.cloud.plugin.feignsdkgen.model;

import lombok.Data;

import java.util.ArrayList;
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

    private List<String> fields = new ArrayList<>(5);

    private Set<String> feignProviderImport;

    private boolean hasSlf4j;
}
