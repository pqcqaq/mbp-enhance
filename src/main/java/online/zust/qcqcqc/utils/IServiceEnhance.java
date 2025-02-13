package online.zust.qcqcqc.utils;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import online.zust.qcqcqc.utils.exception.DependencyCheckException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

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
     * 根据lambda表达式获取
     *
     * @param queryWrapper 查询条件
     * @param deep         深度
     * @return 实体
     */
    T getOne(Wrapper<T> queryWrapper, int deep);

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
     *
     * @param id 主键ID
     */
    void doCheckDependency(Serializable id) throws DependencyCheckException;

    /**
     * 进行依赖检查
     *
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

    /**
     * 根据lambda表达式查询分页数据
     *
     * @param page         分页信息
     * @param size         分页信息
     * @param queryWrapper 查询条件
     * @return 分页数据
     */
    Page<T> pageByLambda(Long page, Long size, LambdaQueryWrapper<T> queryWrapper);

    /**
     * 根据lambda表达式查询分页数据
     *
     * @param page         分页信息
     * @param size         分页信息
     * @param queryWrapper 查询条件
     * @param deep         深度
     * @return 分页数据
     */
    Page<T> pageByLambda(Long page, Long size, LambdaQueryWrapper<T> queryWrapper, int deep);

    /**
     * 根据lambda表达式查询分页数据
     *
     * @param page         分页信息
     * @param size         分页信息
     * @param queryWrapper 查询条件
     * @return 分页数据
     */
    Page<T> pageByLambda(Integer page, Integer size, LambdaQueryWrapper<T> queryWrapper);

    /**
     * 根据lambda表达式查询分页数据
     *
     * @param page         分页信息
     * @param size         分页信息
     * @param queryWrapper 查询条件
     * @param deep         深度
     * @return 分页数据
     */
    Page<T> pageByLambda(Integer page, Integer size, LambdaQueryWrapper<T> queryWrapper, int deep);

    /**
     * 根据字段进行模糊查询
     *
     * @param e 查询的字段
     * @param s 查询条件
     * @return 分页数据
     */
    List<T> fuzzyQuery(SFunction<T, ?> e, Serializable s);

    /**
     * 根据字段进行模糊查询
     *
     * @param e 查询的字段
     * @param s 查询条件
     * @return 分页数据
     */
    List<T> fuzzyQuery(SFunction<T, ?> e, Serializable s, int deep);

    /**
     * 根据字段进行相等查询
     *
     * @param e 查询的字段
     * @param s 查询条件
     * @return 分页数据
     */
    T eq(SFunction<T, ?> e, Serializable s);

    /**
     * 根据字段进行相等查询
     *
     * @param e    查询的字段
     * @param s    查询条件
     * @param deep 深度
     * @return 分页数据
     */
    T eq(SFunction<T, ?> e, Serializable s, int deep);

    /**
     * 根据字段进行相等查询
     *
     * @param e 查询的字段
     * @param s 查询条件
     * @return 分页数据
     */
    List<T> eqList(SFunction<T, ?> e, Serializable s);

    /**
     * 根据字段进行相等查询
     *
     * @param e    查询的字段
     * @param s    查询条件
     * @param deep 深度
     * @return 分页数据
     */
    List<T> eqList(SFunction<T, ?> e, Serializable s, int deep);
}
