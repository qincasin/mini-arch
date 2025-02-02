package com.yxt.ucache.core.utils;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

public class BatchUtils {

    private BatchUtils() {

    }

    public static <T, R> List<R> batchListMerge(int maxSize, List<T> list, Function<Collection<T>, List<R>> function) {
        if (CollectionUtils.isEmpty(list) || function == null) {
            return Collections.emptyList();
        }
        if (list.size() <= maxSize) {
            return function.apply(list);
        }
        List<List<T>> partition = Lists.partition(list, maxSize);
        return partition.stream().map(function).reduce((e1, e2) -> {
            e1.addAll(e2);
            return e1;
        }).orElse(Collections.emptyList());
    }

    public static <T> boolean batchList(int maxSize, List<T> list, Function<Collection<T>, Boolean> function) {
        if (CollectionUtils.isEmpty(list) || function == null) {
            return false;
        }
        if (list.size() <= maxSize) {
            return function.apply(list);
        }
        List<List<T>> partition = Lists.partition(list, maxSize);
        return partition.stream().map(function).reduce((e1, e2) -> e1 && e2).orElse(false);
    }

    public static <K, V> boolean batchMap(int maxSize, Map<K, V> map, Function<Map<K, V>, Boolean> function) {
        if (MapUtils.isEmpty(map) || function == null) {
            return false;
        }
        if (map.size() <= maxSize) {
            return function.apply(map);
        }

        List<List<K>> partition = Lists.partition(new ArrayList<>(map.keySet()), maxSize);
        return partition.stream().map(e -> {
            Map<K, V> submap = new HashMap<>(e.size());
            for (K key : e) {
                submap.put(key, map.get(key));
            }
            return function.apply(submap);
        }).reduce((e1, e2) -> e1 && e2).orElse(false);
    }

    public static <T> void batchList(int maxSize, List<T> list, Consumer<Collection<T>> consumer) {
        if (CollectionUtils.isEmpty(list) || consumer == null) {
            return;
        }
        if (list.size() <= maxSize) {
            consumer.accept(list);
            return;
        }
        List<List<T>> partition = Lists.partition(list, maxSize);
        partition.forEach(consumer);
    }

    public static <T> boolean batchSet(int maxSize, Set<T> set, Function<Collection<T>, Boolean> function) {
        if (CollectionUtils.isEmpty(set) || function == null) {
            return false;
        }
        if (set.size() <= maxSize) {
            return function.apply(set);
        }
        return batchList(maxSize, Lists.newArrayList(set), function);
    }

    public static <T> void batchSet(int maxSize, Set<T> set, Consumer<Collection<T>> consumer) {
        if (CollectionUtils.isEmpty(set) || consumer == null) {
            return;
        }
        if (set.size() <= maxSize) {
            consumer.accept(set);
            return;
        }
        batchList(maxSize, Lists.newArrayList(set), consumer);
    }
}
