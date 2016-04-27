package io.advantageous.config;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.util.Map;

public class JsLoaderTest {

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
