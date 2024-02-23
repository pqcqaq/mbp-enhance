package online.zust.services.config.defaults;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import online.zust.services.config.ConventConfig;
import online.zust.services.conventer.deserializer.DateDeserializer;
import online.zust.services.conventer.deserializer.LocalDatetimeDeserializer;
import online.zust.services.conventer.serializer.BigDecimalSerialize;
import online.zust.services.conventer.serializer.DateSerializer;
import online.zust.services.conventer.serializer.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author qcqcqc
 */
@Component
@ConditionalOnMissingBean(ConventConfig.class)
@Order(1)
public class DefaultConventConfig implements ConventConfig {
    @Override
    public SimpleModule getConventionModule() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        // local-datetime序列化
        simpleModule.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.getInstance());
        // local-datetime序列化
        simpleModule.addDeserializer(LocalDateTime.class, LocalDatetimeDeserializer.getInstance());
        // datetime序列化
        simpleModule.addSerializer(Date.class, DateSerializer.getInstance());
        // datetime序列化
        simpleModule.addDeserializer(Date.class, DateDeserializer.getInstance());
        // big-decimal序列化
        simpleModule.addSerializer(BigDecimal.class, BigDecimalSerialize.getInstance());
        return simpleModule;
    }
}
