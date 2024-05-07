package online.zust.qcqcqc.utils;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author qcqcqc
 * Date: 2024/5/7
 * Time: 下午11:12
 */
public class CommonService<Mapper extends BaseMapper<Entity>, Entity> extends EnhanceService<Mapper, Entity> implements IServiceEnhance<Entity> {
}
