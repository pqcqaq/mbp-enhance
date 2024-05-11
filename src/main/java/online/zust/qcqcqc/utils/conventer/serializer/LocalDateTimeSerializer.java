package online.zust.qcqcqc.utils.conventer.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import online.zust.qcqcqc.utils.utils.TimeFormatterUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * @author pqcmm
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> implements ObjectSerializer {
    private static volatile LocalDateTimeSerializer instance;

    private LocalDateTimeSerializer() {
    }

    public static LocalDateTimeSerializer getInstance() {
        if (instance == null) {
            synchronized (LocalDateTimeSerializer.class) {
                if (instance == null) {
                    instance = new LocalDateTimeSerializer();
                }
            }
        }
        return instance;
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        String s = value.format(TimeFormatterUtils.SDF_1);
        gen.writeString(s);
    }

    @Override
    public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) {
        // 自定义序列化
        jsonSerializer.write(TimeFormatterUtils.SDF_1.format((LocalDateTime) o));
    }
}
