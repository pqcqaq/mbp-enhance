package online.zust.qcqcqc.utils.generators.enums;

/**
 * @author qcqcqc
 * Date: 2024/4/15
 * Time: 22:45
 */
public enum DbCharset {

    /**
     * 字符集
     */
    UTF8MB4("utf8mb4"),
    UTF8("utf8"),
    BINARY("binary");

    private final String charset;

    DbCharset(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return charset;
    }

    public static boolean isCharsetExist(String charset) {
        for (DbCharset dbCharset : DbCharset.values()) {
            if (dbCharset.getCharset().equals(charset.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
