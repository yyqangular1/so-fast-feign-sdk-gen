/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package com.sofast.cloud.plugin.feignsdkgen.model;

import lombok.Data;

/**
 * 方法模型
 *
 * @author NCIT.SOL1.YYQ
 * @date : 2021/9/26 9:00 PM
 */
@Data
public class MethodModel {

  private String name;

  private String returnType;

  private String url;

  private String httpMethod;

  private String methodAnnotation;

  private String methodMappingPathPrefix;

  private String body;

  private String methodDescription;


}
