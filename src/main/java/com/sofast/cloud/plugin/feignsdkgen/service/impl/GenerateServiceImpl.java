/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package com.sofast.cloud.plugin.feignsdkgen.service.impl;

import com.intellij.ide.util.PackageUtil;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.impl.source.tree.java.PsiPackageStatementImpl;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.sofast.cloud.plugin.feignsdkgen.dict.GlobalDict;
import com.sofast.cloud.plugin.feignsdkgen.dict.SdkEnum;
import com.sofast.cloud.plugin.feignsdkgen.dto.GenerateOptions;
import com.sofast.cloud.plugin.feignsdkgen.model.ClassModel;
import com.sofast.cloud.plugin.feignsdkgen.model.MethodModel;
import com.sofast.cloud.plugin.feignsdkgen.service.GenerateService;
import com.sofast.cloud.plugin.feignsdkgen.utils.TemplateProcessor;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO
 *
 * @Description:
 * @Date : 2021/9/26 3:29 PM
 * @Author : NCIT
 */
public class GenerateServiceImpl implements GenerateService {

    private String feignConstantImport;
    private Project project;
    private ClassModel __fcc;

    private VirtualFile entityVf = null, domainVf = null, voVf = null, dtoVf = null;

    /**
     * 如果将bean生成到sdk的包中，那么import需要修改微新生成的bean package路径
     */
    private Set<String> newSdkBeanImport = new HashSet<>(5);

    public GenerateServiceImpl(Project project) {
        this.project = project;
    }

    @Override
    public boolean generate(GenerateOptions options) {

        newSdkBeanImport.clear();
        entityVf = null;
        domainVf = null;
        voVf = null;
        dtoVf = null;

        Application applicationManager = ApplicationManager.getApplication();
        applicationManager.runWriteAction(() -> {

            // 创建基础目录
//                VirtualFile sdkVf = VfsUtil.createDirectoryIfMissing(options.getClassPath());
//                VirtualFile sdkVf = VfsUtil.createDirectoryIfMissing(options.getPackageName());
//            PsiDirectory dir = PackageUtil.findOrCreateDirectoryForPackage(options.getModule(), options.getPackageName(), (PsiDirectory) null, false);
            PsiDirectory dir = PackageUtil.findOrCreateDirectoryForPackage(this.project, options.getPackageName(), (PsiDirectory) null, false);
            VirtualFile sdkVf = dir.getVirtualFile();

            __fcc = createFeignClientClass(options);

            // ================= 生成constants文件
            generateConstants(options, sdkVf);
            // ================= 生成constants文件

            // ================= 生成entity文件
            generateEntity(options, sdkVf);
            // ================= 生成entity文件

            // ================= 生成dto文件
            generateDTO(options, sdkVf);
            // ================= 生成dto文件

            // ================= 生成vo文件
            generateVO(options, sdkVf);
            // ================= 生成vo文件

            // ================= 生成Feign接口文件
            generateFeign(options, sdkVf);
            // ================= 生成Feign接口文件

            // ================= 生成Feign Provider文件
            generateFeignProvider(options, sdkVf);
            // ================= 生成Feign Provider文件

            // ================= 生成fallback文件
            generateFallback(options, sdkVf);
            // ================= 生成fallback文件

            // ================= 生成Factory文件
            generateFactory(options, sdkVf);
            // ================= 生成Factory文件


        });


        return true;
    }

    private ClassModel createFeignClientClass(GenerateOptions options) {

        // 创建Java文件
        ClassModel classModel = new ClassModel();
        classModel.setBaseParentPackage(options.getBaseParentPackage());
        classModel.setClassDescription(options.getClassDescription());
        classModel.setQualifiedName(options.getQualifiedName());
        classModel.setClassMappingPath(options.getClassMappingPath());
        classModel.setApplicationName(options.getAppName());
        classModel.setPackageName(options.getPackageName() + "." + SdkEnum.FEIGN.getName());
        classModel.setApplicationNamePrefix(options.getApplicationNamePrefix().toUpperCase());
        classModel.setFeignConstantsClassName(options.getFeignConstantsClassName());
        classModel.setFeignFallbackClassName(options.getFeignFallbackClassName());
        classModel.setFeignFactoryClassName(options.getFeignFactoryClassName());
        classModel.setFeignProviderClassName(options.getFeignProviderClassName());
        classModel.setFeignConstantImport(options.getFeignConstantImport());
        classModel.setFeignConstantImport(feignConstantImport);
        classModel.setFeignReturnVal(options.getFeignReturnVal());
        classModel.setHasSlf4j(options.isHasSlf4j());
        classModel.setHasBean(options.isHasBean());

        // 文件名
        classModel.setFeignClassName(options.getClassName());
        // TODO url
        classModel.setUrl(StringUtil.isEmpty(options.getClassMappingPath()) ? "" : options.getClassMappingPath());
        // 方法
        Set<String> importList = new HashSet<>();
        Set<String> sdkImportList = new HashSet<>();
        List<MethodModel> methodList = new ArrayList<MethodModel>();
        Set<String> methodAnnotationImport = new HashSet<>(3);

        options.getSelectedList().forEach(m -> {
            MethodModel method = new MethodModel();
            method.setReturnType(m.getValue().split(":")[1]);
            method.setName(m.getValue().split(":")[0]);
            method.setHttpMethod(m.getMethod());
            method.setUrl(m.getMethodMappingPath());
            method.setMethodMappingPathPrefix((classModel.getUrl().replace("/", "_") + m.getMethodMappingPathConst().replace("/", "_")).toUpperCase());
            if (method.getMethodMappingPathPrefix().endsWith("_")) {
                method.setMethodMappingPathPrefix(method.getMethodMappingPathPrefix().substring(0, method.getMethodMappingPathPrefix().length() - 1));
            }
            method.setMethodAnnotation(m.getMethodAnnotation());
            method.setBody(m.getBody());
            // 判断方法中使用了哪些service
            Set<String> providerImport = new HashSet<>(5);
            for (PsiField field : options.getFields()) {
                if (m.getBody().contains(field.getName())) {
                    classModel.getFields().add(field.getText());
                    // Provider类的import获取
                    for (PsiAnnotation annotation : field.getAnnotations()) {
                        providerImport.add(annotation.getQualifiedName());
                    }
                    providerImport.add(field.getType().getCanonicalText());

                }
            }

            method.setMethodDescription(m.getMethodDescription());
            methodList.add(method);
            importList.addAll(m.getImportSet());
            sdkImportList.addAll(m.getSdkBeanSet());

            // Provider类的import单独处理
            providerImport.addAll(importList);
            classModel.setFeignProviderImport(providerImport);


            // 注解的import
            if ("GetMapping".equals(m.getMethodAnnotation())) {
                methodAnnotationImport.add(GlobalDict.ANNOTATION_GET);
            } else if ("PostMapping".equals(m.getMethodAnnotation())) {
                methodAnnotationImport.add(GlobalDict.ANNOTATION_POST);
            } else {
                methodAnnotationImport.add(GlobalDict.ANNOTATION_REQUEST);
                methodAnnotationImport.add(GlobalDict.ANNOTATION_REQUEST_METHOD);
            }
        });
        classModel.setMethods(methodList);
        classModel.setImportList(importList);
        classModel.setSdkBeanSet(sdkImportList);
        classModel.setMethodAnnotationImport(methodAnnotationImport);


        return classModel;
    }

    /**
     * 生成Feign常量文件
     *
     * @param options
     * @param virtualFile
     */
    private void generateConstants(GenerateOptions options, VirtualFile virtualFile) {

        try {
            // 创建SDK目录
            VirtualFile constantVf = VfsUtil.createDirectoryIfMissing(virtualFile, SdkEnum.CONSTANTS.getName());
            // 自动生成的Feign Constants文件包路径，用于在FeignClient中引入（所以常量文件必须在Feign Client文件之前）
            feignConstantImport = options.getPackageName() + "." + SdkEnum.CONSTANTS.getName() + "." + options.getFeignConstantsClassName();
            __fcc.setFeignConstantImport(feignConstantImport);
            String feignConstants = TemplateProcessor.parseFeignConstants(__fcc);
            VirtualFile feign = constantVf.findOrCreateChildData(constantVf, options.getFeignConstantsClassName() + ".java");
            feign.setBinaryContent(feignConstants.getBytes("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成Feign接口文件
     *
     * @param options
     * @param sdkVf
     */
    private void generateFeign(GenerateOptions options, VirtualFile sdkVf) {
        try {
            // 将迁移后sdk的javabean package路径引入
            __fcc.setNewSdkBeanImport(newSdkBeanImport);

            VirtualFile feignVf = VfsUtil.createDirectoryIfMissing(sdkVf, SdkEnum.FEIGN.getName());
            String feignClient = TemplateProcessor.parseFeignClient(__fcc);
            VirtualFile feign = feignVf.findOrCreateChildData(feignVf, options.getClassName() + ".java");
            feign.setBinaryContent(feignClient.getBytes("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

    }

    /**
     * 生成Feign Provider文件
     *
     * @param options
     * @param sdkVf
     */
    private void generateFeignProvider(GenerateOptions options, VirtualFile sdkVf) {

        try {
            if (!options.isHasProvider()) {
                return;
            }
            // Provider的生成目录与其他文件不同，Provider不属于SDK的内容，是服务提供者
            VirtualFile providerVf = VfsUtil.createDirectoryIfMissing(VfsUtil.createDirectoryIfMissing(options.getBaseParentPath()), "feign");
            String feignProvider = TemplateProcessor.parseFeignProvider(__fcc);
            VirtualFile feign = providerVf.findOrCreateChildData(providerVf, options.getFeignProviderClassName() + ".java");
            feign.setBinaryContent(feignProvider.getBytes("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

    }

    /**
     * 生成fallback文件
     *
     * @param options
     * @param sdkVf
     */
    private void generateFallback(GenerateOptions options, VirtualFile sdkVf) {
        try {
            if (!options.isHasFallback()) {
                return;
            }
            VirtualFile fallbackVf = VfsUtil.createDirectoryIfMissing(sdkVf, SdkEnum.FALLBACK.getName());
            String feignFallback = TemplateProcessor.parseFeignClientFallBack(__fcc);
            VirtualFile feign = fallbackVf.findOrCreateChildData(fallbackVf, options.getFeignFallbackClassName() + ".java");
            feign.setBinaryContent(feignFallback.getBytes("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    private void generateFactory(GenerateOptions options, VirtualFile sdkVf) {
        try {
            if (!options.isHasFactory()) {
                return;
            }
            VirtualFile factoryVf = VfsUtil.createDirectoryIfMissing(sdkVf, SdkEnum.FACTORY.getName());
            String feignFactory = TemplateProcessor.parseFeignClientFactory(__fcc);
            VirtualFile feign = factoryVf.findOrCreateChildData(factoryVf, options.getFeignFactoryClassName() + ".java");
            feign.setBinaryContent(feignFactory.getBytes("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    private void generateEntity(GenerateOptions options, VirtualFile sdkVf) {

        if (!options.isHasBean()) {
            return;
        }

        try {

            for (String s : __fcc.getSdkBeanSet()) {
                createBean(s, s.toUpperCase().endsWith("VO") || s.toUpperCase().endsWith("DTO"), options, sdkVf, SdkEnum.ENTITY);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateDTO(GenerateOptions options, VirtualFile sdkVf) {

        if (!options.isHasBean()) {
            return;
        }

        try {
            for (String s : __fcc.getSdkBeanSet()) {
                createBean(s, !s.toUpperCase().endsWith("DTO"), options, sdkVf, SdkEnum.DTO);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateVO(GenerateOptions options, VirtualFile sdkVf) {
        if (!options.isHasBean()) {
            return;
        }
        try {
            for (String s : __fcc.getSdkBeanSet()) {
                createBean(s, !s.toUpperCase().endsWith("VO"), options, sdkVf, SdkEnum.VO);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createBean(String s, boolean beanMatchPattern, GenerateOptions options, VirtualFile virtualFile, SdkEnum type) throws IOException {

        // TODO 判断是否符合dto class
        if (s.startsWith("java.") || s.startsWith("org.springframework")
                || s.startsWith("com.sofast.cloud.common.") || s.endsWith("R")
                || beanMatchPattern) {
            return;
        }

        if (s.startsWith("com.sofast.cloud.common.domain.vo.R")) {
            return;
        }

        PsiFile[] filesByName = FilenameIndex.getFilesByName(this.project, s.substring(s.lastIndexOf(".") + 1) + ".java", GlobalSearchScope.projectScope(project));
        if (filesByName != null && filesByName.length > 0) {
            // TODO 开始编辑文件
            for (PsiFile psiFile : filesByName) {
                // 存在多个同名文件时，包路径必须一致
                if (psiFile instanceof PsiJavaFileImpl && s.startsWith(((PsiJavaFileImpl) psiFile).getPackageName())) {
                    StringBuilder sb = new StringBuilder();
                    VirtualFile beanVf = null;
                    for (PsiElement child : psiFile.getChildren()) {
                        if (child instanceof PsiPackageStatementImpl) {

                            switch (type) {
                                case ENTITY:
                                    if (entityVf == null) {
                                        entityVf = VfsUtil.createDirectoryIfMissing(virtualFile, SdkEnum.ENTITY.getName());
                                    }
                                    beanVf = entityVf.findOrCreateChildData(entityVf, psiFile.getName());
                                    sb.append("package " + options.getPackageName() + "." + entityVf.getName() + ";");
                                    newSdkBeanImport.add(options.getPackageName() + "." + entityVf.getName() + "." + beanVf.getNameWithoutExtension());

                                    beanVf.setBinaryContent(sb.toString().getBytes("UTF-8"));
                                    break;
                                case DTO:
                                    if (dtoVf == null) {
                                        domainVf = VfsUtil.createDirectoryIfMissing(virtualFile, SdkEnum.DOMAIN.getName());
                                        dtoVf = VfsUtil.createDirectoryIfMissing(domainVf, SdkEnum.DTO.getName());
                                    }
                                    beanVf = dtoVf.findOrCreateChildData(dtoVf, psiFile.getName());
                                    sb.append("package " + options.getPackageName() + ".domain." + dtoVf.getName() + ";");
                                    newSdkBeanImport.add(options.getPackageName() + ".domain." + dtoVf.getName() + "." + beanVf.getNameWithoutExtension());

                                    beanVf.setBinaryContent(sb.toString().getBytes("UTF-8"));
                                    break;
                                case VO:
                                    if (voVf == null) {
                                        domainVf = VfsUtil.createDirectoryIfMissing(virtualFile, SdkEnum.DOMAIN.getName());
                                        voVf = VfsUtil.createDirectoryIfMissing(domainVf, SdkEnum.VO.getName());
                                    }
                                    beanVf = voVf.findOrCreateChildData(voVf, psiFile.getName());
                                    sb.append("package " + options.getPackageName() + ".domain." + voVf.getName() + ";");
                                    newSdkBeanImport.add(options.getPackageName() + ".domain." + voVf.getName() + "." + beanVf.getNameWithoutExtension());

                                    break;
                            }

                        } else {
                            sb.append(child.getText());
                        }
                    }
                    // 写文件
                    beanVf.setBinaryContent(sb.toString().getBytes("UTF-8"));
                }
            }
        }
    }

}
