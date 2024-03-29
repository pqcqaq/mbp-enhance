package online.zust.qcqcqc.utils.enhance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EntityInfo<E, S extends EnhanceService<M, E>, M extends BaseMapper<E>> {

    public EntityInfo() {
        this.previous = new ArrayList<>();
        this.entityClass = null;
        this.service = null;
        this.next = new ArrayList<>();
    }

    private List<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> previous;
    private Class<E> entityClass;
    private S service;
    private List<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> next;

    public List<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> getPrevious() {
        return previous;
    }

    public void setPrevious(List<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> previous) {
        this.previous = previous;
    }

    public List<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> getNext() {
        return next;
    }

    public void setNext(List<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> next) {
        this.next = next;
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

    public static <E, S extends EnhanceService<M, E>, M extends BaseMapper<E>> void printEntityTree(EntityInfo<E, S, M> entityInfo, int depth, Set<EntityInfo<?, ?, ?>> visited) {
        // 打印当前节点
        String sb = "  ".repeat(Math.max(0, depth)) + // 缩进
                    entityInfo.entityClass.getSimpleName();
        System.out.println(sb);

        // 将当前节点标记为已访问
        visited.add(entityInfo);

        // 获取下一个节点列表
        List<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> nextNodes = entityInfo.getNext();

        // 递归打印下一个节点
        for (EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> nextNode : nextNodes) {
            if (!visited.contains(nextNode)) { // 如果下一个节点未被访问过
                printEntityTree(nextNode, depth + 1, visited);
            } else {
                // 在遇到已访问过的节点时，打印特殊标记，表示存在环
                System.out.println("  (Cycle Detected: " + nextNode.entityClass.getSimpleName() + ")");
            }
        }
    }
}
