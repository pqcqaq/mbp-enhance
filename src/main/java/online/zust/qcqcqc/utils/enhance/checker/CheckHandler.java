package online.zust.qcqcqc.utils.enhance.checker;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;
import online.zust.qcqcqc.utils.annotation.MsgOnInversePointer;
import online.zust.qcqcqc.utils.annotation.OtODeepSearch;
import online.zust.qcqcqc.utils.enhance.EntityInfo;
import online.zust.qcqcqc.utils.enhance.EntityRelation;
import online.zust.qcqcqc.utils.exception.DependencyCheckException;
import online.zust.qcqcqc.utils.utils.FieldNameConvertUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author qcqcqc
 * Date: 2024/3/30
 * Time: 23:20
 */
public class CheckHandler {
    public static void doCheck(EnhanceService service, Serializable id) throws DependencyCheckException {
        EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo = EntityRelation.entityInfoMap.get(service.getClass());
        checkPrevious(id, entityInfo);
        checkNext(id, entityInfo);
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
        if (entityInfo == null) {
            throw new DependencyCheckException("未找到对应的实体类关系信息");
        }
        // 只有oto是需要反查的，其他的都是模拟一次查询，如果有数据就抛出异常
        Map<EntityInfo, List<Field>> otoPreviousFieldMap = entityInfo.getOtoPreviousFieldMap();
        otoPreviousFieldMap.forEach((entityInfo1, fields) -> fields.forEach(field -> {
            handleOtODeepSearch(entityInfo1.getEntityClass(), field, entityInfo1.getService(), id);
        }));
    }

    private static void handleMtMDeepSearch(Object entity, Field declaredField) {
        try {
            declaredField.setAccessible(true);
            Object o = declaredField.get(entity);
            if (o != null) {
                if (o instanceof List list && !list.isEmpty()) {
                    MsgOnInversePointer annotation = declaredField.getAnnotation(MsgOnInversePointer.class);
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
                    MsgOnInversePointer annotation = declaredField.getAnnotation(MsgOnInversePointer.class);
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
        List<Object> list = service1.list(objectQueryWrapper);
        if (!list.isEmpty()) {
            MsgOnInversePointer annotation1 = declaredField.getAnnotation(MsgOnInversePointer.class);
            String msg = annotation1 == null ? "待删除的对象在类" + entityClass.getSimpleName() + "(list: " + list + ")" + "中存在依赖关系，无法删除" : annotation1.value();
            throw new DependencyCheckException(msg);
        }
    }
}
