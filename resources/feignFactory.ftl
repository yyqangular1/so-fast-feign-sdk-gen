/*
 * Copyright (c) 2019-2029. 恩梯梯数据（中国）信息技术有限公司. All Rights Reserved.
 */

package ${packageName?replace('feign', 'factory')};

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sofast.cloud.common.constants.Constants;
import com.sofast.cloud.common.constants.MsgConstants;
import com.sofast.cloud.common.domain.vo.R;
import com.sofast.cloud.common.exception.SoFastFeignException;
import com.sofast.cloud.common.utils.i18n.I18nUtils;
import ${packageName}.${feignClassName};
import feign.hystrix.FallbackFactory;
<#list importList as il>
import ${il};
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
 * ${classDescription?replace('\"','')}接口的断路器工厂，用于处理异常信息
<#else>
 * ${feignClassName}接口的断路器工厂，用于处理异常信息
</#if>
 *
 * @author NCIT
 * @date ${.now?string("yyyy/MM/dd HH:mm:ss")}
 */
public class ${feignFactoryClassName} implements FallbackFactory<${feignClassName}> {

    @Override
    public ${feignClassName} create(Throwable cause) {
        return new ${feignClassName}() {

        <#list methods as m>
            @Override
            public ${m.returnType} ${m.name} {
                <#if m.returnType != "void">
                if (cause instanceof SoFastFeignException) {
                    // TODO 可以在这里定制Feign错误信息处理（主要用于处理原生框架中抛出的异常信息，例如Oauth2等，信息结构体和Sofast不一致，在这里可以特殊处理）
                    return getMessage(cause);
                }
                    <#if feignReturnVal == 0>
                return null;
                    <#else>
                return R.ng(MsgConstants.ERR_E099);
                    </#if>
                </#if>
            }
        </#list>
        };
    }
    /**
     * 获取异常中的错误信息
     *
     * @param cause
     * @return
     */
    private R getMessage(Throwable cause) {
        SoFastFeignException sfe = ((SoFastFeignException) cause);
        JSONObject data = JSON.parseObject(sfe.getData());
        String message = I18nUtils.message(MsgConstants.ERR_E001);
        if (data != null && data.containsKey("error_description")) {
            // 用于处理Oauth2Exception的返回信息
            message = data.getString("error_description");
        }
        // 用于处理自定义的返回错误信息
        if (data != null && data.containsKey("message")) {
            message = data.getString("message");
        }

        return R.message(Constants.FAIL, message);
    }

}
