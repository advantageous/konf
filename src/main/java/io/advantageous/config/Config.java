package io.advantageous.config;

import java.util.Map;

/**
 * Configuration Interface
 */
public interface Config {
    String getString(String path);

    int getInt(String path);

    float getFloat(String path);

    double getDouble(String path);

    long getLong(String path);

    Map<String, Object> getMap(String path);

    Config getConfig(String path);
}
