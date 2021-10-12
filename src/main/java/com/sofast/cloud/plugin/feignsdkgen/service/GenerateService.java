/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package com.sofast.cloud.plugin.feignsdkgen.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.sofast.cloud.plugin.feignsdkgen.dto.GenerateOptions;
import org.jetbrains.annotations.NotNull;

/**
 * 代码生成
 *
 * @Description:
 * @Date : 2021/9/26 3:29 PM
 * @Author : NCIT
 */
public interface GenerateService {
    static GenerateService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, GenerateService.class);
    }

    boolean generate(GenerateOptions options);
}
