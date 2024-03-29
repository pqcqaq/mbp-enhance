package online.zust.qcqcqc.utils.enhance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qcqcqc
 * Date: 2024/3/28
 * Time: 23:25
 * 通过map和树状结构来存储实体关系，便于查询，在service层初始化时注册实体关系。
 */
public class EntityRelation {
    protected static Map<Class<? extends EnhanceService<?,?>>, EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> entityInfoMap = new HashMap<>();
    protected static EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> BaseEntity = EntityInfo.initEmptyEntityInfo();
}
