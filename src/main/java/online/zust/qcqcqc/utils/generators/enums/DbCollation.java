package online.zust.qcqcqc.utils.generators.enums;

/**
 * @author qcqcqc
 * Date: 2024/4/15
 * Time: 22:49
 */
public enum DbCollation {
    /**
     * 排序规则
     */
    UTF8MB4_GENERAL_CI("utf8mb4_general_ci"),
    UTF8_GENERAL_CI("utf8_general_ci"),
    UTF8MB4_UNICODE_CI("utf8mb4_unicode_ci"),
    UTF8MB4_AI_CI("utf8mb4_ai_ci");

    private final String collation;

    DbCollation(String collation) {
        this.collation = collation;
    }

    public String getCollation() {
        return collation;
    }

    public static boolean isCollationExist(String collation) {
        for (DbCollation dbCollation : DbCollation.values()) {
            if (dbCollation.getCollation().equals(collation.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
