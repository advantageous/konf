package io.advantageous.config;

import io.advantageous.boon.core.Conversions;
import io.advantageous.boon.core.reflection.Mapper;
import io.advantageous.boon.core.reflection.MapperSimple;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.advantageous.boon.core.reflection.BeanUtils.findProperty;

/**
 * @author Rick Hightower
 * @author Geoff Chandler
 */
class ConfigImpl implements Config {

    private final Object root;
    private final Mapper mapper = new MapperSimple();

    <T> ConfigImpl(T object) {
        this.root = object;
    }

    @Override
    public String getString(String path) {
        return findProperty(root, path).toString();
    }

    @Override
    public boolean hasPath(String path) {
        return findProperty(root, path) != null;
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
        if (findProperty(root, path) == null) {
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
        Object value = findProperty(root, path);
        if (value instanceof ScriptObjectMirror) {
            value = extractListFromScriptObjectMirror(path, value);
        }
        return (List<String>) value;
    }


    @Override
    public Config getConfig(String path) {
        validatePath(path);
        return new ConfigImpl(getMap(path));
    }

    @Override
    public List<Config> getConfigList(String path) {
        validatePath(path);
        Object value = findProperty(root, path);
        if (value instanceof ScriptObjectMirror) {
            value = extractListFromScriptObjectMirror(path, value);
        }
        if (!(value instanceof List)) {
            throw new IllegalArgumentException("Expecting list at location " + path + "but found " + value.getClass());
        }
        final List<Object> list = (List<Object>) value;
        if (list.stream().anyMatch(o -> !(o instanceof Map))) {
            throw new IllegalArgumentException("List must contain config maps only");
        }
        return list.stream().map(o -> (Map<String, Object>) o)
                .map(ConfigImpl::new)
                .collect(Collectors.toList());

    }

    private Object extractListFromScriptObjectMirror(String path, Object value) {
        final ScriptObjectMirror mirror = ((ScriptObjectMirror) value);
        if (mirror.isArray()!=true) {
            throw new IllegalArgumentException("Path muse resolve to a JS array or java.util.List path = " + path);
        }
        List<Object> list = new ArrayList(mirror.size());
        for (int index = 0 ; index < mirror.size(); index++) {
            list.add(mirror.getSlot(index));
        }
        value = list;
        return value;
    }

    @Override
    public <T> T get(String path, Class<T> type) {
        validatePath(path);
        final Object value = findProperty(root, path);

        if (type.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else if (value instanceof Map) {
            final Map<String, Object> map = getMap(path);
            return mapper.fromMap(map, type);
        } else {
            return Conversions.coerce(type, value);
        }
    }

    @Override
    public <T> List<T> getList(String path, Class<T> componentType) {

        validatePath(path);

        Object value = findProperty(root, path);
        if (value instanceof ScriptObjectMirror) {
            value = extractListFromScriptObjectMirror(path, value);
        }

        if (value instanceof List) {
            List<Map> list = (List)value;
            return mapper.convertListOfMapsToObjects(list, componentType);
        } else {
            throw new IllegalArgumentException("Path muse resolve to a java.util.List path = " + path);
        }
    }

    @Override
    public String toString() {
        return "ConfigImpl{" +
                "root=" + root +
                '}';
    }
}
