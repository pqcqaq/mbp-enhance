package online.zust.services.conventer.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.io.Serial;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qcqcqc
 */
public class DateDeserializer extends JsonDeserializer<Date> {
    public static final class DateDeserializerException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 2476425818645684775L;

        public DateDeserializerException(String message) {
            super(message);
        }
    }

    private static volatile DateDeserializer instance;

    private DateDeserializer() {
    }

    public static DateDeserializer getInstance() {
        if (instance == null) {
            synchronized (DateDeserializer.class) {
                if (instance == null) {
                    instance = new DateDeserializer();
                }
            }
        }
        return instance;
    }

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {
        String text = p.getText();
        Date parse;
        try {
            parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(text);
        } catch (ParseException e) {
            throw new DateDeserializerException(e.getMessage());
        }
        return parse;
    }
}
