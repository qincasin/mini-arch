package com.yxt.ucache.common.enums;

public enum ConsistencyType implements IEnumType<ConsistencyType> {
    // 最终一致性
    EVENTUAL("eventual"),
    // 强一致性
    STRONG("strong"),
    // 默认
    DEFAULT("dft");
    private String value;

    ConsistencyType(String value) {
        this.value = value;
    }

    @Override
    public String getVal() {
        return value;
    }

    @Override
    public IEnumType[] allValues() {
        return values();
    }
}
