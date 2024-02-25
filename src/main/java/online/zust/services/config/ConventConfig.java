package online.zust.services.config;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author qcqcqc
 */
public interface ConventConfig {
    /**
     * 获取jackson转换模块
     *
     * @return 转换模块
     */
    SimpleModule getConventionModule();
}
