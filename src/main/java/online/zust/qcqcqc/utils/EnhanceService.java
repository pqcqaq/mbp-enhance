package online.zust.qcqcqc.utils;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import online.zust.qcqcqc.utils.annotation.MtMDeepSearch;
import online.zust.qcqcqc.utils.annotation.OtMDeepSearch;
import online.zust.qcqcqc.utils.annotation.OtODeepSearch;
import online.zust.qcqcqc.utils.enhance.checker.CheckHandler;
import online.zust.qcqcqc.utils.exception.DependencyCheckException;
import online.zust.qcqcqc.utils.exception.ErrorDeepSearchException;
import online.zust.qcqcqc.utils.utils.ProxyUtil;
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
import org.springframework.transaction.annotation.Transactional;

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
public class EnhanceService<M extends BaseMapper<T>, T> implements IServiceEnhance<T>, InitializingBean {

    private final int DEEP = 9;

    protected final Log log = LogFactory.getLog(getClass());

    protected final Class<?>[] typeArguments = GenericTypeUtils.resolveTypeArguments(getClass(), EnhanceService.class);

    protected final Class<T> entityClass = currentModelClass();

    protected Class<T> currentModelClass() {
        return (Class<T>) this.typeArguments[1];
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    public Class<? extends EnhanceService> getSelfClass() {
        return getClass();
    }

    public EnhanceService<?, ?> getBean() {
        return ProxyUtil.getBean(getClass());
    }


    private volatile SqlSessionFactory sqlSessionFactory;

    protected SqlSessionFactory getSqlSessionFactory() {
        if (this.sqlSessionFactory == null) {
            synchronized (this) {
                if (this.sqlSessionFactory == null) {
                    Object target = this.baseMapper;
                    // 这个检查目前看着来说基本上可以不用判断Aop是不是存在了.
                    if (com.baomidou.mybatisplus.extension.toolkit.AopUtils.isLoadSpringAop()) {
                        if (AopUtils.isAopProxy(this.baseMapper)) {
                            target = AopProxyUtils.getSingletonTarget(this.baseMapper);
                        }
                    }
                    if (target != null) {
                        MybatisMapperProxy mybatisMapperProxy = (MybatisMapperProxy) Proxy.getInvocationHandler(target);
                        SqlSessionTemplate sqlSessionTemplate = (SqlSessionTemplate) mybatisMapperProxy.getSqlSession();
                        this.sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
                    } else {
                        this.sqlSessionFactory = GlobalConfigUtils.currentSessionFactory(this.entityClass);
                    }
                }
            }
        }
        return this.sqlSessionFactory;
    }

    @Autowired
    protected M baseMapper;

    protected final Class<M> mapperClass = currentMapperClass();

    protected Class<M> currentMapperClass() {
        return (Class<M>) this.typeArguments[0];
    }

    @Override
    public M getBaseMapper() {
        return this.baseMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
    }

    protected String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(mapperClass, sqlMethod);
    }

    protected <E> boolean executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
        return SqlHelper.executeBatch(getSqlSessionFactory(), this.log, list, batchSize, consumer);
    }

    @Override
    public boolean saveOrUpdate(T entity) {
        if (null != entity) {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.entityClass);
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
            Object idVal = tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
            return StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal)) ? save(entity) : updateById(entity);
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        return SqlHelper.saveOrUpdateBatch(getSqlSessionFactory(), this.mapperClass, this.log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal)
                   || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
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
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(OtODeepSearch.class)) {
                handleOtOAnnotation(entity, declaredField, aClass, deep);
            }
            if (declaredField.isAnnotationPresent(MtMDeepSearch.class)) {
                handleMtMAnnotation(entity, declaredField, aClass, deep);
            }
            if (declaredField.isAnnotationPresent(OtMDeepSearch.class)) {
                handleOtMAnnotation(entity, declaredField, aClass, deep);
            }
        }
        return entity;
    }

    @Override
    public void doCheckDependency(Serializable id) throws DependencyCheckException {
        CheckHandler.doCheck(this, id);
    }

    @Override
    public void doCheckDependency(T entity) throws DependencyCheckException {
        if (entity.getClass().getPackageName().startsWith("java")) {
            doCheckDependency((Serializable) entity);
        } else {
            Field[] declaredFields = entity.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(TableId.class)) {
                    declaredField.setAccessible(true);
                    try {
                        doCheckDependency((Serializable) declaredField.get(entity));
                    } catch (IllegalAccessException e) {
                        throw new DependencyCheckException(e.getMessage());
                    }
                    break;
                }
            }
        }
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
        String field = otMDeepSearch.field();
        String baseId = otMDeepSearch.baseId();
        Class<? extends EnhanceService> service = otMDeepSearch.service();
        // field是自身的唯一标识，baseId是另一张表中用于关联这张表的字段名
        EnhanceService bean = ProxyUtil.getBean(service);
        Field declaredField1 = aClass.getDeclaredField(field);
        declaredField1.setAccessible(true);
        Object value = declaredField1.get(entity);
        if (value instanceof Long l) {
            List<?> list = bean.list(new QueryWrapper<>().eq(baseId, l), deep - 1);
            declaredField.set(entity, list);
        } else {
            if (value != null) {
                log.error("类: " + entity.getClass().getCanonicalName() + "中的字段: " + declaredField.getName() + " 值不是Long类型，无法作为id查询");
            }
        }
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
        Class<? extends EnhanceService> relaService = deepSearchList.relaService();
        Class<? extends EnhanceService> targetService = deepSearchList.targetService();
        String column = deepSearchList.baseId();
        String targetIdFieldName = deepSearchList.targetId();
        // 获取entity的id，构造查询条件，relaService中查找对应的全部id
        Field declaredField = aClass.getDeclaredField(deepSearchList.targetField());
        if (declaredField == null) {
            // 字段上是否有TableId注解
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField1 : declaredFields) {
                if (declaredField1.isAnnotationPresent(TableId.class)) {
                    declaredField = declaredField1;
                    break;
                }
            }
            if (declaredField == null) {
                throw new ErrorDeepSearchException("没有找到id字段");
            }
        }

        // 获取值declaredField的值
        declaredField.setAccessible(true);
        Object value = declaredField.get(entity);
        if (!(value instanceof Long) && value != null) {
            throw new ErrorDeepSearchException("id字段不是Long类型");
        }
        Long baseId = (Long) value;
        EnhanceService relaServiceImpl = ProxyUtil.getBean(relaService);
        List<Long> targetIds = relaServiceImpl.list(new QueryWrapper<>().eq(column, baseId)).stream().map(e -> {
            try {
                Field declaredField1 = e.getClass().getDeclaredField(targetIdFieldName);
                declaredField1.setAccessible(true);
                return (Long) declaredField1.get(e);
            } catch (NoSuchFieldException | IllegalAccessException noSuchFieldException) {
                noSuchFieldException.printStackTrace();
                return null;
            }
        }).toList();

        if (targetIds.isEmpty()) {
            if (deepSearchList.notNull()) {
                throw new ErrorDeepSearchException("没有找到对应的id");
            }
            return;
        }
        EnhanceService targetServiceImpl = ProxyUtil.getBean(targetService);
        List<?> list = targetServiceImpl.listByIds(targetIds, deep - 1);

        if (targetIds.size() != list.size()) {
            log.warn("关系表中的id和目标表中的id数量不一致，可能存在数据库异常");
        }

        deepSearchField.set(entity, list);
    }

    private <T> void deepSearchEntityAndSetValue(T entity, Field deepSearchField, Class<?> aClass, OtODeepSearch annotation, int deep) throws NoSuchFieldException, IllegalAccessException {
        // 获取注解中的字段
        String field = annotation.baseId();
        if (field.isEmpty()) {
            field = deepSearchField.getName() + "Id";
        }
        // 获取注解中的service
        Class<? extends EnhanceService> service = annotation.service();
        // 获取service的实例
        EnhanceService bean = ProxyUtil.getBean(service);
        // 名为field的字段的值
        Field declaredField = aClass.getDeclaredField(field);
        declaredField.setAccessible(true);
        Object value = declaredField.get(entity);
        if (value instanceof Long l) {
            // 获取service的getById方法
            Object byId1 = bean.getById(l, deep - 1);
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
