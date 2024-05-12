package online.zust.qcqcqc.utils.utils;

/**
 * @author qcqcqc
 * Date: 2024/3/30
 * Time: 23:55
 */
public class FieldNameConvertUtils {
    public static String camelToUnderline(String param) {
        if (param == null || param.trim().isEmpty()) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append("_");
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    public static String underlineToCamel(String param) {
        if (param == null || param.trim().isEmpty()) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == '_') {
                if (i + 1 < len) {
                    sb.append(Character.toUpperCase(param.charAt(i + 1)));
                    i++;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String classNameToUnderline(String className) {
        return camelToUnderline(className).substring(1);
    }
}
