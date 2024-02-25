package online.zust.services.conventer.serializer;


import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author qcqcqc
 */
public class BigDecimalSerialize extends JsonSerializer<BigDecimal> implements ObjectSerializer {

    private static volatile BigDecimalSerialize instance;

    private BigDecimalSerialize() {
    }

    public static BigDecimalSerialize getInstance() {
        if (instance == null) {
            synchronized (BigDecimalSerialize.class) {
                if (instance == null) {
                    instance = new BigDecimalSerialize();
                }
            }
        }
        return instance;
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value != null) {
            value = value.setScale(2, RoundingMode.HALF_UP);
            double v = Double.parseDouble(value.toString());
            gen.writeNumber(v);
        }
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
        if (object != null) {
            BigDecimal value = (BigDecimal) object;
            value = value.setScale(2, RoundingMode.HALF_UP);
            double v = Double.parseDouble(value.toString());
            serializer.write(v);
        }
    }
}
