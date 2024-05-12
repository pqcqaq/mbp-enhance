package online.zust.qcqcqc.utils.enhance;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;
import online.zust.qcqcqc.utils.annotation.MtMDeepSearch;
import online.zust.qcqcqc.utils.annotation.OtMDeepSearch;
import online.zust.qcqcqc.utils.annotation.OtODeepSearch;
import online.zust.qcqcqc.utils.exception.MbpEnhanceBeanRegisterError;
import online.zust.qcqcqc.utils.utils.FieldNameConvertUtils;
import online.zust.qcqcqc.utils.utils.ReflectUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author qcqcqc
 * Date: 2024/3/28
 * Time: 23:24
 * 实体类关系注册器，由此将entity关联注册到关联表中
 */
@Component
public class EntityRelaRegister implements DisposableBean, InitializingBean {
    @Value("${mbp-enhance.debug:false}")
    private Boolean debug;
    private static List<EnhanceService<?, ?>> enhanceServiceList;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(EntityRelaRegister.class);

    /**
     * 注册实体类关联
     *
     * @param enhanceServiceList 实体类关联服务列表
     */
    public EntityRelaRegister(List<EnhanceService<?, ?>> enhanceServiceList) {
        EntityRelaRegister.enhanceServiceList = enhanceServiceList;
        for (EnhanceService<?, ?> enhanceService : EntityRelaRegister.enhanceServiceList) {
            registerBean(enhanceService.getEntityClass(), enhanceService);
        }
        initRelation();
        addRelaToTree();
        logger.info("实体类关联注册完成: {}", enhanceServiceList);
    }

    /**
     * 打印实体类关系树
     */
    private void printEntityTree() {
        System.out.println("------------------Entity Tree:------------------");
        EntityRelation.printEntityTree(EntityRelation.getBaseEntity(), 0, new HashSet<>());
        System.out.println("-------------Entity Inverse Pointer-------------");
        EntityRelation.printInversePointer();
        System.out.println("------------------------------------------------");
    }

    /**
     * 将没有前置实体的实体类添加到根节点
     */
    private static void addRelaToTree() {
        // find no previous entity and add it to the root
        EntityRelation.getEntityInfoMap().forEach((entityClass, entityInfo) -> {
            Map<EntityInfo, List<Field>> otoPreviousFieldMap = entityInfo.getOtoPreviousFieldMap();
            Map<EntityInfo, List<Field>> otmPreviousFieldMap = entityInfo.getOtmPreviousFieldMap();
            Map<EntityInfo, List<Field>> mtmPreviousFieldMap = entityInfo.getMtmPreviousFieldMap();
            if (otoPreviousFieldMap.isEmpty()) {
                EntityRelation.getBaseEntity().addOtONextField(entityInfo, null);
            }
            if (otmPreviousFieldMap.isEmpty()) {
                EntityRelation.getBaseEntity().addOtMNextField(entityInfo, null);
            }
            if (mtmPreviousFieldMap.isEmpty()) {
                EntityRelation.getBaseEntity().addMtMNextField(entityInfo, null);
            }
        });
    }

    /**
     * 初始化实体类关联
     */
    private static void initRelation() {
        // 初始化实体类关联
        EntityRelation.getEntityInfoMap().forEach((serviceClass, entityInfo) -> {
            Field[] declaredFields = entityInfo.getService().getEntityClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(OtODeepSearch.class)) {
                    declaredField.setAccessible(true);
                    OtODeepSearch annotation = declaredField.getAnnotation(OtODeepSearch.class);
                    Class<? extends EnhanceService> service = annotation.service();
                    EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo1 = EntityRelation.getEntityInfoMap().get(service);
                    entityInfo.addOtONextField(entityInfo1, declaredField);
                    entityInfo1.addOtOPreviousField(entityInfo, declaredField);
                }

                if (declaredField.isAnnotationPresent(OtMDeepSearch.class)) {
                    declaredField.setAccessible(true);
                    OtMDeepSearch annotation = declaredField.getAnnotation(OtMDeepSearch.class);
                    Class<? extends EnhanceService> service = annotation.service();
                    EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo1 = EntityRelation.getEntityInfoMap().get(service);
                    entityInfo.addOtMNextField(entityInfo1, declaredField);
                    entityInfo1.addOtMPreviousField(entityInfo, declaredField);
                }

                if (declaredField.isAnnotationPresent(MtMDeepSearch.class)) {
                    // 这边需要考虑中间关系表，隔了一层关联
                    declaredField.setAccessible(true);
                    MtMDeepSearch annotation = declaredField.getAnnotation(MtMDeepSearch.class);
                    Class<? extends EnhanceService> relaService = annotation.relaService();
                    Class<? extends EnhanceService> targetService = annotation.targetService();
                    EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo1 = EntityRelation.getEntityInfoMap().get(relaService);
                    EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo2 = EntityRelation.getEntityInfoMap().get(targetService);
                    entityInfo.addMtMNextField(entityInfo2, declaredField);
                    entityInfo2.addMtMPreviousField(entityInfo1, declaredField);
//                    // 还需要给关系表添加一对一和一对多关系(原对象中一对多，目标对象中一对一)
//                    entityInfo.addOtMNextField(entityInfo1, declaredField);
//                    entityInfo1.addOtMPreviousField(entityInfo, declaredField);
//                    entityInfo2.addOtONextField(entityInfo1, declaredField);
//                    entityInfo1.addOtOPreviousField(entityInfo2, declaredField);
                }
            }
        });
    }

    /**
     * 注册实体类
     *
     * @param entityClass    实体类
     * @param enhanceService 实体类关联服务
     */
    private void registerBean(Class entityClass, EnhanceService enhanceService) {
        doCheckBean(entityClass);
        Field idField = getIdField(entityClass);
        EntityInfo objectEnhanceServiceBaseMapperEntityInfo = new EntityInfo();
        Annotation annotation = entityClass.getAnnotation(TableName.class);
        if (annotation != null) {
            TableName tableName = (TableName) annotation;
            objectEnhanceServiceBaseMapperEntityInfo.setTableName(tableName.value());
        } else {
            objectEnhanceServiceBaseMapperEntityInfo.setTableName(FieldNameConvertUtils.classNameToUnderline(entityClass.getSimpleName()));
        }
        objectEnhanceServiceBaseMapperEntityInfo.setEntityClass(entityClass);
        objectEnhanceServiceBaseMapperEntityInfo.setIdField(idField);
        objectEnhanceServiceBaseMapperEntityInfo.setService(enhanceService);
        EntityRelation.getEntityInfoMap().put(enhanceService.getSelfClass(), objectEnhanceServiceBaseMapperEntityInfo);
        EntityRelation.getEntityInfos().put(entityClass, objectEnhanceServiceBaseMapperEntityInfo);
    }

    /**
     * 获取实体类主键字段
     *
     * @param entityClass 实体类
     * @return 主键字段
     */
    @NotNull
    public static Field getIdField(Class entityClass) {
        Field idField = ReflectUtils.recursiveGetField(entityClass, "id");
        if (idField == null) {
            throw new MbpEnhanceBeanRegisterError("实体类" + entityClass.getSimpleName() + "未设置表主键");
        }
        return idField;
    }

    /**
     * 检查实体类
     *
     * @param entityClass 实体类
     */
    private void doCheckBean(Class entityClass) {
        Field[] declaredFields = entityClass.getDeclaredFields();
        Annotation annotation = entityClass.getAnnotation(TableName.class);
        if (annotation == null) {
            logger.warn("实体类{}未设置表名，将使用自动驼峰映射", entityClass.getSimpleName());
//            throw new MbpEnhanceBeanRegisterError("实体类" + entityClass.getSimpleName() + "未设置表名");
        }
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(OtODeepSearch.class) || declaredField.isAnnotationPresent(OtMDeepSearch.class) || declaredField.isAnnotationPresent(MtMDeepSearch.class)) {
                if (declaredField.isAnnotationPresent(TableField.class)) {
                    TableField tableField = declaredField.getAnnotation(TableField.class);
                    if (tableField.exist()) {
                        throw new MbpEnhanceBeanRegisterError("实体类" + entityClass.getSimpleName() + "的字段" + declaredField.getName() + "未在数据库中存在, 请设置TableField注解中的(exist = false)");
                    }
                } else {
                    throw new MbpEnhanceBeanRegisterError("实体类" + entityClass.getSimpleName() + "的字段" + declaredField.getName() + "未在数据库中存在, 请添加@TableField(exist = false)注解");
                }
            }
        }
        boolean finded = findIdField(entityClass);
        if (!finded) {
            throw new MbpEnhanceBeanRegisterError("实体类" + entityClass.getSimpleName() + "未设置表主键");
        }
    }

    private boolean findIdField(Class<?> entityClass) {
        Field idField = ReflectUtils.recursiveGetFieldWithAnnotation(entityClass, TableId.class);
        return idField != null;
    }

    /**
     * 销毁
     */
    @Override
    public void destroy() {
        EntityRelation.destroy();
    }

    /**
     * 初始化
     *
     * @throws Exception 异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (debug) {
            printEntityTree();
        }
    }
}
