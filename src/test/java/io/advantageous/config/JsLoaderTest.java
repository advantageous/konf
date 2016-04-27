package io.advantageous.config;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Map;

public class JsLoaderTest {

    @Test(expected = InvocationTargetException.class)
    public void testPrivateConstructor() throws Exception {
        Assert.assertEquals(0, JsLoader.class.getConstructors().length);
        Assert.assertEquals(1, JsLoader.class.getDeclaredConstructors().length);
        Constructor constructor = JsLoader.class.getDeclaredConstructor();
        Assert.assertNotNull(constructor);
        Assert.assertFalse(constructor.isAccessible());
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testLoadConfig() throws Exception {
        Config config = JsLoader.load("test-config.js");
        URI uri = config.get("myUri", URI.class);
        Assert.assertNotNull(uri);
        Assert.assertEquals("host", uri.getHost());
        Map myMap = config.getMap("someKey");
        Assert.assertNotNull(myMap);
        Assert.assertEquals(234, myMap.get("nestedKey"));
    }
}
