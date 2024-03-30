package online.zust.qcqcqc.utils.enhance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;

import java.util.HashSet;
import java.util.Set;

public class EntityInfo<E, S extends EnhanceService<M, E>, M extends BaseMapper<E>> {

    public EntityInfo() {
        this.previous = new HashSet<>();
        this.entityClass = null;
        this.service = null;
        this.next = new HashSet<>();
    }

    private final Set<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> previous;
    private Class<E> entityClass;
    private S service;
    private final Set<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> next;

    public void addPrevious(EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo) {
        previous.add(entityInfo);
    }

    public void addNext(EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> entityInfo) {
        next.add(entityInfo);
    }

    public Set<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> getPrevious() {
        return previous;
    }

    public Set<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> getNext() {
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
}
