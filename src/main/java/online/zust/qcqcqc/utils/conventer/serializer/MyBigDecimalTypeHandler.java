package online.zust.qcqcqc.utils.conventer.serializer;


import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author qcqcqc
 * &#064;Date   2021/7/12 10:43
 * &#064;Description  自定义BigDecimal类型Handler
 */
public class MyBigDecimalTypeHandler extends BaseTypeHandler<BigDecimal> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BigDecimal parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setBigDecimal(i, parameter);
    }

    @Override
    public BigDecimal getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        BigDecimal result = rs.getBigDecimal(columnName);
        if (result != null) {
            //去除小数点后面尾部多余的0
            result = result.stripTrailingZeros();
        }
        return result;
    }

    @Override
    public BigDecimal getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        BigDecimal result = rs.getBigDecimal(columnIndex);
        if (result != null) {
            //去除小数点后面尾部多余的0
            result = result.stripTrailingZeros();
        }
        return result;
    }

    @Override
    public BigDecimal getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        BigDecimal result = cs.getBigDecimal(columnIndex);
        if (result != null) {
            //去除小数点后面尾部多余的0
            result = result.stripTrailingZeros();
        }
        return result;
    }
}
