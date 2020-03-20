package com.itisacat.basic.framework.core.config.condition;

import com.itisacat.basic.framework.consts.PropConsts;
import com.itisacat.basic.framework.core.config.BaseProperties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by huangxin on 2017/2/25.
 */
public class ConfigCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return BaseProperties.getProperty(PropConsts.Core.HJCONFIG_ENABLED, Boolean.class, false);
    }
}
