package online.zust.services.conventer.deserializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import online.zust.services.utils.TimeFormatterUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * @author pqcmm
 */
public class LocalDatetimeDeserializer extends JsonDeserializer<LocalDateTime> implements ObjectSerializer {

    private static volatile LocalDatetimeDeserializer instance;

    private LocalDatetimeDeserializer() {
    }

    public static LocalDatetimeDeserializer getInstance() {
        if (instance == null) {
            synchronized (LocalDatetimeDeserializer.class) {
                if (instance == null) {
                    instance = new LocalDatetimeDeserializer();
                }
            }
        }
        return instance;
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {
        String str = p.getText();
        return LocalDateTime.parse(str, TimeFormatterUtils.SDF_1);
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
        // 自定义反序列化器
        serializer.write(LocalDateTime.parse(object.toString(), TimeFormatterUtils.SDF_1));
    }
}
