package online.zust.qcqcqc.utils.exception;

import java.io.Serial;

/**
 * @author qcqcqc
 * Date: 2024/4/14
 * Time: 11:17
 */
public class ErrorSearchException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -76692626268690220L;

    public ErrorSearchException(String message) {
        super(message);
    }
}
