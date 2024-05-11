package online.zust.qcqcqc.utils.config;


import online.zust.qcqcqc.utils.config.defaults.DefaultConventConfig;
import online.zust.qcqcqc.utils.config.defaults.converter.FastJson2Converter;
import online.zust.qcqcqc.utils.config.defaults.converter.FastJsonConverter;
import online.zust.qcqcqc.utils.config.defaults.converter.JacksonConverter;
import online.zust.qcqcqc.utils.enhance.EntityRelaRegister;
import online.zust.qcqcqc.utils.enhance.checker.CheckHandler;
import online.zust.qcqcqc.utils.enhance.searcher.GeneralEntitySearcher;
import online.zust.qcqcqc.utils.generators.table.TableGenerator;
import online.zust.qcqcqc.utils.utils.BeanConvertUtils;
import online.zust.qcqcqc.utils.utils.ProxyUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author qcqcqc
 */
@Configuration
@Import({DefaultConventConfig.class,
        BeanConvertUtils.class,
        FastJsonConverter.class,
        FastJson2Converter.class,
        JacksonConverter.class,
        EntityRelaRegister.class,
        CheckHandler.class,
        GeneralEntitySearcher.class,
        TableGenerator.class,
})
public class MbpEnhanceAutoInject {
    @Bean
    public ProxyUtil proxyUtil() {
        return new ProxyUtil();
    }
}
