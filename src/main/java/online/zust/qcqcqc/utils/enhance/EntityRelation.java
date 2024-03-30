package online.zust.qcqcqc.utils.enhance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import online.zust.qcqcqc.utils.EnhanceService;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author qcqcqc
 * Date: 2024/3/28
 * Time: 23:25
 * 通过map和树状结构来存储实体关系，便于查询，在service层初始化时注册实体关系。
 */
public class EntityRelation {
    private static final Logger logger = Logger.getLogger(EntityRelation.class.getName());
    protected static Map<Class<? extends EnhanceService<?,?>>, EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> entityInfoMap = new HashMap<>();
    protected static EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> BaseEntity = EntityInfo.initEmptyEntityInfo();

    public static <E, S extends EnhanceService<M, E>, M extends BaseMapper<E>> void printEntityTree(EntityInfo<E, S, M> entityInfo, int depth, Set<EntityInfo<?, ?, ?>> visited) {
        // 打印当前节点
        String sb = "  ".repeat(Math.max(0, depth - 1));
        System.out.println(sb + "|--" + entityInfo.getEntityClass().getSimpleName());

        // 将当前节点标记为已访问
        visited.add(entityInfo);

        // 获取下一个节点列表
        Set<EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>>> nextNodes = entityInfo.getNext();

        // 递归打印下一个节点
        for (EntityInfo<?, ? extends EnhanceService<?, ?>, ? extends BaseMapper<?>> nextNode : nextNodes) {
            if (!visited.contains(nextNode)) { // 如果下一个节点未被访问过
                printEntityTree(nextNode, depth + 1, new HashSet<>(visited));
            } else {
                // 在遇到已访问过的节点时，打印特殊标记，表示存在环
                System.out.println("  (Cycle Detected: " + nextNode.getEntityClass().getSimpleName() + ")");
            }
        }
    }

    public static void printInversePointer() {
        entityInfoMap.forEach((serviceClass, entityInfo) -> {
            System.out.print(entityInfo.getEntityClass().getSimpleName());
            System.out.print("->");
            List<String> list = entityInfo.getPrevious().stream().map(EntityInfo::getEntityClass).map(Class::getSimpleName).toList();
            System.out.println(list);
        });
    }
}
