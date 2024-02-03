package online.zust.services.exception;

import java.io.Serial;

/**
 * @author qcqcqc
 */
public class ErrorDeepSearchException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -1649864766769091376L;

    public ErrorDeepSearchException(String message) {
        super(message);
    }
}
