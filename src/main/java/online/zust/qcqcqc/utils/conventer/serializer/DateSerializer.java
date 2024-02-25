package online.zust.qcqcqc.utils.conventer.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qcqcqc
 */
public class DateSerializer extends JsonSerializer<Date> implements ObjectSerializer {

    private static volatile DateSerializer instance;

    private DateSerializer() {
    }

    public static DateSerializer getInstance() {
        if (instance == null) {
            synchronized (DateSerializer.class) {
                if (instance == null) {
                    instance = new DateSerializer();
                }
            }
        }
        return instance;
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
        gen.writeString(format);
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features){
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) object);
        serializer.write(format);
    }
}
