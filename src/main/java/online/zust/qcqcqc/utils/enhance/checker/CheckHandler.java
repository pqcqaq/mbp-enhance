package online.zust.qcqcqc.utils.enhance.checker;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;
import online.zust.qcqcqc.utils.annotation.MsgOnInversePointer;
import online.zust.qcqcqc.utils.annotation.MtMDeepSearch;
import online.zust.qcqcqc.utils.annotation.OtMDeepSearch;
import online.zust.qcqcqc.utils.annotation.OtODeepSearch;
import online.zust.qcqcqc.utils.enhance.EntityInfo;
import online.zust.qcqcqc.utils.enhance.EntityRelation;
import online.zust.qcqcqc.utils.exception.DependencyCheckException;
import online.zust.qcqcqc.utils.utils.FieldNameConvertUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * @author qcqcqc
 * Date: 2024/3/30
 * Time: 23:20
 */
public class CheckHandler {
    public static <T> void doCheck(T service, Serializable id) throws DependencyCheckException {
        EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo = EntityRelation.entityInfoMap.get(service.getClass());
        Set<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> previous = entityInfo.getPrevious();
        previous.forEach(item -> {
            EnhanceService<? extends BaseMapper<?>, ?> service1 = item.getService();
            Class<?> entityClass = service1.getEntityClass();
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                Annotation annotation = matchFieldTypeAndContainAnnotation(declaredField, entityInfo.getEntityClass());
                if (annotation == null) {
                    continue;
                }
                switch (annotation.annotationType().getSimpleName()) {
                    case "OtODeepSearch" -> handleOtODeepSearch(entityClass, declaredField, service1, id);
                    case "OtMDeepSearch" -> {
                        handleOtMDeepSearch(entityClass, declaredField, service1, id);
                    }
                    case "MtMDeepSearch" -> {
                        handleMtMDeepSearch(entityClass, declaredField, service1, id);
                    }
                }
            }
        });
    }

    private static void handleMtMDeepSearch(Class<?> entityClass, Field declaredField, EnhanceService<? extends BaseMapper<?>, ?> service1, Serializable id) {

    }

    private static void handleOtMDeepSearch(Class<?> entityClass, Field declaredField, EnhanceService<? extends BaseMapper<?>, ?> service1, Serializable id) {
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
        Object one = service1.getOne(objectQueryWrapper);
        if (one != null) {
            MsgOnInversePointer annotation1 = declaredField.getAnnotation(MsgOnInversePointer.class);
            String msg = annotation1 == null ? "待删除的对象在类" + entityClass.getSimpleName() + "中存在依赖关系，无法删除" : annotation1.value();
            throw new DependencyCheckException(msg);
        }
    }

    private static Annotation matchFieldTypeAndContainAnnotation(Field declaredFields, Class<?> entityClass) {
        Class<?> type = declaredFields.getType();
        if (!type.equals(entityClass)) {
            return null;
        }
        if (declaredFields.isAnnotationPresent(OtODeepSearch.class)) {
            return declaredFields.getAnnotation(OtODeepSearch.class);
        }
        if (declaredFields.isAnnotationPresent(OtMDeepSearch.class)) {
            return declaredFields.getAnnotation(OtMDeepSearch.class);
        }
        if (declaredFields.isAnnotationPresent(MtMDeepSearch.class)) {
            return declaredFields.getAnnotation(MtMDeepSearch.class);
        }
        return null;
    }
}
