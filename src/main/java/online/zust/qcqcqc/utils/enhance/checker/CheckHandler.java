package online.zust.qcqcqc.utils.enhance.checker;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;
import online.zust.qcqcqc.utils.annotation.MsgOnCheckError;
import online.zust.qcqcqc.utils.annotation.OtODeepSearch;
import online.zust.qcqcqc.utils.enhance.EntityInfo;
import online.zust.qcqcqc.utils.enhance.EntityRelation;
import online.zust.qcqcqc.utils.exception.DependencyCheckException;
import online.zust.qcqcqc.utils.utils.FieldNameConvertUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author qcqcqc
 * Date: 2024/3/30
 * Time: 23:20
 */
@Component
public class CheckHandler {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CheckHandler.class);
    private static CheckerConfig checkerConfig;

    @Autowired(required = false)
    public CheckHandler(CheckerConfig checkerConfig) {
        if (checkerConfig == null) {
            logger.warn("未找到CheckerConfig的实现类，无法进行依赖检查");
            CheckHandler.checkerConfig = new DefaultCheckerConfig();
        } else {
            logger.info(checkerConfig.getClass().getSimpleName() + "已配置，将用于依赖检查");
            CheckHandler.checkerConfig = checkerConfig;
        }
    }

    public CheckHandler() {
        logger.warn("未配置CheckerConfig的实现类，无法进行依赖检查");
        CheckHandler.checkerConfig = new DefaultCheckerConfig();
    }

    public static void doCheck(EnhanceService service, Serializable id) throws DependencyCheckException {
        Class<? extends EnhanceService> aClass = service.getClass();
        EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo = EntityRelation.getEntityInfoMap().get(aClass);
        if (entityInfo == null) {
            throw new DependencyCheckException("未找到对应的实体类关系信息");
        }
        List<Class<?>> invClass = checkerConfig.needInversePointCheck();
        if (invClass != null && invClass.contains(entityInfo.getEntityClass())) {
            checkPrevious(id, entityInfo);
        }
        List<Class<?>> forClass = checkerConfig.needForwardPointerCheck();
        if (forClass != null && forClass.contains(entityInfo.getEntityClass())) {
            checkNext(id, entityInfo);
        }
    }

    private static void checkNext(Serializable id, EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo) {
        if (entityInfo == null) {
            throw new DependencyCheckException("未找到对应的实体类关系信息");
        }
        // 进行一次查询，下面判断字段是否有数据
        Object byId = entityInfo.getService().getById(id, 1);
        // 只有otm和mtm是需要反查的，然后进行一次查询，如果还有子数据，就抛出异常
        entityInfo.getOtmNextFieldMap().forEach((entityInfo1, fields) -> fields.forEach(field -> handleOtMDeepSearch(byId, field)));
        entityInfo.getMtmNextFieldMap().forEach((entityInfo1, fields) -> fields.forEach(field -> handleMtMDeepSearch(byId, field)));
    }

    private static void checkPrevious(Serializable id, EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo) {
        Object byId = entityInfo.getService().getById(id, 0);
        if (byId == null) {
            throw new DependencyCheckException("待删除的对象不存在");
        }
        // 只有oto是需要反查的，其他的都是模拟一次查询，如果有数据就抛出异常
        Map<EntityInfo, List<Field>> otoPreviousFieldMap = entityInfo.getOtoPreviousFieldMap();
        otoPreviousFieldMap.forEach((entityInfo1, fields) -> fields.forEach(field -> handleOtODeepSearch(entityInfo1.getEntityClass(), field, entityInfo1.getService(), id)));
    }

    private static void handleMtMDeepSearch(Object entity, Field declaredField) {
        try {
            declaredField.setAccessible(true);
            Object o = declaredField.get(entity);
            if (o != null) {
                if (o instanceof List list && !list.isEmpty()) {
                    MsgOnCheckError annotation = declaredField.getAnnotation(MsgOnCheckError.class);
                    String msg = annotation == null ? "待删除的对象的子查询存在数据：" + "(list: " + list + ")" + "无法删除" : annotation.value();
                    throw new DependencyCheckException(msg);
                }
            }
        } catch (IllegalAccessException e) {
            throw new DependencyCheckException("反射获取字段值失败: " + e.getMessage());
        }
    }

    private static void handleOtMDeepSearch(Object entity, Field declaredField) {
        try {
            declaredField.setAccessible(true);
            Object o = declaredField.get(entity);
            if (o != null) {
                if (o instanceof List list && !list.isEmpty()) {
                    MsgOnCheckError annotation = declaredField.getAnnotation(MsgOnCheckError.class);
                    String msg = annotation == null ? "待删除的对象的子查询存在数据：" + "(list: " + list + ")" + "无法删除" : annotation.value();
                    throw new DependencyCheckException(msg);
                }
            }
        } catch (IllegalAccessException e) {
            throw new DependencyCheckException("反射获取字段值失败: " + e.getMessage());
        }
    }

    private static void handleOtODeepSearch(Class<?> entityClass, Field declaredField, EnhanceService<? extends BaseMapper<?>, ?> service1, Serializable id) {
        OtODeepSearch annotation = declaredField.getAnnotation(OtODeepSearch.class);
        String field = annotation.baseId();
        if (field.trim().isEmpty()) {
            field = declaredField.getName() + "Id";
        }
        field = FieldNameConvertUtils.camelToUnderline(field);
        QueryWrapper objectQueryWrapper = new QueryWrapper();
        objectQueryWrapper.eq(field, id);
        List<Object> list = service1.list(objectQueryWrapper, 0);
        if (!list.isEmpty()) {
            MsgOnCheckError annotation1 = declaredField.getAnnotation(MsgOnCheckError.class);
            String msg = annotation1 == null ? "待删除的对象在类" + entityClass.getSimpleName() + "(list: " + list + ")" + "中存在依赖关系，无法删除" : annotation1.value();
            throw new DependencyCheckException(msg);
        }
    }
}
