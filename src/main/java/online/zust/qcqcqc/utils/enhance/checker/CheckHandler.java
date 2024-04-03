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
        checkPrevious(id, entityInfo);
    }

    private static void checkPrevious(Serializable id, EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo) {
        Set<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> previous = entityInfo.getPrevious();
        previous.forEach(item -> {
            EnhanceService<? extends BaseMapper<?>, ?> service1 = item.getService();
            if (service1 == null) {
                // 如果为null，则表示顶层对象为Object，不需要检查
                return;
            }
            Class<?> entityClass = service1.getEntityClass();
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                Annotation annotation = matchFieldTypeAndContainAnnotation(declaredField, entityInfo.getEntityClass());
                if (annotation == null) {
                    continue;
                }
                switch (annotation.annotationType().getSimpleName()) {
                    case "OtODeepSearch" -> handleOtODeepSearch(entityClass, declaredField, service1, id);
                    case "OtMDeepSearch" -> handleOtMDeepSearch(entityClass, declaredField, service1, id);
                    case "MtMDeepSearch" -> handleMtMDeepSearch(declaredField, id);
                }
            }
        });
    }

    private static void handleMtMDeepSearch(Field declaredField, Serializable id) {
        // TODO: 这里有问题，因为无法获取到关联表的信息，关联表里面是没有注解的
//        MtMDeepSearch annotation = declaredField.getAnnotation(MtMDeepSearch.class);
//        String s = annotation.targetId();
//        if (s.trim().isEmpty()) {
//            s = FieldNameConvertUtils.camelToUnderline(s);
//        }
//        Class<? extends EnhanceService> aClass = annotation.relaService();
//        EnhanceService bean = ProxyUtil.getBean(aClass);
//        List<Object> list = bean.list(new QueryWrapper<>().eq(s, id));
//        if (!list.isEmpty()) {
//            MsgOnInversePointer annotation1 = declaredField.getAnnotation(MsgOnInversePointer.class);
//            String msg = annotation1 == null ? "待删除的对象在关系类" + bean.getEntityClass().getSimpleName() + "中存在依赖关系，无法删除" : annotation1.value();
//            throw new DependencyCheckException(msg);
//        }
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
        List<Object> list = service1.list(objectQueryWrapper);
        if (!list.isEmpty()) {
            MsgOnInversePointer annotation1 = declaredField.getAnnotation(MsgOnInversePointer.class);
            String msg = annotation1 == null ? "待删除的对象在类" + entityClass.getSimpleName() + "(list: " + list + ")" + "中存在依赖关系，无法删除" : annotation1.value();
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
