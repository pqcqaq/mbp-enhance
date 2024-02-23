package online.zust.services.conventer.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import online.zust.services.utils.TimeFormatterUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author pqcmm
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
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
}
