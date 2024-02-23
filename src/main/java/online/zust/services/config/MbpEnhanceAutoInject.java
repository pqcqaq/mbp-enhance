package online.zust.services.config;

import online.zust.services.config.defaults.DefaultConventConfig;
import online.zust.services.utils.BeanConventUtils;
import online.zust.services.utils.ProxyUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author qcqcqc
 */
@Configuration
@Import({DefaultConventConfig.class, BeanConventUtils.class})
public class MbpEnhanceAutoInject {
    @Bean
    public ProxyUtil proxyUtil() {
        return new ProxyUtil();
    }
}
