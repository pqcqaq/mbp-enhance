package online.zust.qcqcqc.utils;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import online.zust.qcqcqc.utils.exception.DependencyCheckException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author qcqcqc
 */
public interface IServiceEnhance<T> extends IService<T> {
    /**
     * 根据ID获取
     *
     * @param id 主键ID (类字段名)
     * @return 实体
     */
    @Override
    T getById(Serializable id);

    /**
     * 根据ID获取
     *
     * @param id   主键ID (类字段名)
     * @param deep 深度
     * @return 实体
     */
    T getById(Serializable id, int deep);

    /**
     * 深度搜索
     *
     * @param queryWrapper 查询条件
     * @return 查询结果
     */
    List<T> list(QueryWrapper<T> queryWrapper);

    /**
     * 深度搜索
     *
     * @param queryWrapper 查询条件
     * @param deep         深度
     * @return 查询结果
     */
    List<T> list(QueryWrapper<T> queryWrapper, int deep);

    /**
     * 根据ID列表获取
     *
     * @param idList 主键ID列表 (类字段名)
     * @return 实体列表
     */
    @Override
    List<T> listByIds(Collection<? extends Serializable> idList);

    /**
     * 根据ID列表获取
     *
     * @param idList 主键ID列表 (类字段名)
     * @param deep   深度
     * @return 实体列表
     */
    List<T> listByIds(Collection<? extends Serializable> idList, int deep);

    /**
     * 深度搜索
     *
     * @param entity 实体
     * @return 查询结果
     */
    T getDeepSearch(T entity);

    /**
     * 深度搜索
     *
     * @param entity 实体
     * @param deep   深度
     * @return 查询结果
     */
    T getDeepSearch(T entity, int deep);

    /**
     * 进行依赖检查
     * @param id 主键ID
     */
    void doCheckDependency(Serializable id) throws DependencyCheckException;

    /**
     * 进行依赖检查
     * @param entity 实体
     */
    void doCheckDependency(T entity) throws DependencyCheckException;


    /**
     * 根据id删除
     *
     * @param id 主键ID
     * @return 是否成功
     */
    @Override
    default boolean removeById(Serializable id) {
        doCheckDependency(id);
        return IService.super.removeById(id);
    }

    /**
     * 根据id删除
     *
     * @param entity 实体
     * @return 是否成功
     */
    @Override
    default boolean removeById(T entity) {
        doCheckDependency(entity);
        return IService.super.removeById(entity);
    }

}
