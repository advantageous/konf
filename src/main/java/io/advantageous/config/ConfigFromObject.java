package io.advantageous.config;

import io.advantageous.boon.core.Conversions;
import io.advantageous.boon.core.reflection.Mapper;
import io.advantageous.boon.core.reflection.MapperSimple;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.advantageous.boon.core.Maps.map;
import static io.advantageous.boon.core.reflection.BeanUtils.findProperty;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.*;

/**
 * Turns any Map or Java Object into config.
 * Works with any Java Object tree and or any Nashorn ScriptObjectMirror tree.
 *
 * @author Rick Hightower
 * @author Geoff Chandler
 */
class ConfigFromObject implements Config {

    private final Object root;
    private final Mapper mapper = new MapperSimple();

    private final Map<TimeUnit, List<String>> timeUnitMap = map(
            MICROSECONDS, asList("microseconds", "microsecond", "micros", "micro", "us"),
            MILLISECONDS, asList("milliseconds", "millisecond", "millis", "milli", "ms"),
            SECONDS, asList("seconds", "second", "s"),
            MINUTES, asList("minutes", "minute", "m"),
            HOURS, asList("hours", "hour", "h"),
            DAYS, asList("days", "day", "d")
    );

    <T> ConfigFromObject(T object) {
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
    public Duration getDuration(String path) {
        validatePath(path);

        final Object value = findProperty(root, path);


        if (value instanceof Duration) {
            return (Duration) value;
        }


        if (value instanceof Number) {
            return Duration.ofMillis(((Number) value).longValue());
        }

        if (value instanceof CharSequence) {
            final String durationString = value.toString();
            try {
                return Duration.parse(value.toString());
            } catch (DateTimeParseException dateTimeParse) {

                final Optional<Map.Entry<TimeUnit, List<String>>> entry = timeUnitMap.entrySet().stream().filter(timeUnitListEntry ->
                        timeUnitListEntry.getValue().stream().anyMatch((Predicate<String>)
                                postFix -> durationString.endsWith(postFix))).findFirst();

                if (!entry.isPresent()) {
                    throw new IllegalArgumentException("Path does not resolve to a Duration");
                }
                return entry.map(timeUnitListEntry -> {
                    final Optional<String> postFix = timeUnitListEntry.getValue()
                            .stream().filter(durationString::endsWith).findFirst();
                    final String unitString = durationString.replace(postFix.get(), "").trim();
                    long unit = Long.parseLong(unitString);
                    return Duration.ofNanos(timeUnitListEntry.getKey().toNanos(unit));
                }).get();
            }
        } else {
            throw new IllegalArgumentException("Path does not resolve to a Duration");
        }
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
            value = extractListFromScriptObjectMirror(path, value, CharSequence.class);
        }
        return ((List<CharSequence>) value).stream().map((CharSequence::toString)).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getIntList(String path) {
        return getNumberList(path).stream().map(Number::intValue).collect(Collectors.toList());
    }

    @Override
    public List<Double> getDoubleList(String path) {
        return getNumberList(path).stream().map(Number::doubleValue).collect(Collectors.toList());
    }

    @Override
    public List<Float> getFloatList(String path) {
        return getNumberList(path).stream().map(Number::floatValue).collect(Collectors.toList());
    }

    @Override
    public List<Long> getLongList(String path) {
        return getNumberList(path).stream().map(Number::longValue).collect(Collectors.toList());
    }

    private List<Number> getNumberList(String path) {
        validatePath(path);
        Object value = findProperty(root, path);
        if (value instanceof ScriptObjectMirror) {
            value = extractListFromScriptObjectMirror(path, value, Number.class);
        } else if (value instanceof List) {
            ((List) value).stream().forEach(o -> {
                if (!(o instanceof Number)) {
                    throw new IllegalArgumentException("Path must equate to list with Numbers," +
                            " but found type " + (o == null ? o : o.getClass().getName()));
                }
            });
        }
        return (List<Number>) value;
    }


    @Override
    public Config getConfig(String path) {
        validatePath(path);
        return new ConfigFromObject(getMap(path));
    }

    @Override
    public List<Config> getConfigList(String path) {
        validatePath(path);
        Object value = findProperty(root, path);
        if (value instanceof ScriptObjectMirror) {
            value = extractListFromScriptObjectMirror(path, value, Map.class);
        }
        if (!(value instanceof List)) {
            throw new IllegalArgumentException("Expecting list at location " + path + "but found " + value.getClass());
        }
        final List<Object> list = (List<Object>) value;
        if (list.stream().anyMatch(o -> !(o instanceof Map))) {
            throw new IllegalArgumentException("List must contain config maps only");
        }
        return list.stream().map(o -> (Map<String, Object>) o)
                .map(ConfigFromObject::new)
                .collect(Collectors.toList());

    }

    private Object extractListFromScriptObjectMirror(String path, Object value, Class<?> typeCheck) {
        final ScriptObjectMirror mirror = ((ScriptObjectMirror) value);
        if (mirror.isArray() != true) {
            throw new IllegalArgumentException("Path must resolve to a JS array or java.util.List path = " + path);
        }
        List<Object> list = new ArrayList(mirror.size());
        for (int index = 0; index < mirror.size(); index++) {
            final Object item = mirror.getSlot(index);

            if (item == null) {
                throw new IllegalArgumentException("Path must resolve to a list of " + typeCheck.getName()
                        + " issue at index " + index + " but item is null");
            }
            if (!typeCheck.isAssignableFrom(item.getClass())) {
                throw new IllegalArgumentException("Path must resolve to a list of " + typeCheck.getName()
                        + " issue at index " + index + "but item is " + item.getClass().getName());
            }
            list.add(item);
        }
        return list;
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
            value = extractListFromScriptObjectMirror(path, value, Map.class);
        }

        if (value instanceof List) {
            List<Map> list = (List) value;
            return mapper.convertListOfMapsToObjects(list, componentType);
        } else {
            throw new IllegalArgumentException("Path muse resolve to a java.util.List path = " + path);
        }
    }

    @Override
    public String toString() {
        return "ConfigFromObject{" +
                "root=" + root +
                '}';
    }
}
