package online.zust.qcqcqc.utils.config.defaults.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import online.zust.qcqcqc.utils.config.JsonConverter;
import online.zust.qcqcqc.utils.config.condition.FastJsonCondition;
import online.zust.qcqcqc.utils.conventer.deserializer.DateDeserializer;
import online.zust.qcqcqc.utils.conventer.deserializer.LocalDatetimeDeserializer;
import online.zust.qcqcqc.utils.conventer.serializer.BigDecimalSerialize;
import online.zust.qcqcqc.utils.conventer.serializer.DateSerializer;
import online.zust.qcqcqc.utils.conventer.serializer.LocalDateTimeSerializer;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author qcqcqc
 */
@Component
@Conditional(FastJsonCondition.class)
public class FastJsonConverter implements JsonConverter {

    static {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        // 添加fastjson的配置信息 比如 ：是否要格式化返回的json数据
        FastJsonConfig fastJsonConfig = new FastJsonConfig();

        // 修改配置返回内容的过滤
        fastJsonConfig.setSerializerFeatures(
                // 格式化输出
                SerializerFeature.PrettyFormat,
                // 消除循环引用
                SerializerFeature.DisableCircularReferenceDetect,
                // 返回结果保留null值
                SerializerFeature.WriteMapNullValue,
                // 将返回值为null的字符串转变成"",在这里可以自己设置
                SerializerFeature.WriteNullStringAsEmpty,
                // List字段如果为null,输出为[],而非null
                SerializerFeature.WriteNullListAsEmpty
        );

        // 解决 SerializerFeature.WriteNullStringAsEmpty 不生效问题
        ValueFilter valueFilter = (object, name, value) -> {
            if (null == value) {
                value = "";
            }
            return value;
        };
        // 注入过滤器
        fastJsonConfig.setSerializeFilters(valueFilter);

        // Long、BigDecimal 序列化时转 String
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        serializeConfig.put(BigDecimal.class, ToStringSerializer.instance);
        // local-datetime序列化
        serializeConfig.put(LocalDateTime.class, LocalDateTimeSerializer.getInstance());
        // local-datetime序列化
        serializeConfig.put(LocalDateTime.class, LocalDatetimeDeserializer.getInstance());
        // datetime序列化
        serializeConfig.put(Date.class, DateSerializer.getInstance());
        // datetime序列化
        serializeConfig.put(Date.class, DateDeserializer.getInstance());
        // big-decimal序列化
        serializeConfig.put(BigDecimal.class, BigDecimalSerialize.getInstance());

        // 在转换器中添加配置信息
        fastJsonConfig.setSerializeConfig(serializeConfig);
        fastConverter.setDefaultCharset(StandardCharsets.UTF_8);
        fastConverter.setFastJsonConfig(fastJsonConfig);
    }

    @Override
    public <M, T> T convertValue(M entity, Class<T> clazz) {
        // 对象类型转换
        return JSON.parseObject(JSON.toJSONString(entity), clazz);
    }
}
