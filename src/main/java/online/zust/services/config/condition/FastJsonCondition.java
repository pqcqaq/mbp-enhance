package online.zust.services.config.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author qcqcqc
 */
@Configuration
public class FastJsonCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 检查配置中是否有converter.type: fastjson
        return "fastjson".equals(context.getEnvironment().getProperty("converter.type"));
    }
}
