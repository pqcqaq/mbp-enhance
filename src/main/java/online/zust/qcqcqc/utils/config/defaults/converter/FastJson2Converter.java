package online.zust.qcqcqc.utils.config.defaults.converter;

import com.alibaba.fastjson2.JSON;
import online.zust.qcqcqc.utils.config.JsonConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author qcqcqc
 */
@Component
@ConditionalOnProperty(prefix = "converter", name = "type", havingValue = "fastjson2")
public class FastJson2Converter implements JsonConverter {
    @Override
    public <M, T> T convertValue(M entity, Class<T> clazz) {
        return JSON.parseObject(JSON.toJSONString(entity), clazz);
    }
}
