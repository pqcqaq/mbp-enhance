package online.zust.qcqcqc.utils.enhance.checker;

import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author qcqcqc
 * Date: 2024/4/3
 * Time: 21:43
 */
@Component
@ConditionalOnMissingBean(CheckerConfig.class)
public class DefaultCheckerConfig implements CheckerConfig {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DefaultCheckerConfig.class);

    static {
        logger.warn("DefaultCheckerConfig is used, please implement CheckerConfig interface and register it as a bean");
    }

    @Override
    public List<Class<?>> needInversePointCheck() {
        return null;
    }

    @Override
    public List<Class<?>> needForwardPointerCheck() {
        return null;
    }
}
