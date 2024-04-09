package online.zust.qcqcqc.utils.exception;

import java.io.Serial;

/**
 * @author qcqcqc
 * Date: 2024/4/2
 * Time: 22:59
 */
public class MbpEnhanceBeanRegisterError extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4854819234268290921L;

    public MbpEnhanceBeanRegisterError(String message) {
        super(message);
    }
}
