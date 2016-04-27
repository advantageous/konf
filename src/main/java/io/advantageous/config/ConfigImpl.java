package io.advantageous.config;

import io.advantageous.boon.core.Conversions;
import io.advantageous.boon.core.reflection.Mapper;
import io.advantageous.boon.core.reflection.MapperSimple;

import java.util.List;
import java.util.Map;

import static io.advantageous.boon.core.reflection.BeanUtils.*;


public class ConfigImpl implements Config {

    private final Object root;
    private final Mapper mapper = new MapperSimple();

    public <T> ConfigImpl(T object) {
        this.root = object;
    }

    @Override
    public String getString(String path) {
        return findProperty(root, path).toString();
    }

    @Override
    public boolean hasPath(String path) {
        return findProperty(root, path)!=null;
    }

    @Override
    public int getInt(String path) {
        validatePath(path);
        return ((Number) findProperty(root, path)).intValue();
    }

    @Override
    public float getFloat(String path) {
        validatePath(path);
        return ((Number) findProperty(root, path)).floatValue();
    }

    private void validatePath(String path) {
        if (findProperty(root, path)==null) {
            throw new IllegalArgumentException("Path or property " + path + " does not exist");
        }
    }

    @Override
    public double getDouble(String path) {
        validatePath(path);
        return ((Number) findProperty(root, path)).doubleValue();
    }

    @Override
    public long getLong(String path) {
        validatePath(path);
        return ((Number) findProperty(root, path)).longValue();
    }

    @Override
    public Map<String, Object> getMap(String path) {
        validatePath(path);
        return ((Map) findProperty(root, path));
    }

    @Override
    public List<String> getStringList(String path) {
        validatePath(path);
        return (List<String>) findProperty(root, path);
    }


    @Override
    public Config getConfig(String path) {
        validatePath(path);
        return new ConfigImpl(getMap(path));
    }

    @Override
    public <T> T get(String path, Class<T> type) {
        validatePath(path);
        final Object value = findProperty(root, path);

        if (type.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else if (value instanceof Map){
            final Map<String, Object> map = getMap(path);
            return mapper.fromMap(map, type);
        } else {
            return Conversions.coerce(type, value);
        }
    }

    @Override
    public <T> List<T> getList(String path, Class<T> componentType) {

        validatePath(path);
        List<Map> list = (List)findProperty(root, path);
        return mapper.convertListOfMapsToObjects(list, componentType);
    }

    @Override
    public String toString() {
        return "ConfigImpl{" +
                "root=" + root +
                '}';
    }
}
