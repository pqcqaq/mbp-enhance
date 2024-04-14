package online.zust.qcqcqc.utils.enhance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author qcqcqc
 * Date: 2024/3/28
 * Time: 23:25
 * 通过map和树状结构来存储实体关系，便于查询，在service层初始化时注册实体关系。
 */
public class EntityRelation {
    private static final Logger logger = Logger.getLogger(EntityRelation.class.getName());
    private static final Map<Class<? extends EnhanceService<?, ?>>, EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> entityInfoMap = new HashMap<>();

    private static final Map<Class<?>, EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> entitysMap = new HashMap<>();
    private static EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> BaseEntity = EntityInfo.initEmptyEntityInfo();

    public static Map<Class<? extends EnhanceService<?, ?>>, EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> getEntityInfoMap() {
        return entityInfoMap;
    }

    public static Map<Class<?>, EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> getEntityInfos() {
        return entitysMap;
    }

    public static EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> getEntityInfoByEntityClass(Class<?> clazz) {
        return entitysMap.get(clazz);
    }

    public static EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> getBaseEntity() {
        return BaseEntity;
    }

    public static void setBaseEntity(EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> baseEntity) {
        BaseEntity = baseEntity;
    }

    public static void destroy() {
        entityInfoMap.clear();
        BaseEntity = EntityInfo.initEmptyEntityInfo();
    }

    public static <E, S extends EnhanceService<M, E>, M extends BaseMapper<E>> void printEntityTree(EntityInfo<E, S, M> entityInfo, int depth, Set<EntityInfo<?, ?, ?>> visited) {
        printEntityTree("Base", entityInfo, depth, visited);
    }

    public static <E, S extends EnhanceService<M, E>, M extends BaseMapper<E>> void printEntityTree(String type, EntityInfo<E, S, M> entityInfo, int depth, Set<EntityInfo<?, ?, ?>> visited) {
        // 打印当前节点
        String sb = "  ".repeat(Math.max(0, depth - 1));
        System.out.println(sb + type + "|--" + entityInfo.getEntityClass().getSimpleName());

        // 将当前节点标记为已访问
        visited.add(entityInfo);

        // 获取下一个节点列表
        Map<EntityInfo, List<Field>> otoNextFieldMap = entityInfo.getOtoNextFieldMap();
        Map<EntityInfo, List<Field>> otmNextFieldMap = entityInfo.getOtmNextFieldMap();
        Map<EntityInfo, List<Field>> mtmNextFieldMap = entityInfo.getMtmNextFieldMap();

        for (EntityInfo nextNode : otoNextFieldMap.keySet()) {
            if (!visited.contains(nextNode)) {
                printEntityTree("OtO: ", nextNode, depth + 1, new HashSet<>(visited));
            } else {
                System.out.println("  (Cycle Detected: " + nextNode.getEntityClass().getSimpleName() + ")");
            }
        }

        for (EntityInfo nextNode : otmNextFieldMap.keySet()) {
            if (!visited.contains(nextNode)) {
                printEntityTree("OtM: ", nextNode, depth + 1, new HashSet<>(visited));
            } else {
                System.out.println("  (Cycle Detected: " + nextNode.getEntityClass().getSimpleName() + ")");
            }
        }

        for (EntityInfo nextNode : mtmNextFieldMap.keySet()) {
            if (!visited.contains(nextNode)) {
                printEntityTree("MtM: ", nextNode, depth + 1, new HashSet<>(visited));
            } else {
                System.out.println("  (Cycle Detected: " + nextNode.getEntityClass().getSimpleName() + ")");
            }
        }
    }

    public static void printInversePointer() {
        entityInfoMap.forEach((serviceClass, entityInfo) -> {
            System.out.print("entity: " + entityInfo.getEntityClass().getSimpleName());
            System.out.print("---->");
            List<String> list1 = entityInfo.getOtoPreviousFieldMap().keySet().stream().map(EntityInfo::getEntityClass).map(item -> item.getSimpleName() + "(OtO)").toList();
            List<String> list = new ArrayList<>(list1);
            list.addAll(entityInfo.getOtmPreviousFieldMap().keySet().stream().map(EntityInfo::getEntityClass).map(item -> item.getSimpleName() + "(OtM)").toList());
            list.addAll(entityInfo.getMtmPreviousFieldMap().keySet().stream().map(EntityInfo::getEntityClass).map(item -> item.getSimpleName() + "(MtM)").toList());
            System.out.println(list);
        });
    }

    public static <T> EntityInfo<?, ? extends EnhanceService<?, ?>,
            ? extends BaseMapper<?>> getEntityInfoByClass(Class<T> clazz) {
        return entityInfoMap.values().stream().filter(entityInfo -> entityInfo.getEntityClass().equals(clazz)).findFirst().orElse(null);
    }
}
