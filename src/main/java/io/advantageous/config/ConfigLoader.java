package io.advantageous.config;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import static io.advantageous.config.ResourceUtils.findResource;

/**
 * Javascript configuration loader.
 *
 * @author Geoff Chandler geoffc@gmail.com
 * @author Rick Hightower
 */
@SuppressWarnings("WeakerAccess")
public class ConfigLoader {

    /**
     * Do not allow instantiation of this class.
     */
    private ConfigLoader() {
        throw new IllegalStateException("this class is not to be instantiated.");
    }

    /**
     * Works with any nested structure of maps and lists and basic Java types.
     * Works with Pojos, Maps, Lists.
     *
     * @param rootOfConfig rootConfig object
     * @return Config version of object map.
     */
    public static Config loadFromObject(final Object rootOfConfig) {
        return new ConfigFromObject(rootOfConfig);
    }

    /**
     * Creates a chain of configs.
     * @param configs var args of configs. Searched from left to right so that the
     *                left side overrides the right side.
     *
     * @return chain of configs as a single config. Left side is most significant.
     */
    public static Config configWithFallbacks(final Config[] configs) {
        return new Configs(configs);
    }

    /**
     * Alias for `configWithFallbacks`.
     * Creates a chain of configs.
     * @param configs var args of configs. Searched from left to right so that the
     *                left side overrides the right side.
     *
     * @return chain of configs as a single config. Left side is most significant.
     */
    public static Config configs(final Config... configs) {
        return configWithFallbacks(configs);
    }

    /**
     * Alias for `load`. Loads a config file.
     *
     * @param resources classpath resources to from which to load javascript
     * @return Config.
     */
    public static Config config(final String... resources) {
        return load(resources);
    }

    /**
     * Loads a config file.
     *
     * @param resources classpath resources to from which to load javascript
     * @return Config.
     */
    public static Config load(final String... resources) {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try (final InputStream resourceAsStream = findResource("jjs-config-utils.js")) {
            engine.eval(new InputStreamReader(resourceAsStream));
            loadResources(engine, resources);
        } catch (final ScriptException se) {
            throw new IllegalArgumentException("unable to execute main javascript.", se);
        } catch (final Exception ex) {
            if (ex instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) ex;
            }
            throw new IllegalArgumentException("unable to load main resource ", ex);
        }
        return loadFromObject(engine.get("config"));
    }

    private static void loadResources(ScriptEngine engine, String[] resources) {
        for (final String resource : resources) {
            try (final InputStream resource1 = findResource(resource)) {
                engine.eval(new InputStreamReader(resource1));
            } catch (final Exception e) {
                throw new IllegalArgumentException("unable to execute javascript. " + resource, e);
            }
        }
    }


}
