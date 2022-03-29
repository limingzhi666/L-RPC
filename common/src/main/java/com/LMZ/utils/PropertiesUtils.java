package com.LMZ.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.spi.ImageReaderSpi;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 读取配置文件
 */
@Slf4j
public class PropertiesUtils {
    public static Properties readPropertiesFile(String fileName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfigPath = "";
        if (url != null) {
            rpcConfigPath = url.getPath() + fileName;
        }
        Properties properties = null;
        try (InputStreamReader isr = new InputStreamReader(
                new FileInputStream(rpcConfigPath), StandardCharsets.UTF_8)) {
            properties = new Properties();
            properties.load(isr);
        } catch (IOException e) {
            log.error("读取属性文件时发生异常 [{}]", fileName);
        }
        return properties;
    }
}