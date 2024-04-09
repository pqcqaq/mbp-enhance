package online.zust.qcqcqc.utils.enhance.searcher;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;
import online.zust.qcqcqc.utils.enhance.EntityInfo;
import online.zust.qcqcqc.utils.enhance.EntityRelation;
import org.springframework.stereotype.Component;

/**
 * @author qcqcqc
 * Date: 2024/4/9
 * Time: 22:00
 */
@Component
@SuppressWarnings({"unchecked", "unused"})
public class GeneralEntitySearcher {
    public static <T> T searchEntityById(Class<T> clazz, Long id) {
        EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfoByClass = EntityRelation.getEntityInfoByClass(clazz);
        if (entityInfoByClass == null) {
            return null;
        }
        EnhanceService<?, T> enhanceService = (EnhanceService<?, T>) entityInfoByClass.getService();
        return enhanceService.getById(id);
    }
}
