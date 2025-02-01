package com.yxt.ucache.common.enums;

public enum CacheInternalType implements IEnumType<CacheInternalType>  {
    CAFFEINE("caffeine");
    private String value;

    CacheInternalType(String value) {
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
