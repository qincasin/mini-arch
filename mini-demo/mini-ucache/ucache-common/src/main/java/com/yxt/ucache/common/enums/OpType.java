package com.yxt.ucache.common.enums;

public enum OpType implements IEnumType<OpType> {
    // @formatter:off
    // 缓存操作 key-val
    GET("cache", "get"),
    PUT("cache", "put"),
    REMOVE("cache", "remove"),

    // Redis操作, string
    STRING_ADD("string", "add"),
    STRING_QUERY("string", "query"),
    STRING_REM("string", "remove"),

    // Redis操作, list
    LIST_ADD("list", "add"),
    LIST_QUERY("list", "query"),
    LIST_REM("list", "remove"),

    // Redis操作, set
    SET_ADD("set", "add"),
    SET_QUERY("set", "query"),
    SET_REM("set", "remove"),

    // Redis操作, zset
    ZSET_ADD("zset", "add"),
    ZSET_QUERY("zset", "query"),
    ZSET_REM("zset", "remove"),

    // Redis操作, hash
    HASH_ADD("hash", "add"),
    HASH_QUERY("hash", "query"),
    HASH_REM("hash", "remove"),

    NOP("nop", "nop");
    private String value;
    private String subVal;
    // @formatter:on

    OpType(String value, String subVal) {
        this.value = value;
        this.subVal = subVal;
    }

    @Override
    public String getVal() {
        return value;
    }

    public String getSubVal() {
        return subVal;
    }

    @Override
    public IEnumType[] allValues() {
        return values();
    }

    public boolean isCache() {
        return "cache".equals(value);
    }

    public boolean isRedisCollection() {
        return isSet() || isZSet() || isList() || isHash();
    }

    public boolean isZSet() {
        return "zset".equals(value);
    }

    public boolean isSet() {
        return "set".equals(value);
    }

    public boolean isHash() {
        return "hash".equals(value);
    }

    public boolean isList() {
        return "list".equals(value);
    }

    public boolean isAdd() {
        return this == SET_ADD || this == ZSET_ADD || this == PUT || this == STRING_ADD || this == HASH_ADD || this == LIST_ADD;
    }
}
