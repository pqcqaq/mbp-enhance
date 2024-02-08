package online.zust.services.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.Serial;

/**
 * @author qcqcqc
 */
public class BeanConventUtils {
    public BeanConventUtils() {
        throw new UnsupportedOperationException("BeanConventUtils is a utility class");
    }

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    public final static class BeanConventException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 3211204477960618903L;

        public BeanConventException(Throwable cause) {
            super(cause);
        }
    }

    public static <T, M> T objectConvent(M entity, Class<T> clazz) {
        if (entity == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.convertValue(entity, clazz);
        } catch (Exception e) {
            throw new BeanConventException(e);
        }
    }
}
