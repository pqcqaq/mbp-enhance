package online.zust.qcqcqc.utils.conventer.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author qcqcqc
 */
public class ListBigDecimalSerialize extends JsonSerializer<List<BigDecimal>> {

    private static volatile ListBigDecimalSerialize instance;

    private ListBigDecimalSerialize() {
    }

    public static ListBigDecimalSerialize getInstance() {
        if (instance == null) {
            synchronized (ListBigDecimalSerialize.class) {
                if (instance == null) {
                    instance = new ListBigDecimalSerialize();
                }
            }
        }
        return instance;
    }

    @Override
    public void serialize(List<BigDecimal> bigDecimals, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (bigDecimals != null) {
            jsonGenerator.writeStartArray();
            for (BigDecimal bigDecimal : bigDecimals) {
                if (bigDecimal != null) {
                    bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
                    double v = Double.parseDouble(bigDecimal.toString());
                    jsonGenerator.writeNumber(v);
                }
            }
            jsonGenerator.writeEndArray();
        }
    }
}
