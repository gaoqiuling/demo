package com.itisacat.basic.framework.plugin.swagger.config;

import com.itisacat.basic.framework.core.config.BaseProperties;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SwggerCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

		String env = BaseProperties.getString("spring.profiles.active");

		return env == null || !env.startsWith("prod");
	}

}
