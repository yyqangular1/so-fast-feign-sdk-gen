/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package ${packageName?replace('feign', 'fallback')};

import ${packageName}.${feignClassName};
<#list importList as il>
import ${il};
</#list>
<#if feignReturnVal != 0>
import com.sofast.cloud.common.constants.MsgConstants;
import com.sofast.cloud.common.domain.vo.R;
</#if>

/**
<#if classDescription??>
 * ${classDescription?replace('\"','')}接口的断路器
<#else>
 * ${feignClassName}接口的断路器
</#if>
 *
 * @author NCIT
 * @date ${.now?string("yyyy/MM/dd HH:mm:ss")}
 */
public class ${feignFallbackClassName} implements ${feignClassName} {

<#list methods as m>
    @Override
    public ${m.returnType} ${m.name} {
        <#if m.returnType != "void">
            <#if feignReturnVal == 0>
        return null;
            <#else>
        return R.ng(MsgConstants.ERR_E099);
            </#if>
        </#if>
    }

</#list>
}
