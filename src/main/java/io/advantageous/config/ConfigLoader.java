package io.advantageous.config;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.URI;
import java.net.URL;

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
            try (final InputStream resource1 = findResource(resource)){
                engine.eval(new InputStreamReader(resource1));
            } catch (final Exception e) {
                throw new IllegalArgumentException("unable to execute javascript. " + resource, e);
            }
        }
    }

    private static InputStream findResource(final String resourceName) {
        InputStream resourceAsStream = ConfigLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (resourceAsStream == null) {
            resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        }

        if (resourceAsStream == null) {
            final URI uri = URI.create(resourceName);
            if (uri.getScheme().equals("file")) {
                try {
                    final String path = uri.getPath();
                    resourceAsStream = new FileInputStream(new File(path));
                } catch (FileNotFoundException e) {
                    throw new IllegalArgumentException("File resource could not be loaded " + resourceName);
                }
            } else if (uri.getScheme().equals("http")) {
                try {
                    URL url = uri.toURL();
                    resourceAsStream = url.openStream();
                } catch (Exception e) {
                    throw new IllegalArgumentException("Web resource could not be loaded " + resourceName);
                }
            } else if (uri.getScheme().equals("classpath")) {
                String path = uri.getSchemeSpecificPart();
                if (path.startsWith("//")) {
                    path = path.substring(2);
                } else if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                resourceAsStream = ConfigLoader.class.getClassLoader().getResourceAsStream(path);
                if (resourceAsStream == null) {
                    resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
                }
            }
        }

        if (resourceAsStream == null) {
            throw new IllegalArgumentException("resources could not be loaded " + resourceName);
        }

        return resourceAsStream;
    }

}
