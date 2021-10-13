/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package ${baseParentPackage}.feign;

import org.springframework.web.bind.annotation.RestController;
import ${packageName}.${feignClassName};
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
<#if hasSlf4j>
import lombok.extern.slf4j.Slf4j;
</#if>
<#list feignProviderImport as fpi>
import ${fpi};
</#list>
<#if hasBean>
<#list newSdkBeanImport as nsbs>
import ${nsbs};
</#list>
<#else>
<#list sdkBeanSet as sbs>
import ${sbs};
</#list>
</#if>
/**
<#if classDescription??>
 * ${classDescription?replace('\"','')}的Feign接口提供者trim
<#else>
 * ${feignClassName}的Feign接口提供者
</#if>
 *
 * @author NCIT
 * @date ${.now?string("yyyy/MM/dd HH:mm:ss")}
 */
<#if hasSlf4j>
@Slf4j
</#if>
@RestController
@Api(tags = "${classDescription?replace('\"','')}")
public class ${feignProviderClassName} implements ${feignClassName} {

<#list fields as f>
    ${f}
</#list>

<#list methods as m>
    /**
    <#if m.methodDescription??&&m.methodDescription!="">
     * ${m.methodDescription?trim?replace('\"','')}
    <#else>
     * TODO 请书写注释
    </#if>
     *
     * @return
     */
    @ApiOperation(value = "${m.methodDescription?trim?replace('\"','')}")
    @Override
    public ${m.returnType} ${m.name} ${m.body}

</#list>
}
