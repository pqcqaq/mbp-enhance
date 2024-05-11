package online.zust.qcqcqc.utils.exception;

import java.io.Serial;

/**
 * @author qcqcqc
 * Date: 2024/3/30
 * Time: 17:00
 */
public class DependencyCheckException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -5485302282952634175L;

    public DependencyCheckException(String message) {
        super(message);
    }
}
