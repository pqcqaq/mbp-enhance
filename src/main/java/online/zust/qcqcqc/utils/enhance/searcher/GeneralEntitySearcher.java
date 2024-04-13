package online.zust.qcqcqc.utils.enhance.searcher;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.parser.JsqlParserFunction;
import online.zust.qcqcqc.utils.EnhanceService;
import online.zust.qcqcqc.utils.enhance.EntityInfo;
import online.zust.qcqcqc.utils.enhance.EntityRelation;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * @author qcqcqc
 * Date: 2024/4/9
 * Time: 22:00
 */
@Component
@SuppressWarnings({"unchecked", "unused"})
public class GeneralEntitySearcher {
    /**
     * 根据ID搜索实体
     *
     * @param clazz 实体类
     * @param id    ID
     * @param <T>   实体类型
     * @return 实体
     */
    public static <T> T searchEntityById(Class<T> clazz, Long id) {
        EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfoByClass = EntityRelation.getEntityInfoByClass(clazz);
        if (entityInfoByClass == null) {
            return null;
        }
        EnhanceService<?, T> enhanceService = (EnhanceService<?, T>) entityInfoByClass.getService();
        return enhanceService.getById(id);
    }

    /**
     * 根据字段搜索实体
     *
     * @param entityClass 实体类
     * @param e           JsqlParserFunction
     * @param s           ID
     * @param <E>         实体类型
     * @return 实体
     */
    public static <E> E eq(Class<E> entityClass, SFunction<E, ?> e, Serializable s) {
        EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfoByClass = EntityRelation.getEntityInfoByClass(entityClass);
        if (entityInfoByClass == null) {
            return null;
        }
        EnhanceService<?, E> enhanceService = (EnhanceService<?, E>) entityInfoByClass.getService();
        return enhanceService.eq(e, s);
    }

    /**
     * 根据字段搜索实体
     *
     * @param entityClass 实体类
     * @param e           JsqlParserFunction
     * @param s           ID
     * @param deep        深度
     * @param <E>         实体类型
     * @return 实体
     */
    public static <E> E eq(Class<E> entityClass, SFunction<E, ?> e, Serializable s, int deep) {
        EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfoByClass = EntityRelation.getEntityInfoByClass(entityClass);
        if (entityInfoByClass == null) {
            return null;
        }
        EnhanceService<?, E> enhanceService = (EnhanceService<?, E>) entityInfoByClass.getService();
        return enhanceService.eq(e, s, deep);
    }

    /**
     * 模糊查询
     *
     * @param entityClass 实体类
     * @param e           JsqlParserFunction
     * @param s           模糊查询的字符串
     * @param <E>         实体类型
     * @return 实体列表
     */
    public static <E> List<E> fuzzyQuery(Class<E> entityClass, SFunction<E, ?> e, Serializable s) {
        EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfoByClass = EntityRelation.getEntityInfoByClass(entityClass);
        if (entityInfoByClass == null) {
            return null;
        }
        EnhanceService<?, E> enhanceService = (EnhanceService<?, E>) entityInfoByClass.getService();
        return enhanceService.fuzzyQuery(e, s);
    }

    /**
     * 模糊查询
     *
     * @param entityClass 实体类
     * @param e           JsqlParserFunction
     * @param s           模糊查询的字符串
     * @param deep        深度
     * @param <E>         实体类型
     * @return 实体列表
     */
    public static <E> List<E> fuzzyQuery(Class<E> entityClass, SFunction<E, ?> e, Serializable s, int deep) {
        EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfoByClass = EntityRelation.getEntityInfoByClass(entityClass);
        if (entityInfoByClass == null) {
            return null;
        }
        EnhanceService<?, E> enhanceService = (EnhanceService<?, E>) entityInfoByClass.getService();
        return enhanceService.fuzzyQuery(e, s, deep);
    }

    /**
     * 根据lambda表达式查询分页数据
     *
     * @param entityClass 实体类
     * @param <E>         实体类型
     * @return 分页数据
     */
    public static <E> E getLatest(Class<E> entityClass) {
        EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfoByClass = EntityRelation.getEntityInfoByClass(entityClass);
        if (entityInfoByClass == null) {
            return null;
        }
        EnhanceService<?, E> enhanceService = (EnhanceService<?, E>) entityInfoByClass.getService();
        QueryWrapper<E> eQueryWrapper = new QueryWrapper<>();
        eQueryWrapper.orderByDesc("create_time");
        eQueryWrapper.last("limit 1");
        return enhanceService.getOne(eQueryWrapper);
    }
}
