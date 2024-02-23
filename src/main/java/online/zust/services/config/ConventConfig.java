package online.zust.services.config;

import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.List;

/**
 * @author qcqcqc
 */
public interface ConventConfig {
    /**
     * 获取转换模块
     *
     * @return 转换模块
     */
    SimpleModule getConventionModule();
}
