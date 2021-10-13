/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package com.sofast.cloud.plugin.feignsdkgen.ui;

import com.google.common.base.CaseFormat;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassImpl;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import com.sofast.cloud.plugin.feignsdkgen.dict.GlobalDict;
import com.sofast.cloud.plugin.feignsdkgen.dto.GenerateOptions;
import com.sofast.cloud.plugin.feignsdkgen.service.GenerateService;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

/**
 * SDK生成器
 */
public class MethodListDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    //    private JTextField textField2;
    private JTextField textField3;
    private JTextField tfAppName;
    private JLabel lblFile;
    private JLabel lblPackage;
    private JList methodList;
    private JCheckBox chkBean;
    private JCheckBox chkDTO;
    private JCheckBox chkVO;
    private JPanel JPanelBody;
    private JPanel JpFeignClient;
    private JPanel JpPackageName;
    private JPanel JpListTitle;
    private JPanel JpList;
    private JPanel JpBeanType;
    private JPanel JpBeanTypeTitle;
    private JPanel JpAppName;
    private JComboBox comboBox1;
    private JCheckBox FallbackCheckBox;
    private JCheckBox FallbackFactoryCheckBox;
    private JPanel FeignHystrix;
    private JRadioButton rdoHasProvider;
    private JRadioButton Slf4JRadioButton;
    private JPanel JpFilename;
    private JButton btnSelectPackage;
    private String hidPath;
    private String baseParentPath;
    private String baseParentPackage;
    /**
     * Controller前缀，作为feign constants的命名前缀
     */
    private String applicationNamePrefix;
    /**
     * 类文件的requestMapping
     */
    private String classMappingPath;
    /**
     * requestMapping annotation的import完成路径
     */
    private String qualifiedName;

    private List<String> methodUrlList = new ArrayList<>();

//    private Map<Integer, Set<String>> returnTypeList = new HashMap<>();

    private List<PsiField> ownFields;

    private GenerateService generateService;
    private Project project;

    private String classDescription = "";

    public MethodListDialog(@Nullable Project project, AnActionEvent e) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.project = project;

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        // 获取当前操作的类文件
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        // 获取当前操作类文件的文件名
        String className = psiFile.getVirtualFile().getNameWithoutExtension();

        // 文件名
        textField1.setText(className.endsWith("Controller") ? "I" + className.replace("Controller", GlobalDict.FEIGN_SUFFIX) : "I" + className + GlobalDict.FEIGN_SUFFIX);
        // 目录
        baseParentPackage = JavaDirectoryService.getInstance().getPackage(e.getRequiredData(LangDataKeys.IDE_VIEW).getOrChooseDirectory()).getParentPackage().getQualifiedName();
        textField3.setText(baseParentPackage + ".sdk");
        // 微服务名
        tfAppName.setText(e.getDataContext().getData(LangDataKeys.MODULE).getName());
        applicationNamePrefix = tfAppName.getText().replace(GlobalDict.SOFAST_PREFIX, "").replace("-", "_");

        // 路径
        baseParentPath = psiFile.getParent().getParentDirectory().getVirtualFile().getPath();
        hidPath = baseParentPath + File.separator + "sdk";

        List<MethodListItem> list = new ArrayList<MethodListItem>();

        int i = 0;
        for (PsiElement psiElement : psiFile.getChildren()) {
            if (psiElement instanceof PsiClassImpl) {
                PsiClassImpl psiClass = (PsiClassImpl) psiElement;

                // 获取类的mapping
                PsiAnnotation annotation = psiClass.getAnnotation(GlobalDict.ANNOTATION_GET);
                if (annotation != null) {
                    // annotation的import完整包名
                    qualifiedName = annotation.getQualifiedName();
                    // 从annotation中获取的value两侧带有引号，所以需要剔除
                    classMappingPath = annotation.findAttributeValue("value").getText().replace("\"", "");
                }

                // 获取类的注释
                PsiAnnotation swaggerAnno = psiClass.getAnnotation(GlobalDict.SWAGGER_API);
                if (swaggerAnno != null) {
                    try {
                        classDescription = swaggerAnno.findAttributeValue("value").getText();
                    } catch (Exception ex) {
                    }
                }
                if (StringUtil.isEmpty(classDescription)) {
                    PsiDocTag clzDocTag = psiClass.getDocComment().findTagByName("Description");
                    if (clzDocTag == null) {
                        clzDocTag = psiClass.getDocComment().findTagByName("description");
                    }
                    if (clzDocTag != null) {
                        classDescription = clzDocTag.getValueElement().getText();
                    } else {
                        if (psiClass.getDocComment() != null) {
                            for (PsiElement descriptionElement : psiClass.getDocComment().getDescriptionElements()) {
                                if (descriptionElement instanceof PsiDocToken && !descriptionElement.getText().startsWith("<")) {
                                    classDescription += descriptionElement.getText();
                                }
                            }
                        }
                    }
                }

                // 获取所有方法
                PsiMethod[] methods = psiClass.getMethods();
                // 获取所有字段
                ownFields = psiClass.getOwnFields();

                for (PsiMethod method : methods) {
                    PsiModifierList modifierList = method.getModifierList();
                    String text = modifierList.getLastChild().getText();

                    String methodType = "GET";
                    String methodAnnotation = "GetMapping";
                    // 方法的requestmapping
                    PsiAnnotation methodAnno = modifierList.findAnnotation(GlobalDict.ANNOTATION_GET);
                    String methodMappingPath = "";
                    String methodMappingPathConst = "";
                    if (methodAnno == null) {
                        methodAnno = modifierList.findAnnotation(GlobalDict.ANNOTATION_POST);
                        methodType = "POST";
                        methodAnnotation = "PostMapping";
                        if (methodAnno == null) {
                            methodAnno = modifierList.findAnnotation(GlobalDict.ANNOTATION_REQUEST);
                            if (methodAnno != null) {
                                methodType = methodAnno.findAttributeValue("method").getLastChild().getText();
                                methodAnnotation = "RequestMapping";
                            }
                        }
                    }
                    if (methodAnno != null) {
                        methodMappingPath = methodAnno.findAttributeValue("value").getText().replace("\"", "");
                        if (StringUtil.isNotEmpty(methodMappingPath) && methodMappingPath.endsWith("}")) {
                            methodMappingPathConst = methodMappingPath.substring(0, methodMappingPath.lastIndexOf("/"));
                        } else {
                            methodMappingPathConst = methodMappingPath;
                        }
                    }

                    // 方法名
                    PsiIdentifier nameIdentifier = method.getNameIdentifier();
                    String text1 = nameIdentifier.getText();

                    // 方法的返回值
                    PsiType returnType = method.getReturnType();
//                    String canonicalText1 = method.getReturnType().getCanonicalText();
                    String canonicalText1 = method.getReturnType().getPresentableText();

                    // 方法的参数
                    PsiParameterList parameterList = method.getParameterList();
                    String canonicalText = parameterList.getText();

                    String name = method.getName();
                    // 只有public方法才可以生成
                    if ("public".equals(text)) {
                        MethodListItem item = new MethodListItem();
                        item.setId(i);
                        item.setValue(name + canonicalText + ":" + canonicalText1);
                        item.setMethod(methodType);
                        item.setMethodAnnotation(methodAnnotation);
                        item.setMethodMappingPathConst(methodMappingPathConst);
                        item.setMethodMappingPath(methodMappingPath);
                        // 返回值的import
                        item.setImportSet(getReturnTypeList(method).get("returnTypeList"));
                        item.setSdkBeanSet(getReturnTypeList(method).get("sdkBeanSet"));
                        // 参数的import
                        item.getImportSet().addAll(getParamTypeList(method).get("paramTypeList"));
                        item.getSdkBeanSet().addAll(getParamTypeList(method).get("sdkBeanSet"));
                        item.setBody(method.getBody().getText());

                        String methodDescription = "";
                        PsiAnnotation methodDescriptionAnno = method.getAnnotation(GlobalDict.SWAGGER_API_OPERATOR);
                        if (methodDescriptionAnno != null) {
                            try {
                                methodDescription = methodDescriptionAnno.findAttributeValue("value").getText();
                            } catch (Exception ex) {
                            }
                        }
                        if (StringUtil.isEmpty(methodDescription)) {
                            if (method.getDocComment() != null) {
                                for (PsiElement descriptionElement : method.getDocComment().getDescriptionElements()) {
                                    if (descriptionElement instanceof PsiDocToken && !descriptionElement.getText().startsWith("<")) {
                                        methodDescription += descriptionElement.getText();
                                    }
                                }
                            }
                        }

                        item.setMethodDescription(methodDescription);
                        list.add(item);
                        // 只有public的方法才计算
                        methodUrlList.add(methodMappingPath);
                        i++;
                    }
                }
            }
        }

        methodList.setListData(list.toArray());
        methodList.setCellRenderer(new FeignListCellRenderer());

        btnSelectPackage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PackageChooserDialog selector = new PackageChooserDialog("请选择SDK包路径", project);
                selector.selectPackage(textField3.getText());
                selector.show();
                PsiPackage selectedPackage = selector.getSelectedPackage();
                if (selectedPackage != null) {
                    textField3.setText(selectedPackage.getQualifiedName());
                }
            }
        });
    }

    private Map<String, Set<String>> getReturnTypeList(PsiMethod method) {
        if (null == method) {
            return new HashMap<String, Set<String>>() {
                {
                    put("returnTypeList", new HashSet<>());
                    put("sdkBeanSet", new HashSet<>());
                }

            };
        }
        PsiType returnType = method.getReturnType();
        Set<String> returnTypeList = new HashSet<>(5);
        Set<String> sdkBeanSet = new HashSet<>(5);
        buildMethodReturnAndParamType(returnType, returnTypeList, sdkBeanSet);
        Map<String, Set<String>> retMap = new HashMap<>(2);
        retMap.put("returnTypeList", returnTypeList);
        retMap.put("sdkBeanSet", sdkBeanSet);
        return retMap;
    }

    /**
     * 获取参数的类型
     *
     * @param method
     * @return
     */
    private Map<String, Set<String>> getParamTypeList(PsiMethod method) {

        if (null == method || method.getParameterList().isEmpty()) {
            return new HashMap<String, Set<String>>() {
                {
                    put("paramTypeList", new HashSet<>());
                    put("sdkBeanSet", new HashSet<>());
                }

            };
        }
        PsiParameterList parameterList = method.getParameterList();
        Set<String> paramTypeList = new HashSet<>(5);
        Set<String> sdkBeanSet = new HashSet<>(5);
        for (PsiParameter parameter : parameterList.getParameters()) {
            // 注解类型
            PsiAnnotation[] annotations = parameter.getAnnotations();
            for (PsiAnnotation annotation : annotations) {
                paramTypeList.add(annotation.getQualifiedName());
            }
            buildMethodReturnAndParamType(parameter.getType(), paramTypeList, sdkBeanSet);
        }
        Map<String, Set<String>> retMap = new HashMap<>(2);
        retMap.put("paramTypeList", paramTypeList);
        retMap.put("sdkBeanSet", sdkBeanSet);

        return retMap;
    }

    /**
     * 获取参数和返回值的import class
     *
     * @param targetType 目标类型
     * @param set        如果该类型为非sdk的bean，则放入通用引入类型中，只用于import，而不进行bean迁移
     * @param sdkBeanSet 如果该类型为sdk下的bean，则放入sdk引入类型中，需要进行bean迁移，便于打包SDK
     */
    private void buildMethodReturnAndParamType(PsiType targetType, Set<String> set, Set<String> sdkBeanSet) {
        if (targetType instanceof PsiClassReferenceType) {
            PsiClassReferenceType type = (PsiClassReferenceType) targetType;
            // 获取主返回类型
            if (isWrapImport(type)) {
                // 判断是否为本工程的javabean
                if (type.rawType().getCanonicalText().startsWith(baseParentPackage)) {
                    sdkBeanSet.add(type.rawType().getCanonicalText());
                } else {
                    set.add(type.rawType().getCanonicalText());
                }
            }
            // 获取泛型
            if (type.hasParameters()) {
                PsiType[] parameters = type.getParameters();
                // 处理是 List 的返回结果, 将List<T> 的泛型拿出来
                for (int i = 0; i < parameters.length; i++) {
                    buildMethodReturnAndParamType(parameters[i], set, sdkBeanSet);
//                    if (parameters[i] instanceof PsiClassReferenceType) {
//                        if (isWrapImport((PsiClassReferenceType) parameters[i])) {
//                            set.add(parameters[i].getCanonicalText());
//                        }
//                    }
                }
            }
        }
    }

    private static boolean isWrapImport(PsiClassReferenceType type) {

        if (GlobalDict.JAVA_GENERAL_TYPE.contains(type.getReference().getQualifiedName())) {
            return false;
        }

        return true;
    }

    private void onOK() {
        // 获取要生成的文件名
        String feignClzName = textField1.getText();
        // 设置Feign Constants的类名
        String constClzName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, applicationNamePrefix) + "Constants";

        // 设置Feign Fallback的类名
        String fallbackClzName = feignClzName.replace(GlobalDict.FEIGN_SUFFIX, "") + GlobalDict.FALLBACK_SUFFIX;
        if (fallbackClzName.startsWith("I")) {
            fallbackClzName = fallbackClzName.substring(1);
        }

        // 设置Feign Fallback Factory的类名
        String factoryClzName = feignClzName.replace(GlobalDict.FEIGN_SUFFIX, "") + GlobalDict.FACTORY_SUFFIX;
        if (factoryClzName.startsWith("I")) {
            factoryClzName = factoryClzName.substring(1);
        }
        // 设置FeignProvider的类名
        String feignProviderName = feignClzName.replace(GlobalDict.FEIGN_SUFFIX, "") + GlobalDict.PROVIDER_SUFFIX;
        if (feignProviderName.startsWith("I")) {
            feignProviderName = feignProviderName.substring(1);
        }

        // 获取超类名称
//        String superClzName = textField2.getText();
        // 获取包名
        String packageName = textField3.getText();
        // 获取要生成的方法
        List<MethodListItem> selectedValuesList = methodList.getSelectedValuesList();

        if (StringUtil.isEmpty(feignClzName)) {
            Messages.showWarningDialog("请输入文件名", GlobalDict.TITLE_INFO);
            return;
        }
        if (StringUtil.isEmpty(packageName)) {
            Messages.showWarningDialog("请输入包名", GlobalDict.TITLE_INFO);
            return;
        }
        if (selectedValuesList.isEmpty()) {
            Messages.showWarningDialog("请选择至少一个方法", GlobalDict.TITLE_INFO);
            return;
        }

        GenerateOptions options = new GenerateOptions();
        options.setClassDescription(classDescription);
        options.setClassMappingPath(classMappingPath);
        options.setQualifiedName(qualifiedName);
        options.setAppName(tfAppName.getText());
        options.setClassName(feignClzName);
        options.setClassPath(hidPath);
        options.setBaseParentPath(baseParentPath);
//        options.setSuperClassName(superClzName);
        options.setPackageName(packageName);
        options.setBaseParentPackage(baseParentPackage);
        options.setFeignConstantsClassName(constClzName);
        options.setFeignFallbackClassName(fallbackClzName);
        options.setFeignFactoryClassName(factoryClzName);
        options.setFeignProviderClassName(feignProviderName);

        options.setApplicationNamePrefix(applicationNamePrefix);
        options.setProject(this.project);
        options.setSelectedList(selectedValuesList);
        options.setHasBean(chkBean.isSelected());
//        options.setHasDTO(chkDTO.isSelected());
//        options.setHasVO(chkVO.isSelected());
        options.setHasProvider(rdoHasProvider.isSelected());
        options.setHasFallback(FallbackCheckBox.isSelected());
        options.setHasFactory(FallbackFactoryCheckBox.isSelected());
        options.setHasSlf4j(Slf4JRadioButton.isSelected());

        options.setFeignReturnVal(comboBox1.getSelectedIndex());

        options.setFields(ownFields);
        ;

        generateService = GenerateService.getInstance(this.project);
        // 创建包路径
        boolean isSuccess = generateService.generate(options);

        if (isSuccess) {
            dispose();
        } else {
            Messages.showErrorDialog("生成失败", GlobalDict.TITLE_INFO);
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
//        MethodListDialog dialog = new MethodListDialog();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
    }
}
