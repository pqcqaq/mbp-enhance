package online.zust.qcqcqc.utils.generators.config;

import online.zust.qcqcqc.utils.generators.enums.DataType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

/**
 * @author qcqcqc
 * Date: 2024/5/11
 * Time: 下午2:13
 */
public class DefaultColumnTypeMap {

    private static final HashMap<Class<?>, DataType> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(Long.class, DataType.Bigint);
        TYPE_MAP.put(Integer.class, DataType.Integer);
        TYPE_MAP.put(String.class, DataType.Varchar);
        TYPE_MAP.put(java.util.Date.class, DataType.Datetime);
        TYPE_MAP.put(java.sql.Date.class, DataType.Date);
        TYPE_MAP.put(java.sql.Timestamp.class, DataType.Timestamp);
        TYPE_MAP.put(java.sql.Time.class, DataType.Time);
        TYPE_MAP.put(java.sql.Blob.class, DataType.Blob);
        TYPE_MAP.put(Boolean.class, DataType.Tinyint);
        TYPE_MAP.put(Double.class, DataType.Double);
        TYPE_MAP.put(Float.class, DataType.Float);
        TYPE_MAP.put(Byte.class, DataType.Char);
        TYPE_MAP.put(Character.class, DataType.Char);
        TYPE_MAP.put(BigDecimal.class, DataType.Varchar);
        TYPE_MAP.put(BigInteger.class, DataType.Varchar);
    }

    public static DataType getDataType(Class<?> clazz) {
        return TYPE_MAP.computeIfAbsent(clazz, k -> DataType.Varchar);
    }
}
