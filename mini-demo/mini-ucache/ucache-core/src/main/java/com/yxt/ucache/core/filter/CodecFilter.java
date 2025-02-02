package com.yxt.ucache.core.filter;

import com.yxt.ucache.common.SerialPolicy;
import com.yxt.ucache.common.ValWrapper;
import com.yxt.ucache.common.enums.CacheType;
import com.yxt.ucache.common.enums.OpType;
import com.yxt.ucache.core.core.UCacheManager;
import com.yxt.ucache.core.core.config.NamespaceConfig;
import com.yxt.ucache.core.utils.InnerCodecUtils;

/**
 * 编解码filter, 对远程缓存值进行序列化和反序列化, 如果需要的话
 *
 * @author qinjiaxing on 2025/2/1
 * @author <others>
 */
public class CodecFilter extends AbsInvokeFilter {

    private final SerialPolicy valueCompressSerial;
    private final SerialPolicy valueSerial;
    private final NamespaceConfig config;

    public CodecFilter(NamespaceConfig config) {
        super("CodecFilter", config.getNamespace(), null);
        this.config = config;
        valueCompressSerial = UCacheManager.getSerialPolicy(config.getValueCompressSerialName(), UCacheManager.getSerialPolicy(SerialPolicy.Gzip, null));
        valueSerial = UCacheManager.getSerialPolicy(config.getValueSerialName(), UCacheManager.getSerialPolicy(SerialPolicy.Jackson, null));
    }

    @Override
    public Object invoke(FilterContext context) {
        boolean needCodec = CacheType.REMOTE.equals(getTarget().getCacheType());
        ValWrapper valWrapper = context.getValWrapper();
        if (needCodec && OpType.PUT.equals(context.getOpType()) && valWrapper != null) {
            valWrapper.setValue(InnerCodecUtils.serialVal(valWrapper, this.valueSerial, this.valueCompressSerial, config.getCompressThreshold()));
        }
        Object invoke = super.invoke(context);
        if (needCodec && OpType.GET.equals(context.getOpType()) && (valWrapper = context.getValWrapper()) != null) {
            valWrapper.setValue(InnerCodecUtils.deSerialVal(valWrapper));
        }
        return invoke;
    }

    @Override
    protected boolean canProcess(FilterContext context) {
        return false;
    }
}
