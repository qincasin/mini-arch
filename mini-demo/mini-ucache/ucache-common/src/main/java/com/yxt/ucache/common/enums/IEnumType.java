package com.yxt.ucache.common.enums;

public interface IEnumType<T extends IEnumType> {

    String getVal();

    default <T extends IEnumType> T valueOfNull(String val) {
        for (IEnumType t : allValues()) {
            if (t.eq(val)) {
                return (T) t;
            }
        }
        return null;
    }

    IEnumType[] allValues();

    default boolean eq(String o) {
        if (o == null || o.trim().isEmpty()) {
            return false;
        }
        return getVal().equalsIgnoreCase(o);
    }
}
