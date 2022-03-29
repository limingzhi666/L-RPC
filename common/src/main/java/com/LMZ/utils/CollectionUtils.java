package com.LMZ.utils;

import java.util.Collection;

/**
 * 集合工具类
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

}