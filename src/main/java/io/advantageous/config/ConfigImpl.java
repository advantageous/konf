package io.advantageous.config;

import java.util.List;
import java.util.Map;

import static io.advantageous.boon.core.reflection.BeanUtils.*;


public class ConfigImpl implements Config {

    private final Object root;

    public ConfigImpl(Map<String, Object> map) {
        this.root = map;
    }

    public <T> ConfigImpl(T object) {
        this.root = object;
    }

    @Override
    public String getString(String path) {
        return findProperty(root, path).toString();
    }

    @Override
    public boolean hasPath(String path) {
        return isPropPath(path);
    }

    @Override
    public int getInt(String path) {
        return ((Number)findProperty(root, path)).intValue();
    }

    @Override
    public float getFloat(String path) {
        return ((Number)findProperty(root, path)).floatValue();
    }

    @Override
    public double getDouble(String path) {
        return ((Number)findProperty(root, path)).doubleValue();
    }

    @Override
    public long getLong(String path) {
        return ((Number)findProperty(root, path)).longValue();
    }

    @Override
    public Map<String, Object> getMap(String path) {
        return ((Map)findProperty(root, path));
    }

    @Override
    public List<String> getStringList(String path) {
        return (List<String>)findProperty(root, path);
    }


    @Override
    public Config getConfig(String path) {
        return new ConfigImpl(getMap(path));
    }

    @Override
    public <T> T get(String path, Class<T> type) {
        return idxGeneric(type, root, path);
    }
}
