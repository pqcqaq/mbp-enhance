package online.zust.qcqcqc.utils.enhance.searcher;

import online.zust.qcqcqc.utils.exception.ErrorDeepSearchException;

import java.lang.reflect.Field;

/**
 * @author qcqcqc
 * Date: 2024/3/30
 * Time: 11:00
 */
public interface DeepSearchHandler {
    Object handleSearch(Field fieldWithAnnotation, Object entity) throws ErrorDeepSearchException;
}
