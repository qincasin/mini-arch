package com.qjx.demo.code.utils;

import java.util.BitSet;

/**
 * <Description>
 *
 * @author qinjiaxing on 2024/7/23
 * @author <others>
 */
public class BloomFilter {

    // 默认大小
    private static final int DEFAULT_SIZE = Integer.MAX_VALUE;
    // 最小的大小
    private static final int MIN_SIZE = 1000;
    // hash函数的种子因子
    private static final int[] HASH_SEEDS = new int[]{3, 5, 7, 11, 13, 17, 19, 23, 29, 31};
    // 大小为默认大小
    private int SIZE = DEFAULT_SIZE;
    // 位数组，0/1 表示特征
    private BitSet bitSet = null;
    // hash 函数
    private HashFunction[] hashFunctions = new HashFunction[HASH_SEEDS.length];

    public BloomFilter() {
        init();
    }

    public BloomFilter(int size) {
        if (size >= MIN_SIZE) {
            this.SIZE = size;
        }
        init();
    }

    public static void main(String[] args) {
        Integer a = 12345;
        Integer b = 23456;
        BloomFilter bloomFilter = new BloomFilter();
        System.out.println(bloomFilter.contains(a));
        System.out.println(bloomFilter.contains(b));
        bloomFilter.add(a);
        bloomFilter.add(b);
        System.out.println(bloomFilter.contains(a));
        System.out.println(bloomFilter.contains(b));
    }

    /**
     * 初始化
     */
    private void init() {
        bitSet = new BitSet(SIZE);
        // 初始化hash函数
        for (int i = 0; i < HASH_SEEDS.length; i++) {
            hashFunctions[i] = new HashFunction(SIZE, HASH_SEEDS[i]);
        }
    }

    /**
     * 添加
     * 其实就是求出来的hash值，放入到 bitset中
     *
     * @param value
     */
    public void add(Object value) {
        for (HashFunction hashFunction : hashFunctions) {
            bitSet.set(hashFunction.hash(value), true);
        }
    }

    /**
     * 是否包含
     * 这里就是去hash set 中获取hash，但凡取出来的有一个false，则直接返回
     *
     * @param value value
     * @return bool
     */
    public boolean contains(Object value) {
        boolean rst = true;
        for (HashFunction hashFunction : hashFunctions) {
            rst = bitSet.get(hashFunction.hash(value));
            if (!rst) {
                return false;
            }
        }
        return rst;
    }

    // hash 函数
    public static class HashFunction {

        // 位函数大小
        private final int size;
        // hash 种子
        private final int seed;

        public HashFunction(int size, int seed) {
            this.seed = seed;
            this.size = size;
        }

        public int hash(Object value) {
            if (value == null) {
                return 0;
            }
            // hash值
            int hash1 = value.hashCode();
            // 高位的hash
            int hash2 = hash1 >>> 16;
            // 合并hash值(相当于把高低位的特征结合)
            int combine = hash1 ^ hash2;
            // 相乘再取余
            return Math.abs(combine * seed) % size;
        }
    }

}
