package online.zust.services.config;

import online.zust.services.utils.ProxyUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qcqcqc
 */
@Configuration
public class MbpEnhanceAutoInject {
    @Bean
    public ProxyUtil proxyUtil() {
        return new ProxyUtil();
    }
}
