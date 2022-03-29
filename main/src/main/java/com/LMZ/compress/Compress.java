package com.LMZ.compress;

import com.LMZ.extension.SPI;

@SPI
public interface Compress {
    /**
     * 压缩
     */
    byte[] compress(byte[] bytes);
    /**
     * 解压
     */
    byte[] decompress(byte[] bytes);
}