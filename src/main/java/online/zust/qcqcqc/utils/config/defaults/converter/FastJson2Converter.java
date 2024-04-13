package online.zust.qcqcqc.utils.config.defaults.converter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import online.zust.qcqcqc.utils.config.JsonConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author qcqcqc
 */
@Component
@ConditionalOnProperty(prefix = "mbp-enhance.converter", name = "type", havingValue = "fastjson2")
public class FastJson2Converter implements JsonConverter {
    private static final FastJson2Converter INSTANCE = new FastJson2Converter();

    public static FastJson2Converter getInstance() {
        return INSTANCE;
    }

    static {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setWriterFeatures(
                JSONWriter.Feature.WriteNullStringAsEmpty,
                JSONWriter.Feature.WriteLongAsString,
                JSONWriter.Feature.WriteMapNullValue,
                JSONWriter.Feature.WriteNullListAsEmpty,
                JSONWriter.Feature.WriteBigDecimalAsPlain,
                JSONWriter.Feature.IgnoreErrorGetter
        );
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
    }

    @Override
    public <M, T> T convertValue(M entity, Class<T> clazz) {
        return JSON.parseObject(JSON.toJSONString(entity), clazz);
    }

    @Override
    public <T> T fromString(String entity, Class<T> clazz) {
        return JSON.parseObject(entity, clazz);
    }
}
