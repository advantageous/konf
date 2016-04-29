package io.advantageous.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class JsLoaderTest {

    private Config config;

    @Before
    public void setUp() throws Exception {
        config = ConfigLoader.load("test-config.js");
    }

    @Test
    public void testSimple() throws Exception {

        assertEquals(URI.create("http://localhost:8080/foo"), config.get("uri", URI.class));
        assertEquals(URI.create("http://localhost:8080/foo"), config.get("myURI", URI.class));
        assertEquals(1, config.getInt("int1"));
        assertEquals(asList("Foo", "Bar"), config.getStringList("stringList"));
        assertEquals("rick", config.getString("string1"));
        assertEquals(Object.class, config.get("myClass", Class.class));
        assertEquals(1.0, config.getDouble("double1"), 0.001);
        assertEquals(1L, config.getLong("long1"));
        assertEquals(1.0f, config.getFloat("float1"), 0.001);
        System.out.println(config.toString());
    }

    @Test
    public void testReadClass() throws Exception {
        final Employee employee = config.get("employee", Employee.class);
        assertEquals("Geoff", employee.name);
        assertEquals("123", employee.id);
    }

    @Test
    public void testReadListOfClass() throws Exception {
        final List<Employee> employees = config.getList("employees", Employee.class);
        assertEquals("Geoff", employees.get(0).name);
        assertEquals("123", employees.get(0).id);
    }

    @Test
    public void testReadListOfConfig() throws Exception {
        final List<Config> employees = config.getConfigList("employees");
        assertEquals("Geoff", employees.get(0).getString("name"));
        assertEquals("123", employees.get(0).getString("id"));
    }

    @Test
    public void testSimplePath() throws Exception {

        assertTrue(config.hasPath("configInner.int2"));
        assertFalse(config.hasPath("configInner.foo.bar"));
        assertEquals(2, config.getInt("configInner.int2"));
        assertEquals(2.0f, config.getFloat("configInner.float2"), 0.001);
    }

    @Test
    public void testGetConfig() throws Exception {
        final Config configInner = config.getConfig("configInner");
        assertEquals(2, configInner.getInt("int2"));
        assertEquals(2.0f, configInner.getFloat("float2"), 0.001);
    }

    @Test
    public void testGetMap() throws Exception {
        final Map<String, Object> map = config.getMap("configInner");
        assertEquals(2, (int) map.get("int2"));
        assertEquals(2.0f, (double) map.get("float2"), 0.001);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testNoPath() throws Exception {
        config.getInt("department.employees");
    }

    @Test(expected = InvocationTargetException.class)
    public void testPrivateConstructor() throws Exception {
        Assert.assertEquals(0, ConfigLoader.class.getConstructors().length);
        Assert.assertEquals(1, ConfigLoader.class.getDeclaredConstructors().length);
        Constructor constructor = ConfigLoader.class.getDeclaredConstructor();
        Assert.assertNotNull(constructor);
        Assert.assertFalse(constructor.isAccessible());
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testNumberList() throws Exception {
        assertEquals(asList(1.0, 2.0, 3.0), config.getDoubleList("doubles"));
        assertEquals(asList(1.0f, 2.0f, 3.0f), config.getFloatList("floats"));
        assertEquals(asList(1, 2, 3), config.getIntList("ints"));
        assertEquals(asList(1L, 2L, 3L), config.getLongList("longs"));
    }


    @Test
    public void testLoadConfig() throws Exception {
        Config config = ConfigLoader.load("test-config.js");
        URI uri = config.get("myUri", URI.class);
        Assert.assertNotNull(uri);
        Assert.assertEquals("host", uri.getHost());
        Map myMap = config.getMap("someKey");
        Assert.assertNotNull(myMap);
        Assert.assertEquals(234, myMap.get("nestedKey"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadJs() throws Exception {
        ConfigLoader.load("bad-config.js");
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongTypeInList() {
        assertEquals(asList(1, 2, 3), config.getIntList("intsWrongType"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void listHasNull() {
        assertEquals(asList(1, 2, 3), config.getIntList("intsNull"));
    }

    @Test
    public void testDuration() {
        assertEquals(Duration.ofMillis(10), config.getDuration("tenMillis"));
        assertEquals(Duration.ofMillis(10), config.getDuration("tenMilliseconds"));
        assertEquals(Duration.ofSeconds(10), config.getDuration("tenSeconds"));
        assertEquals(Duration.ofMinutes(10), config.getDuration("tenMinutes"));
        assertEquals(Duration.ofHours(10), config.getDuration("tenHours"));
        assertEquals(Duration.ofDays(10), config.getDuration("tenDays"));

        assertEquals(Duration.ofMinutes(15), config.getDuration("fifteenMinutes"));

        assertEquals(Duration.ofSeconds(10), config.getDuration("tenSeconds2"));
        assertEquals(Duration.ofMinutes(10), config.getDuration("tenMinutes2"));
        assertEquals(Duration.ofHours(10), config.getDuration("tenHours2"));
        assertEquals(Duration.ofDays(10), config.getDuration("tenDays2"));
        assertEquals(Duration.ofMillis(10), config.getDuration("tenMillis2"));
        assertEquals(Duration.ofMillis(10), config.getDuration("tenMilliseconds2"));
        assertEquals(asList(Duration.ofSeconds(10)), config.getDurationList("durationList"));


    }

    @Test
    public void testBoolean() {

        /* Just make sure it does not throw an exception. */
        config.getBoolean("macOS");
        config.getBoolean("windows");

    }

    @SuppressWarnings("unused")
    private static class Employee {
        private String id;
        private String name;
    }

}
