package online.zust.qcqcqc.utils.annotation;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

/**
 * @author qcqcqc
 * Date: 2024/4/3
 * Time: 23:16
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LastSqlOnSearch {
    /**
     * 需要在搜索时进行的sql
     *
     * @return 需要在搜索时进行的sql
     */
    @Language("SQL")
    String value() default "";
}
