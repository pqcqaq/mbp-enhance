package online.zust.qcqcqc.utils;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import online.zust.qcqcqc.utils.annotation.LastSqlOnSearch;
import online.zust.qcqcqc.utils.annotation.MtMDeepSearch;
import online.zust.qcqcqc.utils.annotation.OtMDeepSearch;
import online.zust.qcqcqc.utils.annotation.OtODeepSearch;
import online.zust.qcqcqc.utils.enhance.EntityInfo;
import online.zust.qcqcqc.utils.enhance.EntityRelation;
import online.zust.qcqcqc.utils.enhance.checker.CheckHandler;
import online.zust.qcqcqc.utils.exception.DependencyCheckException;
import online.zust.qcqcqc.utils.exception.ErrorDeepSearchException;
import online.zust.qcqcqc.utils.utils.FieldNameConvertUtils;
import online.zust.qcqcqc.utils.utils.ProxyUtil;
import online.zust.qcqcqc.utils.utils.ReflectUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;


/**
 * @author qcqcqc
 */
@SuppressWarnings("all")
public class EnhanceService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IServiceEnhance<T>, InitializingBean {

    private static final int DEEP = 9;

    public Class<? extends EnhanceService> getSelfClass() {
        return getClass();
    }

    @Override
    public T getById(Serializable id) {
        T byId = baseMapper.selectById(id);
        byId = getDeepSearch(byId);
        return byId;
    }

    @Override
    public T getById(Serializable id, int deep) {
        T byId = baseMapper.selectById(id);
        if (deep <= 0) {
            return byId;
        }
        byId = getDeepSearch(byId, deep);
        return byId;
    }

    @Override
    public T getOne(Wrapper<T> queryWrapper, int deep) {
        T t = baseMapper.selectOne(queryWrapper);
        if (deep <= 0) {
            return t;
        }
        t = getDeepSearch(t, deep);
        return t;
    }

    @Override
    public List<T> list(QueryWrapper<T> queryWrapper) {
        List<T> ts = baseMapper.selectList(queryWrapper);
        ts.forEach(this::getDeepSearch);
        return ts;
    }

    @Override
    public List<T> list(QueryWrapper<T> queryWrapper, int deep) {
        List<T> ts = baseMapper.selectList(queryWrapper);
        if (deep <= 0) {
            return ts;
        }
        ts.forEach(e -> getDeepSearch(e, deep));
        return ts;
    }

    @Override
    public List<T> listByIds(Collection<? extends Serializable> idList) {
        List<T> ts = getBaseMapper().selectBatchIds(idList);
        ts.forEach(this::getDeepSearch);
        return ts;
    }

    @Override
    public List<T> listByIds(Collection<? extends Serializable> idList, int deep) {
        List<T> ts = getBaseMapper().selectBatchIds(idList);
        if (deep <= 0) {
            return ts;
        }
        ts.forEach(e -> getDeepSearch(e, deep));
        return ts;
    }

    @Override
    public T getDeepSearch(T entity) {
        return getDeepSearch(entity, DEEP);
    }

    @Override
    public T getDeepSearch(T entity, int deep) {
        if (deep <= 0) {
            return entity;
        }
        if (entity == null) {
            return null;
        }
        Class<?> aClass = entity.getClass();
        EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo = EntityRelation.getEntityInfoMap().get(getClass());
        entityInfo.getOtoNextFieldMap().forEach((entityInfo1, fields) -> {
            fields.forEach(field -> {
                handleOtOAnnotation(entity, field, aClass, deep);
            });
        });
        entityInfo.getOtmNextFieldMap().forEach((entityInfo1, fields) -> {
            fields.forEach(field -> {
                handleOtMAnnotation(entity, field, aClass, deep);
            });
        });
        entityInfo.getMtmNextFieldMap().forEach((entityInfo1, fields) -> {
            fields.forEach(field -> {
                handleMtMAnnotation(entity, field, aClass, deep);
            });
        });
        return entity;
    }

    @Override
    public void doCheckDependency(Serializable id) throws DependencyCheckException {
        CheckHandler.doCheck(this, id);
    }

    @Override
    public void doCheckDependency(T entity) throws DependencyCheckException {
        Class<?> aClass = entity.getClass();
        if (aClass.getPackageName().startsWith("java")) {
            doCheckDependency((Serializable) entity);
        } else {
            EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo = EntityRelation.getEntityInfos().get(aClass);
            if (entityInfo == null) {
                throw new DependencyCheckException("没有找到实体类: " + aClass.getCanonicalName());
            }
            if (!entityInfo.getEntityClass().getSimpleName().equals(getEntityClass().getSimpleName())) {
                throw new DependencyCheckException("实体类不匹配");
            }
            Field idField = entityInfo.getIdField();
            idField.setAccessible(true);
            try {
                Serializable id = (Serializable) idField.get(entity);
                doCheckDependency(id);
            } catch (IllegalAccessException e) {
                throw new DependencyCheckException("执行依赖检查失败: " + e.getMessage());
            }
        }
    }

    @Override
    public Page<T> pageByLambda(Long page, Long size, LambdaQueryWrapper<T> queryWrapper) {
        Page<T> tPage = new Page<>(page, size);
        Page<T> page1 = baseMapper.selectPage(tPage, queryWrapper);
        List<T> records = page1.getRecords();
        records.forEach(this::getDeepSearch);
        return page1;
    }

    @Override
    public Page<T> pageByLambda(Long page, Long size, LambdaQueryWrapper<T> queryWrapper, int deep) {
        Page<T> tPage = new Page<>(page, size);
        Page<T> page1 = baseMapper.selectPage(tPage, queryWrapper);
        List<T> records = page1.getRecords();
        records.forEach(e -> e = getDeepSearch(e, deep));
        return page1;
    }

    @Override
    public Page<T> pageByLambda(Integer page, Integer size, LambdaQueryWrapper<T> queryWrapper) {
        Page<T> tPage = new Page<>(page, size);
        Page<T> page1 = baseMapper.selectPage(tPage, queryWrapper);
        List<T> records = page1.getRecords();
        records.forEach(this::getDeepSearch);
        return page1;
    }

    @Override
    public Page<T> pageByLambda(Integer page, Integer size, LambdaQueryWrapper<T> queryWrapper, int deep) {
        Page<T> tPage = new Page<>(page, size);
        Page<T> page1 = baseMapper.selectPage(tPage, queryWrapper);
        List<T> records = page1.getRecords();
        records.forEach(e -> e = getDeepSearch(e, deep));
        return page1;
    }

    @Override
    public List<T> fuzzyQuery(SFunction<T, ?> e, Serializable s) {
        return fuzzyQuery(e, s, DEEP);
    }

    @Override
    public List<T> fuzzyQuery(SFunction<T, ?> e, Serializable s, int deep) {
        List<T> list = list(new LambdaQueryWrapper<T>().like(e, s));
        if (deep <= 0) {
            return list;
        }
        list.forEach(e1 -> e1 = getDeepSearch(e1, deep));
        return list;
    }

    @Override
    public T eq(SFunction<T, ?> e, Serializable s) {
        return getOne(new LambdaQueryWrapper<T>().eq(e, s));
    }

    @Override
    public T eq(SFunction<T, ?> e, Serializable s, int deep) {
        return getOne(new LambdaQueryWrapper<T>().eq(e, s), deep);
    }

    @Override
    public List<T> eqList(SFunction<T, ?> e, Serializable s) {
        return eqList(e, s, DEEP);
    }

    @Override
    public List<T> eqList(SFunction<T, ?> e, Serializable s, int deep) {
        List<T> list = list(new LambdaQueryWrapper<T>().eq(e, s));
        if (deep <= 0) {
            return list;
        }
        list.forEach(e1 -> e1 = getDeepSearch(e1, deep));
        return list;
    }

    private void handleOtMAnnotation(T entity, Field declaredField, Class<?> aClass, int deep) {
        declaredField.setAccessible(true);
        OtMDeepSearch otMDeepSearch = declaredField.getAnnotation(OtMDeepSearch.class);
        try {
            deepSearchListAndSetValue(entity, declaredField, aClass, otMDeepSearch, deep);
        } catch (Exception e) {
            log.error("获取字段值失败: ", e);
            // 暂时不需要抛出异常，直接返回null
            if (otMDeepSearch.notNull()) {
                throw new ErrorDeepSearchException("获取字段值失败:" + e.getMessage());
            }
        }
    }

    private void deepSearchListAndSetValue(T entity, Field declaredField, Class<?> aClass, OtMDeepSearch otMDeepSearch, int deep)
            throws NoSuchFieldException, IllegalAccessException {
        String baseId = otMDeepSearch.baseId();
        baseId = FieldNameConvertUtils.camelToUnderline(baseId);
        Class<? extends EnhanceService> service = otMDeepSearch.service();
        // field是自身的唯一标识，baseId是另一张表中用于关联这张表的字段名
        EnhanceService bean = ProxyUtil.getBean(service);
        Field declaredField1 = EntityRelation.getEntityInfos().get(aClass).getIdField();
        declaredField1.setAccessible(true);
        Object value = declaredField1.get(entity);
        if (value instanceof Long l) {
            LastSqlOnSearch annotation = declaredField.getAnnotation(LastSqlOnSearch.class);
            List<?> list;
            if (annotation == null || annotation.value().trim().isEmpty()) {
                // 获取service的list方法
                list = bean.list(new QueryWrapper<>().eq(baseId, l), deep - 1);
                declaredField.set(entity, list);
            } else {
                // 获取service的list方法
                String sql = annotation.value();
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq(baseId, l);
                queryWrapper.last(sql);
                list = bean.list(queryWrapper, deep - 1);
                declaredField.set(entity, list);
            }
            declaredField.set(entity, list);
        } else {
            if (value != null) {
                log.error("类: " + entity.getClass().getCanonicalName() + "中的字段: " + declaredField.getName() + " 值不是Long类型，无法作为id查询");
            }
        }
    }

    private Field getDeclaredField(Class<?> aClass, String field) {
        Field declaredField = ReflectUtils.recursiveGetField(aClass, field);
        if (declaredField == null) {
            throw new ErrorDeepSearchException("没有找到字段: " + field);
        }
        return declaredField;
    }

    private void handleMtMAnnotation(T entity, Field deepSearchField, Class<?> aClass, int deep) {
        deepSearchField.setAccessible(true);
        MtMDeepSearch deepSearchList = deepSearchField.getAnnotation(MtMDeepSearch.class);
        try {
            deepSearchListAndSetValue(entity, deepSearchField, aClass, deepSearchList, deep);
        } catch (Exception e) {
            log.error("获取字段值失败: ", e);
            // 暂时不需要抛出异常，直接返回null
            if (deepSearchList.notNull()) {
                throw new ErrorDeepSearchException("获取字段值失败:" + e.getMessage());
            }
        }
    }

    private void handleOtOAnnotation(T entity, Field deepSearchField, Class<?> aClass, int deep) {
        deepSearchField.setAccessible(true);
        OtODeepSearch otODeepSearchEntity = deepSearchField.getAnnotation(OtODeepSearch.class);
        try {
            deepSearchEntityAndSetValue(entity, deepSearchField, aClass, otODeepSearchEntity, deep);
        } catch (Exception e) {
            log.error("获取字段值失败: ", e);
            // 暂时不需要抛出异常，直接返回null
            if (otODeepSearchEntity.notNull()) {
                throw new ErrorDeepSearchException("获取字段值失败:" + e.getMessage());
            }
            return;
        }
    }

    private <T> void deepSearchListAndSetValue(T entity, Field deepSearchField, Class<?> aClass, MtMDeepSearch deepSearchList, int deep)
            throws NoSuchFieldException, IllegalAccessException {
        // 流程：获取entity的id，构造查询条件，relaService中查找对应的全部id，targetService中查找全部id对应的实体
        Class<? extends EnhanceService> relaService = deepSearchList.relaService();
        Class<? extends EnhanceService> targetService = deepSearchList.targetService();
        String column = deepSearchList.baseId();
        column = FieldNameConvertUtils.camelToUnderline(column);
        // 目标对象在关系表中的字段名
        String targetIdFieldName = deepSearchList.targetId();
        targetIdFieldName = FieldNameConvertUtils.underlineToCamel(targetIdFieldName);
        // 获取entity的id，构造查询条件，relaService中查找对应的全部id
        Field declaredField = EntityRelation.getEntityInfos().get(aClass).getIdField();
        // 获取值declaredField的值
        declaredField.setAccessible(true);
        Object value = declaredField.get(entity);
        if (!(value instanceof Long) && value != null) {
            throw new ErrorDeepSearchException("id字段不是Long类型");
        }
        Long baseId = (Long) value;
        EnhanceService relaServiceImpl = ProxyUtil.getBean(relaService);
        final String finalTargetIdFieldName = targetIdFieldName;
        // 这里list出来的是一堆关联对象，需要根据这些对象的目标id去查找对应的实体
        List<Long> targetIds = relaServiceImpl.list(new QueryWrapper<>().eq(column, baseId)).stream().map(e -> {
            try {
                Class<?> aClass1 = e.getClass();
                Field declaredField1 = getDeclaredField(aClass1, finalTargetIdFieldName);
                declaredField1.setAccessible(true);
                return (Long) declaredField1.get(e);
            } catch (Exception exception) {
                log.error("获取字段值失败: ", exception);
                return null;
            }
        }).toList();

        if (targetIds.isEmpty()) {
            if (deepSearchList.notNull()) {
                throw new ErrorDeepSearchException("没有在关联表找到对应的目标对象id");
            }
            return;
        }
        EnhanceService targetServiceImpl = ProxyUtil.getBean(targetService);
        LastSqlOnSearch annotation = deepSearchField.getAnnotation(LastSqlOnSearch.class);
        List<?> list;
        if (annotation == null || annotation.value().trim().isEmpty()) {
            list = targetServiceImpl.listByIds(targetIds, deep - 1);
        } else {
            String idFieldName = EntityRelation.getEntityInfos().get(targetServiceImpl.getEntityClass()).getIdField().getName();
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.in(FieldNameConvertUtils.camelToUnderline(idFieldName), targetIds);
            queryWrapper.last(annotation.value());
            list = targetServiceImpl.list(queryWrapper, deep - 1);
        }

        if (targetIds.size() != list.size()) {
            log.warn("关系表中的id和目标表中的id数量不一致，可能存在数据库异常");
        }

        deepSearchField.set(entity, list);
    }

    private <T> void deepSearchEntityAndSetValue(T entity, Field deepSearchField, Class<?> aClass, OtODeepSearch annotation, int deep) throws NoSuchFieldException, IllegalAccessException {
        // 获取注解中的字段
        String field = annotation.baseId();
        if (field.trim().isEmpty()) {
            field = deepSearchField.getName() + "Id";
        }
        field = FieldNameConvertUtils.underlineToCamel(field);
        // 获取注解中的service
        Class<? extends EnhanceService> service = annotation.service();
        // 获取service的实例
        EnhanceService bean = ProxyUtil.getBean(service);
        // 名为field的字段的值
        Field declaredField = getDeclaredField(aClass, field);
        declaredField.setAccessible(true);
        Object value = declaredField.get(entity);
        if (value instanceof Long l) {
            LastSqlOnSearch lastSql = deepSearchField.getAnnotation(LastSqlOnSearch.class);
            Object byId1;
            if (lastSql == null || lastSql.value().trim().isEmpty()) {
                // 获取service的getById方法
                byId1 = bean.getById(l, deep - 1);
            } else {
                // 获取service的getOne方法
                Class entityClass1 = bean.getEntityClass();
                Field[] declaredFields = entityClass1.getDeclaredFields();
                String idField = EntityRelation.getEntityInfos().get(entityClass1).getIdField().getName();
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq(FieldNameConvertUtils.camelToUnderline(idField), l);
                queryWrapper.last(lastSql.value());
                byId1 = bean.getOne(queryWrapper, deep - 1);
            }
            deepSearchField.set(entity, byId1);
        } else {
            if (value != null) {
                log.error("类: " + entity.getClass().getCanonicalName() + "中的字段: " + declaredField.getName() + " 值不是Long类型，无法作为id查询");
            }
        }
    }

    @Override
    public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
        T t = baseMapper.selectOne(queryWrapper, throwEx);
        t = getDeepSearch(t);
        return t;
    }

    @Override
    public Optional<T> getOneOpt(Wrapper<T> queryWrapper, boolean throwEx) {
        T value = baseMapper.selectOne(queryWrapper, throwEx);
        value = getDeepSearch(value);
        return Optional.ofNullable(value);
    }

    @Override
    public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
        return SqlHelper.getObject(log, baseMapper.selectMaps(queryWrapper));
    }

    @Override
    public <V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper) {
        return SqlHelper.getObject(log, listObjs(queryWrapper, mapper));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("EnhanceService inited: " + this.getClass().getName());
    }
}
