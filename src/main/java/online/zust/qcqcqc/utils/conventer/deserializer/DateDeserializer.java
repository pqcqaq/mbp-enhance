package online.zust.qcqcqc.utils.conventer.deserializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.io.Serial;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qcqcqc
 */
public class DateDeserializer extends JsonDeserializer<Date> implements ObjectSerializer {

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

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
        // 自定义反序列化
        serializer.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(object));
    }
}
