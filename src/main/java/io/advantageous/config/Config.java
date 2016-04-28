package io.advantageous.config;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Configuration Interface
 *
 * @author Rick Hightower
 * @author Geoff Chandler
 */
public interface Config {


    /**
     * Get a string at location
     *
     * @param path path
     * @return string at location./
     */
    String getString(String path);

    /**
     * Checks to see if config has the path specified.
     *
     * @param path path to property.
     * @return true if the path exists.
     */
    boolean hasPath(String path);

    /**
     * Get float at location
     *
     * @param path path to property.
     * @return value
     */
    int getInt(String path);


    /**
     * Get float at location
     *
     * @param path path to property.
     * @return value
     */
    float getFloat(String path);

    /**
     * Get double at location
     *
     * @param path path to property.
     * @return value
     */
    double getDouble(String path);

    /**
     * Get long at location
     *
     * @param path path to property.
     * @return value
     */
    long getLong(String path);


    /**
     * Get Duration at location
     *
     * @param path path to property.
     * @return value
     */
    Duration getDuration(String path);

    /**
     * Get list of strings at location
     *
     * @param path path to list of strings.
     * @return value
     */
    List<String> getStringList(String path);

    /**
     * Get list of ints at location
     *
     * @param path path to list of strings.
     * @return value
     */
    List<Integer> getIntList(String path);

    /**
     * Get list of doubles at location
     *
     * @param path path to list of strings.
     * @return value
     */
    List<Double> getDoubleList(String path);

    /**
     * Get list of floats at location
     *
     * @param path path to list of strings.
     * @return value
     */
    List<Float> getFloatList(String path);

    /**
     * Get list of doubles at location
     *
     * @param path path to list of strings.
     * @return value
     */
    List<Long> getLongList(String path);

    /**
     * Get map at location
     *
     * @param path path to list of strings.
     * @return value
     */
    Map<String, Object> getMap(String path);

    /**
     * Get a single config at location.
     *
     * @param path path to config
     * @return Config at location
     */
    Config getConfig(String path);


    /**
     * Get list of configs at location.
     *
     * @param path path to config
     * @return Config at location
     */
    List<Config> getConfigList(String path);

    /**
     * Get a single POJO.
     *
     * @param path path
     * @param type type
     * @param <T>  T generic type
     * @return a single POJO.
     */
    <T> T get(String path, Class<T> type);

    /**
     * Get a list of POJOs.
     *
     * @param path          path
     * @param componentType component type of the pojo
     * @param <T>           generic type of the POJO.
     * @return list of POJOs of type T (type class).
     */
    <T> List<T> getList(String path, Class<T> componentType);

}
