/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package com.sofast.cloud.plugin.feignsdkgen.utils;

import com.sofast.cloud.plugin.feignsdkgen.model.ClassModel;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * freemarker模板处理器
 *
 * @author NCIT.SOL1.YYQ
 * @date 2021/9/29 7:04 PM
 */
public class TemplateProcessor {

    private static Configuration freeMarkerConfig = new Configuration(
            new Version("2.3.0"));

    private static Template feignClientTemplate;

    private static Template feignProviderTemplate;

    private static Template feignConstantsTemplate;

    private static Template feignFallbackTemplate;

    private static Template feignFactoryTemplate;

    static {
        try {
            initTemplate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initTemplate() throws IOException {

        StringTemplateLoader loader = new StringTemplateLoader();

        String templateStr = getTemplate("feignClient.ftl");
        loader.putTemplate("feignClient", templateStr);
        freeMarkerConfig.setTemplateLoader(loader);
        feignClientTemplate = freeMarkerConfig.getTemplate("feignClient");

        String providerTemplateStr = getTemplate("feignProvider.ftl");
        loader.putTemplate("feignProvider", providerTemplateStr);
        freeMarkerConfig.setTemplateLoader(loader);
        feignProviderTemplate = freeMarkerConfig.getTemplate("feignProvider");

        String constantsTemplateStr = getTemplate("feignConstants.ftl");
        loader.putTemplate("feignConstants", constantsTemplateStr);
        freeMarkerConfig.setTemplateLoader(loader);
        feignConstantsTemplate = freeMarkerConfig.getTemplate("feignConstants");

        String fallBackTemplateStr = getTemplate("feignFallBack.ftl");
        loader.putTemplate("feignFallBack", fallBackTemplateStr);
        freeMarkerConfig.setTemplateLoader(loader);
        feignFallbackTemplate = freeMarkerConfig.getTemplate("feignFallBack");

        String factoryTemplateStr = getTemplate("feignFactory.ftl");
        loader.putTemplate("feignFactory", factoryTemplateStr);
        freeMarkerConfig.setTemplateLoader(loader);
        feignFactoryTemplate = freeMarkerConfig.getTemplate("feignFactory");

    }

    public static String parseFeignClient(ClassModel classModel)
            throws IOException, TemplateException {
        StringWriter sw = new StringWriter();
        feignClientTemplate.process(classModel, sw);
        return sw.toString();
    }

    public static String parseFeignProvider(ClassModel classModel)
            throws IOException, TemplateException {
        StringWriter sw = new StringWriter();
        feignProviderTemplate.process(classModel, sw);
        return sw.toString();
    }

    public static String parseFeignConstants(ClassModel classModel)
            throws IOException, TemplateException {
        StringWriter sw = new StringWriter();
        feignConstantsTemplate.process(classModel, sw);
        return sw.toString();
    }

    public static String parseFeignClientFallBack(ClassModel classModel)
            throws IOException, TemplateException {
        StringWriter sw = new StringWriter();
        feignFallbackTemplate.process(classModel, sw);
        return sw.toString();
    }

    public static String parseFeignClientFactory(ClassModel classModel)
            throws IOException, TemplateException {
        StringWriter sw = new StringWriter();
        feignFactoryTemplate.process(classModel, sw);
        return sw.toString();
    }

    private static String getTemplate(String path) {

        StringBuilder builder = new StringBuilder();

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                TemplateProcessor.class.getClassLoader().getResourceAsStream(path),
                                "UTF-8"))
        ) {

            String line = reader.readLine();

            while (line != null) {
                builder.append(line).append("\n");
                line = reader.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return builder.toString();
    }
}
