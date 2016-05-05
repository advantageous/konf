package io.advantageous.config;

import java.net.URI;
import java.time.Duration;
import java.util.*;

class Configs implements Config {

    private final List<Config> configList;

    Configs(Config... configs) {
        this.configList = Collections.unmodifiableList(Arrays.asList(configs));
    }


    @Override
    public ConfigMemorySize getMemorySize(final String path) {
        return findConfig(path).getMemorySize(path);
    }

    private Config findConfig(String path) {
        final Optional<Config> configOptional =
                configList.stream().filter(config -> config.hasPath(path)).findFirst();
        if (!configOptional.isPresent()) {
            throw new IllegalArgumentException("Path path = " + path + " not found.");
        }
        return configOptional.get();
    }

    @Override
    public List<ConfigMemorySize> getMemorySizeList(String path) {
        return findConfig(path).getMemorySizeList(path);
    }

    @Override
    public URI getUri(String path) {
        return findConfig(path).getUri(path);
    }

    @Override
    public List<URI> getUriList(String path) {
        return findConfig(path).getUriList(path);
    }

    @Override
    public String getString(String path) {
        return findConfig(path).getString(path);
    }

    @Override
    public boolean hasPath(String path) {
        return configList.stream().filter(config -> config.hasPath(path)).findFirst().isPresent();
    }

    @Override
    public int getInt(String path) {
        return findConfig(path).getInt(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return findConfig(path).getBoolean(path);
    }

    @Override
    public List<Boolean> getBooleanList(String path) {
        return findConfig(path).getBooleanList(path);
    }

    @Override
    public float getFloat(String path) {
        return findConfig(path).getFloat(path);
    }

    @Override
    public double getDouble(String path) {
        return findConfig(path).getDouble(path);
    }

    @Override
    public long getLong(String path) {
        return findConfig(path).getLong(path);
    }

    @Override
    public Duration getDuration(String path) {
        return findConfig(path).getDuration(path);
    }

    @Override
    public List<Duration> getDurationList(String path) {
        return findConfig(path).getDurationList(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return findConfig(path).getStringList(path);
    }

    @Override
    public List<Integer> getIntList(String path) {
        return findConfig(path).getIntList(path);
    }

    @Override
    public List<Double> getDoubleList(String path) {
        return findConfig(path).getDoubleList(path);
    }

    @Override
    public List<Float> getFloatList(String path) {
        return findConfig(path).getFloatList(path);
    }

    @Override
    public List<Long> getLongList(String path) {
        return findConfig(path).getLongList(path);
    }

    @Override
    public Map<String, Object> getMap(String path) {
        return findConfig(path).getMap(path);
    }

    @Override
    public Config getConfig(String path) {
        return findConfig(path).getConfig(path);
    }

    @Override
    public List<Config> getConfigList(String path) {
        return findConfig(path).getConfigList(path);
    }

    @Override
    public <T> T get(String path, Class<T> type) {
        return findConfig(path).get(path, type);
    }

    @Override
    public <T> List<T> getList(String path, Class<T> componentType) {
        return findConfig(path).getList(path, componentType);
    }
}
