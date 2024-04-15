package online.zust.qcqcqc.utils.generators.enums;

/**
 * @author qcqcqc
 * Date: 2024/4/15
 * Time: 22:51
 */
public enum DbEngine {
    /**
     * 存储引擎
     */
    InnoDB("innodb"),
    MyISAM("myisam"),
    MEMORY("memory"),
    CSV("csv"),
    ARCHIVE("archive"),
    FEDERATED("federated"),
    MRG_MYISAM("mrg_myisam"),
    PERFORMANCE_SCHEMA("performance_schema"),
    SPIDER("spider");

    private final String engine;

    DbEngine(String engine) {
        this.engine = engine;
    }

    public String getEngine() {
        return engine;
    }

    public static boolean isEngineExist(String engine) {
        for (DbEngine dbEngine : DbEngine.values()) {
            if (dbEngine.getEngine().equals(engine.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
