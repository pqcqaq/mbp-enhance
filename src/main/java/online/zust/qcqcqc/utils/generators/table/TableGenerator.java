package online.zust.qcqcqc.utils.generators.table;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;
import online.zust.qcqcqc.utils.enhance.EntityInfo;
import online.zust.qcqcqc.utils.enhance.EntityRelation;
import online.zust.qcqcqc.utils.generators.enums.DbCharset;
import online.zust.qcqcqc.utils.generators.enums.DbCollation;
import online.zust.qcqcqc.utils.generators.enums.DbEngine;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @author qcqcqc
 * @date 2024/04
 * @time 18-08-06
 */
@Component
@ConditionalOnProperty(prefix = "mbp-enhance.generator", name = "enable", havingValue = "true")
public class TableGenerator implements InitializingBean {

    /**
     * SqlSession
     */
    private final JdbcTemplate jdbcTemplate;

    public TableGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 表的字符集
     */
    @Value("${mbp-enhance.generator.table.charset:utf8mb4}")
    private String charset;
    /**
     * 表的排序规则
     */
    @Value("${mbp-enhance.generator.table.collation:utf8mb4_general_ci}")
    private String collation;
    /**
     * 表的引擎
     */
    @Value("${mbp-enhance.generator.table.engine:InnoDB}")
    private String engine;
    /**
     * 是否删除表
     */
    @Value("${mbp-enhance.generator.table.drop-table:false}")
    private Boolean dropTable;

    public void initTable() {
        Map<Class<?>, EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> entityInfos = EntityRelation.getEntityInfos();
        Set<Class<?>> classes = entityInfos.keySet();
        for (Class<?> clazz : classes) {
            EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo = entityInfos.get(clazz);
            TableInfo tableInfo = new TableInfo(entityInfo, charset, collation, engine,dropTable);
            tableInfo.createTable(jdbcTemplate);
        }
    }

    private void checkDatabaseInfo() throws Exception {
        boolean charsetExist = DbCharset.isCharsetExist(charset);
        if (!charsetExist) {
            throw new Exception("不支持的字符集：" + charset);
        }
        boolean collationExist = DbCollation.isCollationExist(collation);
        if (!collationExist) {
            throw new Exception("不支持的排序规则：" + collation);
        }
        boolean engineExist = DbEngine.isEngineExist(engine);
        if (!engineExist) {
            throw new Exception("不支持的存储引擎：" + engine);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        checkDatabaseInfo();
    }
}
