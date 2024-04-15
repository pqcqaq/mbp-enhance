package online.zust.qcqcqc.utils.generators.table;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;
import online.zust.qcqcqc.utils.enhance.EntityInfo;
import online.zust.qcqcqc.utils.generators.annotation.ColumnType;
import online.zust.qcqcqc.utils.generators.enums.DataType;
import online.zust.qcqcqc.utils.utils.FieldNameConvertUtils;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.util.StringJoiner;

/**
 * @author qcqcqc
 * Date: 2024/4/15
 * Time: 22:54
 */
public class TableInfo {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TableInfo.class);
    private final EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo;
    private final String tableName;
    private final String charset;
    private final String collation;
    private final String engine;

    public TableInfo(EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo, String charset, String collation, String engine) {
        this.tableName = entityInfo.getEntityClass().getAnnotation(TableName.class).value();
        this.entityInfo = entityInfo;
        this.charset = charset;
        this.collation = collation;
        this.engine = engine;
    }

    public void createTable(JdbcTemplate session) {
        String sql = genInitSql();
        log.info("创建表 `{}`: \n{}", tableName, sql);
        try {
            session.execute(sql);
        } catch (Exception e) {
            log.error("创建表 `{}` 失败", tableName, e);
        }
    }

    private String genInitSql() {
        Class<?> entityClass = entityInfo.getEntityClass();
        boolean equals = "BaseEntity".equals(entityClass.getSuperclass().getSimpleName());
        @Language("MySQL")
        String sql = """
                CREATE TABLE IF NOT EXISTS %s (
                    %s
                ) ENGINE=%s DEFAULT CHARSET=%s COLLATE=%s;
                """;
        StringJoiner stringJoiner = new StringJoiner(",\n");
        if (equals) {
            genColumnSql(stringJoiner, entityClass.getSuperclass());
        }
        genColumnSql(stringJoiner, entityClass);
        return String.format(sql, tableName, stringJoiner, engine, charset, collation);
    }

    private void genColumnSql(StringJoiner stringJoiner, Class<?> superclass) {
        Field[] declaredFields = superclass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (isExcludedField(declaredField)) {
                continue;
            }
            ColumnType columnType = declaredField.getAnnotation(ColumnType.class);
            boolean isIdField = declaredField.isAnnotationPresent(TableId.class);
            DataType type;
            int length;
            boolean nullable;
            String comment;
            String defaultValue;
            String name = declaredField.getName();
            name = FieldNameConvertUtils.camelToUnderline(name);
            if (columnType == null) {
                log.warn("字段 `{}` 未指定类型，将在创建时使用默认类型映射", name);
                type = isIdField ? DataType.Bigint : DataType.Varchar;
                length = 255;
                nullable = true;
                comment = "";
                defaultValue = "";
            } else {
                type = columnType.type();
                length = columnType.length();
                nullable = columnType.nullable();
                comment = columnType.comment();
                defaultValue = columnType.defaultValue();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(name);
            stringBuilder.append(" ");
            stringBuilder.append(type.getType());
            if (type.hasLength()) {
                stringBuilder.append("(");
                stringBuilder.append(length);
                stringBuilder.append(")");
            }
            if (!nullable) {
                stringBuilder.append(" NOT NULL ");
            }
            if (isIdField) {
                stringBuilder.append(" AUTO_INCREMENT PRIMARY KEY ");
            }
            if (!defaultValue.isEmpty()) {
                stringBuilder.append(" DEFAULT ");
                stringBuilder.append(defaultValue);
            }
            if (!comment.isEmpty()) {
                stringBuilder.append(" COMMENT '");
                stringBuilder.append(comment);
                stringBuilder.append("'");
            }
            stringJoiner.add(stringBuilder.toString());
        }
    }

    private boolean isExcludedField(Field declaredField) {
        TableField annotation = declaredField.getAnnotation(TableField.class);
        if (annotation != null) {
            return !annotation.exist();
        }
        String name = declaredField.getName();
        return "serialVersionUID".equals(name);
    }
}
