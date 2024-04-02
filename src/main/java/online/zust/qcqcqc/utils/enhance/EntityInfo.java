package online.zust.qcqcqc.utils.enhance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;

import java.lang.reflect.Field;
import java.util.*;

public class EntityInfo<E, S extends EnhanceService<M, E>, M extends BaseMapper<E>> {

    private final Set<EntityInfo> previous;
    private Class<E> entityClass;
    private S service;
    private final Set<EntityInfo> next;
    private final Map<EntityInfo, List<Field>> otoFieldMap;
    private final Map<EntityInfo, List<Field>> otmFieldMap;
    private final Map<EntityInfo, List<Field>> mtmFieldMap;

    public EntityInfo() {
        this.previous = new HashSet<>();
        this.entityClass = null;
        this.service = null;
        this.next = new HashSet<>();
        this.otoFieldMap = new HashMap<>();
        this.otmFieldMap = new HashMap<>();
        this.mtmFieldMap = new HashMap<>();
    }

    public void addOtOField(EntityInfo entityInfo, Field field) {
        otoFieldMap.computeIfAbsent(entityInfo, k -> new ArrayList<>()).add(field);
    }

    public void addOtMField(EntityInfo entityInfo, Field field) {
        otmFieldMap.computeIfAbsent(entityInfo, k -> new ArrayList<>()).add(field);
    }

    public void addMtMField(EntityInfo entityInfo, Field field) {
        mtmFieldMap.computeIfAbsent(entityInfo, k -> new ArrayList<>()).add(field);
    }

    public Map<EntityInfo, List<Field>> getOtoFieldMap() {
        return otoFieldMap;
    }

    public Map<EntityInfo, List<Field>> getOtmFieldMap() {
        return otmFieldMap;
    }

    public Map<EntityInfo, List<Field>> getMtmFieldMap() {
        return mtmFieldMap;
    }


    public void addPrevious(EntityInfo entityInfo) {
        previous.add(entityInfo);
    }

    public void addNext(EntityInfo entityInfo) {
        next.add(entityInfo);
    }

    public Set<EntityInfo> getPrevious() {
        return previous;
    }

    public Set<EntityInfo> getNext() {
        return next;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityInfo<?, ?, ?> that = (EntityInfo<?, ?, ?>) o;
        return Objects.equals(entityClass, that.entityClass) && Objects.equals(service, that.service);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityClass, service);
    }
}
