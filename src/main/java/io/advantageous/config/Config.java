package io.advantageous.config;

import java.util.List;
import java.util.Map;

/**
 * Configuration Interface
 */
public interface Config {
    String getString(String path);
    boolean hasPath(String path);
    int getInt(String path);
    float getFloat(String path);
    double getDouble(String path);
    long getLong(String path);
    List<String> getStringList(String path);
    Map<String, Object> getMap(String path);
    Config getConfig(String path);
    <T> T get(String path, Class<T> type);
}
