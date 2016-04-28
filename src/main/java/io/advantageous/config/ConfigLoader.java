package io.advantageous.config;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;

import static java.lang.Thread.currentThread;

/**
 * Javascript configuration loader.
 *
 * @author Geoff Chandler geoffc@gmail.com
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
     * Loads a config file.
     *
     * @param resources classpath resources to from which to load javascript
     * @return Config.
     */
    public static Config load(final String... resources) {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval(new InputStreamReader(
                    ConfigLoader.class.getClassLoader().getResourceAsStream("jjs-config-utils.js")));

            for (final String resource : resources) {
                try {
                    engine.eval(new InputStreamReader(
                            currentThread().getContextClassLoader().getResourceAsStream(resource)));
                } catch (final Exception e) {
                    throw new IllegalArgumentException("unable to execute javascript. " + resource, e);
                }
            }
        } catch (final ScriptException e) {
            throw new IllegalArgumentException("unable to execute javascript.", e);
        }
        return loadFromObject(engine.get("config"));
    }

}
