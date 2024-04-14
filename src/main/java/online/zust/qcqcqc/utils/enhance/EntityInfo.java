package online.zust.qcqcqc.utils.enhance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;

import java.lang.reflect.Field;
import java.util.*;

public class EntityInfo<E, S extends EnhanceService<M, E>, M extends BaseMapper<E>> {

    private final Map<EntityInfo, List<Field>> otoPreviousFieldMap;
    private final Map<EntityInfo, List<Field>> otmPreviousFieldMap;
    private final Map<EntityInfo, List<Field>> mtmPreviousFieldMap;
    private Class<E> entityClass;
    private Field idField;
    private S service;
    private final Map<EntityInfo, List<Field>> otoNextFieldMap;
    private final Map<EntityInfo, List<Field>> otmNextFieldMap;
    private final Map<EntityInfo, List<Field>> mtmNextFieldMap;

    public EntityInfo() {
        this.otoPreviousFieldMap = new HashMap<>();
        this.otmPreviousFieldMap = new HashMap<>();
        this.mtmPreviousFieldMap = new HashMap<>();
        this.entityClass = null;
        this.idField = null;
        this.service = null;
        this.otoNextFieldMap = new HashMap<>();
        this.otmNextFieldMap = new HashMap<>();
        this.mtmNextFieldMap = new HashMap<>();
    }

    public Field getIdField() {
        return idField;
    }

    public void setIdField(Field idField) {
        this.idField = idField;
    }

    public void addOtOPreviousField(EntityInfo entityInfo, Field annotation) {
        otoPreviousFieldMap.computeIfAbsent(entityInfo, k -> new ArrayList<>()).add(annotation);
    }

    public void addOtMPreviousField(EntityInfo entityInfo, Field annotation) {
        otmPreviousFieldMap.computeIfAbsent(entityInfo, k -> new ArrayList<>()).add(annotation);
    }

    public void addMtMPreviousField(EntityInfo entityInfo, Field annotation) {
        mtmPreviousFieldMap.computeIfAbsent(entityInfo, k -> new ArrayList<>()).add(annotation);
    }

    public Map<EntityInfo, List<Field>> getOtoPreviousFieldMap() {
        return otoPreviousFieldMap;
    }

    public Map<EntityInfo, List<Field>> getOtmPreviousFieldMap() {
        return otmPreviousFieldMap;
    }

    public Map<EntityInfo, List<Field>> getMtmPreviousFieldMap() {
        return mtmPreviousFieldMap;
    }

    public void addOtONextField(EntityInfo entityInfo, Field field) {
        otoNextFieldMap.computeIfAbsent(entityInfo, k -> new ArrayList<>()).add(field);
    }

    public void addOtMNextField(EntityInfo entityInfo, Field field) {
        otmNextFieldMap.computeIfAbsent(entityInfo, k -> new ArrayList<>()).add(field);
    }

    public void addMtMNextField(EntityInfo entityInfo, Field field) {
        mtmNextFieldMap.computeIfAbsent(entityInfo, k -> new ArrayList<>()).add(field);
    }

    public Map<EntityInfo, List<Field>> getOtoNextFieldMap() {
        return otoNextFieldMap;
    }

    public Map<EntityInfo, List<Field>> getOtmNextFieldMap() {
        return otmNextFieldMap;
    }

    public Map<EntityInfo, List<Field>> getMtmNextFieldMap() {
        return mtmNextFieldMap;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public S getService() {
        return service;
    }

    public void setService(S service) {
        this.service = service;
    }

    public static EntityInfo<?, ?, ?> initEmptyEntityInfo() {
        EntityInfo<Object, EnhanceService<BaseMapper<Object>, Object>, BaseMapper<Object>> objectEnhanceServiceBaseMapperEntityInfo = new EntityInfo<>();
        objectEnhanceServiceBaseMapperEntityInfo.setEntityClass(Object.class);
        objectEnhanceServiceBaseMapperEntityInfo.setService(null);
        return objectEnhanceServiceBaseMapperEntityInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityInfo<?, ?, ?> that = (EntityInfo<?, ?, ?>) o;
        return Objects.equals(entityClass, that.entityClass) && Objects.equals(service, that.service);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityClass, service);
    }
}
