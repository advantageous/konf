package io.advantageous.config;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;

import static java.lang.Thread.currentThread;

/**
 * Javascript configuration loader.
 *
 * @author Geoff Chandler <geoffc@gmail.com>
 */
public class JsLoader {

    /**
     * Do not allow instantiation of this class.
     */
    private JsLoader() {
        throw new IllegalStateException("this class is not to be instantiated.");
    }

    public static Config load(final String path) {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval(new InputStreamReader(JsLoader.class.getClassLoader().getResourceAsStream("jjs-config-utils.js")));
            engine.eval(new InputStreamReader(currentThread().getContextClassLoader().getResourceAsStream(path)));
        } catch (ScriptException e) {
            throw new IllegalArgumentException("unable to load javascript config at path: " + path);
        }
        return new ConfigImpl(engine.get("config"));
    }

}
