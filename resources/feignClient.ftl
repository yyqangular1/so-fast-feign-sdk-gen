/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package ${packageName};

import org.springframework.cloud.openfeign.FeignClient;
import ${feignConstantImport};
<#list importList as il>
import ${il};
</#list>
<#list methodAnnotationImport as mai>
import ${mai};
</#list>


/**
<#if classDescription??>
 * ${classDescription?replace('\"','')}的Feign接口
<#else>
 * ${feignClassName}的Feign接口
</#if>
 *
 * @author NCIT
 * @date ${.now?string("yyyy/MM/dd HH:mm:ss")}
 */
@FeignClient(contextId = "${feignClassName?uncap_first}", value = ${feignConstantsClassName}.FEIGN_${applicationNamePrefix}_SERVICE_ID)
public interface ${feignClassName} {

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
    <#if m.methodAnnotation == "RequestMapping">
    @${m.methodAnnotation}(value = ${feignConstantsClassName}.FEIGN${m.methodMappingPathPrefix?replace('\"','')}, method = RequestMethod.${m.httpMethod})
    <#else>
    @${m.methodAnnotation}(value = ${feignConstantsClassName}.FEIGN${m.methodMappingPathPrefix?replace('\"','')})
    </#if>
    ${m.returnType} ${m.name};

</#list>
}
