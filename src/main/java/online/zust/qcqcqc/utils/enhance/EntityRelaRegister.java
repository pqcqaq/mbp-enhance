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
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public EntityRelaRegister(List<EnhanceService<?, ?>> enhanceServiceList) {
        EntityRelaRegister.enhanceServiceList = enhanceServiceList;
        for (EnhanceService<?, ?> enhanceService : EntityRelaRegister.enhanceServiceList) {
            registerBean(enhanceService.getEntityClass(), enhanceService);
        }
        initRelation();
        addRelaToTree();
        logger.info("实体类关联注册完成: {}", enhanceServiceList.stream().map(item -> item.getClass().getSimpleName()).toList());
    }

    private void printEntityTree() {
        System.out.println("------------------Entity Tree:------------------");
        EntityRelation.printEntityTree(EntityRelation.BaseEntity, 0, new HashSet<>());
        System.out.println("-------------Entity Inverse Pointer-------------");
        EntityRelation.printInversePointer();
        System.out.println("------------------------------------------------");
    }

    private static void addRelaToTree() {
        // find no previous entity and add it to the root
        EntityRelation.entityInfoMap.forEach((entityClass, entityInfo) -> {
            Set<EntityInfo> previous = entityInfo.getPrevious();
            if (previous.isEmpty()) {
                EntityRelation.BaseEntity.getNext().add(entityInfo);
                entityInfo.getPrevious().add(EntityRelation.BaseEntity);
            }
        });
    }

    private static void initRelation() {
        // 初始化实体类关联
        EntityRelation.entityInfoMap.forEach((serviceClass, entityInfo) -> {
            Field[] declaredFields = entityInfo.getService().getEntityClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(OtODeepSearch.class)) {
                    declaredField.setAccessible(true);
                    OtODeepSearch annotation = declaredField.getAnnotation(OtODeepSearch.class);
                    Class<? extends EnhanceService> service = annotation.service();
                    EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo1 = EntityRelation.entityInfoMap.get(service);
                    entityInfo1.addPrevious(entityInfo);
                    entityInfo.addNext(entityInfo1);
                    entityInfo.addOtONextField(entityInfo1, declaredField);
                    entityInfo1.addOtOPreviousField(entityInfo, declaredField);
                }

                if (declaredField.isAnnotationPresent(OtMDeepSearch.class)) {
                    declaredField.setAccessible(true);
                    OtMDeepSearch annotation = declaredField.getAnnotation(OtMDeepSearch.class);
                    Class<? extends EnhanceService> service = annotation.service();
                    EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo1 = EntityRelation.entityInfoMap.get(service);
                    entityInfo1.addPrevious(entityInfo);
                    entityInfo.addNext(entityInfo1);
                    entityInfo.addOtMNextField(entityInfo1, declaredField);
                    entityInfo1.addOtMPreviousField(entityInfo, declaredField);
                }

                if (declaredField.isAnnotationPresent(MtMDeepSearch.class)) {
                    // 这边需要考虑中间关系表，隔了一层关联
                    declaredField.setAccessible(true);
                    MtMDeepSearch annotation = declaredField.getAnnotation(MtMDeepSearch.class);
                    Class<? extends EnhanceService> relaService = annotation.relaService();
                    Class<? extends EnhanceService> targetService = annotation.targetService();
                    EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo1 = EntityRelation.entityInfoMap.get(relaService);
                    EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo2 = EntityRelation.entityInfoMap.get(targetService);
                    entityInfo1.addPrevious(entityInfo);
                    entityInfo.addNext(entityInfo1);
                    entityInfo2.addPrevious(entityInfo1);
                    entityInfo1.addNext(entityInfo2);
                    entityInfo.addMtMNextField(entityInfo2, declaredField);
                    entityInfo2.addMtMPreviousField(entityInfo1, declaredField);
                }
            }
        });
    }

    private void registerBean(Class entityClass, EnhanceService enhanceService) {
        doCheckBean(entityClass);
        EntityInfo objectEnhanceServiceBaseMapperEntityInfo = new EntityInfo();
        objectEnhanceServiceBaseMapperEntityInfo.setEntityClass(entityClass);
        objectEnhanceServiceBaseMapperEntityInfo.setService(enhanceService);
        EntityRelation.entityInfoMap.put(enhanceService.getSelfClass(), objectEnhanceServiceBaseMapperEntityInfo);
    }

    private void doCheckBean(Class entityClass) {
        Field[] declaredFields = entityClass.getDeclaredFields();
        Annotation annotation = entityClass.getAnnotation(TableName.class);
        boolean hasTableId = false;
        if (annotation == null) {
            throw new MbpEnhanceBeanRegisterError("实体类" + entityClass.getSimpleName() + "未设置表名");
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
            if (declaredField.isAnnotationPresent(TableId.class)) {
                hasTableId = true;
            }
        }
        if (!hasTableId) {
            Class<?> superclass = entityClass.getSuperclass();
            boolean equals = "BaseEntity".equals(superclass.getSimpleName());
            if (!equals) {
                throw new MbpEnhanceBeanRegisterError("实体类" + entityClass.getSimpleName() + "未设置表主键");
            }
        }
    }

    @Override
    public void destroy() {
        EntityRelation.entityInfoMap.clear();
        EntityRelation.BaseEntity = EntityInfo.initEmptyEntityInfo();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (debug) {
            printEntityTree();
        }
    }
}
