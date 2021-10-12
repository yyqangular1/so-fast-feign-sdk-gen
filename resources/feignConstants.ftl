/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package ${packageName?replace('feign', 'constants')};

import com.sofast.cloud.common.constants.Constants;

/**
 * ${applicationName}服务的Feign接口常量定义
 *
 * @author NCIT
 * @date ${.now?string("yyyy/MM/dd HH:mm:ss")}
 */
public interface ${feignConstantsClassName} {

    String FEIGN_${applicationNamePrefix}_SERVICE_ID = "${applicationName}";

<#list methods as m>
    /**
    <#if m.methodDescription??&&m.methodDescription!="">
     * ${m.methodDescription?trim?replace('\"','')}
    <#else>
     * TODO 请书写注释
    </#if>
     */
    String FEIGN${m.methodMappingPathPrefix?replace('\"','')} = Constants.FEIGN_PREFIX + "${url?replace('\"','')}${m.url?replace('\"','')}";
</#list>
}
