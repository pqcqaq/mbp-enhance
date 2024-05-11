package online.zust.qcqcqc.utils.config.defaults.converter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import online.zust.qcqcqc.utils.config.ConventConfig;
import online.zust.qcqcqc.utils.config.JsonConverter;
import online.zust.qcqcqc.utils.config.defaults.DefaultConventConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author qcqcqc
 */
@Component
@ConditionalOnMissingBean(JsonConverter.class)
public class JacksonConverter implements JsonConverter {
    private static final Logger log = LoggerFactory.getLogger(JacksonConverter.class);
    public static ObjectMapper OBJECT_MAPPER;
    private static final JacksonConverter INSTANCE;

    static {
        DefaultConventConfig defaultConventConfig = new DefaultConventConfig();
        INSTANCE = new JacksonConverter(Map.of("default", defaultConventConfig));
    }

    public static JacksonConverter getInstance() {
        return INSTANCE;
    }

    @Autowired
    public JacksonConverter(Map<String, ConventConfig> conventConfigMaps) {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        conventConfigMaps.forEach((k, v) -> {
            log.info("加载转换模块: {}", k);
            OBJECT_MAPPER.registerModule(v.getConventionModule());
        });
    }

    @Override
    public <M, T> T convertValue(M entity, Class<T> clazz) {
        return OBJECT_MAPPER.convertValue(entity, clazz);
    }

    @Override
    public <T> T fromString(String entity, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(entity, clazz);
        } catch (Exception e) {
            log.error("解析json失败", e);
            return null;
        }
    }
}
