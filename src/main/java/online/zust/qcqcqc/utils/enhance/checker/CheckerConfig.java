package online.zust.qcqcqc.utils.enhance.checker;

import java.util.List;

/**
 * @author qcqcqc
 * Date: 2024/4/2
 * Time: 23:51
 */
public interface CheckerConfig {

    /**
     * 需要进行反向指针检查的服务
     *
     * @return 需要进行反向指针检查的服务
     */
    List<Class<?>> needInversePointCheck();

    /**
     * 需要进行前向指针检查的服务
     *
     * @return 需要进行前向指针检查的服务
     */
    List<Class<?>> needForwardPointerCheck();
}
