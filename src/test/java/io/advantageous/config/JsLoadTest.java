package io.advantageous.config;

import org.junit.Test;

import java.io.File;
import java.net.URI;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsLoadTest {

    @Test(expected = IllegalArgumentException.class)
    public void loadDontExist() {
        ConfigLoader.load("crapthatdontexist");
    }

    @Test
    public void loadClasspathURI() {
        final Config config = ConfigLoader.load("classpath:test-config.js");
        assertEquals(asList(1.0, 2.0, 3.0), config.getDoubleList("doubles"));
    }


    @Test
    public void loadClasspathURI2() {
        final Config config = ConfigLoader.load("classpath:/test-config.js");
        assertEquals(asList(1.0, 2.0, 3.0), config.getDoubleList("doubles"));
    }


    @Test
    public void loadClasspathURI3() {
        final Config config = ConfigLoader.load("classpath://test-config.js");
        assertEquals(asList(1.0, 2.0, 3.0), config.getDoubleList("doubles"));
    }

    @Test
    public void loadAsClassResources() {
        final Config config = ConfigLoader.load("test-config.js");
        assertEquals(asList(1.0, 2.0, 3.0), config.getDoubleList("doubles"));
    }


    @Test
    public void testFile() {
        File file = new File("./src/test/resources/test-config.js");
        assertTrue(file.exists());
        System.out.println(file.toURI().toString());
        final Config config = ConfigLoader.load(file.toURI().toString());
        assertEquals(asList(1.0, 2.0, 3.0), config.getDoubleList("doubles"));
    }


    @Test
    public void testFile2() {
        File file = new File("./src/test/resources/test-config.js");
        file = file.getAbsoluteFile();
        final URI uri = URI.create("file://" + file.toString());
        System.out.println(uri);
        final Config config = ConfigLoader.load(uri.toString());
        assertEquals(asList(1.0, 2.0, 3.0), config.getDoubleList("doubles"));
    }
}
