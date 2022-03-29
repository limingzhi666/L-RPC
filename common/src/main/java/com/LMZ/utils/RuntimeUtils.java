package com.LMZ.utils;

/**
 * 获取CPU的核心数
 */
public class RuntimeUtils {
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}