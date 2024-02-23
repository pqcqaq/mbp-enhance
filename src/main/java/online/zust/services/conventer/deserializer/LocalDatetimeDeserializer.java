package online.zust.services.conventer.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import online.zust.services.utils.TimeFormatterUtils;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author pqcmm
 */
public class LocalDatetimeDeserializer extends JsonDeserializer<LocalDateTime> {

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
}
