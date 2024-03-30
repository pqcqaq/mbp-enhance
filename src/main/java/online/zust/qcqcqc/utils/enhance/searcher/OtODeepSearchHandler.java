package online.zust.qcqcqc.utils.enhance.searcher;

import online.zust.qcqcqc.utils.EnhanceService;
import online.zust.qcqcqc.utils.annotation.OtODeepSearch;
import online.zust.qcqcqc.utils.exception.ErrorDeepSearchException;
import online.zust.qcqcqc.utils.utils.ProxyUtil;

import java.lang.reflect.Field;

/**
 * @author qcqcqc
 * Date: 2024/3/30
 * Time: 11:10
 */
public class OtODeepSearchHandler implements DeepSearchHandler {
    @Override
    public Object handleSearch(Field fieldWithAnnotation, Object entity) throws ErrorDeepSearchException {
        OtODeepSearch annotation = fieldWithAnnotation.getAnnotation(OtODeepSearch.class);
        String field = annotation.baseId();
        if (field.trim().isEmpty()) {
            field = fieldWithAnnotation.getName() + "Id";
        }
        Object idValue;
        Class<? extends EnhanceService> service = annotation.service();
        EnhanceService bean = ProxyUtil.getBean(service);
        try {
            fieldWithAnnotation.setAccessible(true);
            Field declaredField = entity.getClass().getDeclaredField(field);
            idValue = declaredField.get(entity);
        } catch (Exception e) {
            throw new ErrorDeepSearchException(e.getMessage());
        }
        if (idValue == null) {
            return null;
        }
        if (idValue instanceof Long) {
            return bean.getById((Long) idValue);
        }
        throw new ErrorDeepSearchException("Field type error: " + fieldWithAnnotation.getName() + " should be Long type.");
    }
}
