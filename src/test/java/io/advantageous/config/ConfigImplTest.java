package io.advantageous.config;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static io.advantageous.boon.core.Maps.map;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class ConfigImplTest {

    private Config config;

    @Before
    public void setUp() throws Exception {

        final Map map = map("int1", 1,
                "float1", 1.0,
                "double1", 1.0,
                "long1", 1L,
                "string1", "rick",
                "stringList", asList("Foo", "Bar"),
                "configInner", map(
                        "int2", 2,
                        "float2", 2.0
                ),
                "uri", URI.create("http://localhost:8080/foo"),
                "myClass", "java.lang.Object",
                "myURI", "http://localhost:8080/foo",
                "employee", map("id", 123, "name", "Geoff"),
                "employees", asList(
                        map("id", 123, "name", "Geoff"),
                        map("id", 456, "name", "Rick"),
                        map("id", 789, "name", "Paul")
                ),
                "floats", asList(1.0, 2.0, 3.0),
                "doubles", asList(1.0, 2.0, 3.0),
                "longs", asList(1.0, 2.0, 3.0),
                "ints", asList(1, 2, 3),
                "intsNull", asList(1, null, 3),
                "intsWrongType", asList(1, "2", 3)
        );
        config = new ConfigFromObject(map);
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
    public void testNumberList() throws Exception {
        assertEquals(asList(1.0, 2.0, 3.0), config.getDoubleList("doubles"));
        assertEquals(asList(1.0f, 2.0f, 3.0f), config.getFloatList("floats"));
        assertEquals(asList(1, 2, 3), config.getIntList("ints"));
        assertEquals(asList(1L, 2L, 3L), config.getLongList("longs"));
    }

    @Test
    public void testSimple() throws Exception {
        assertEquals(Object.class, config.get("myClass", Class.class));
        assertEquals(URI.create("http://localhost:8080/foo"), config.get("uri", URI.class));
        assertEquals(URI.create("http://localhost:8080/foo"), config.get("myURI", URI.class));
        assertEquals(1, config.getInt("int1"));
        assertEquals(asList("Foo", "Bar"), config.getStringList("stringList"));
        assertEquals("rick", config.getString("string1"));
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
        assertEquals(123, employees.get(0).getInt("id"));
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
    public void testGetConfigConvertIntoPojo() throws Exception {
        final Config configInner = config.getConfig("employee");
        final Employee employee = configInner.get("this", Employee.class);
        assertEquals("Geoff", employee.name);
        assertEquals("123", employee.id);
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

    @SuppressWarnings("unused")
    private static class Employee {
        private String id;
        private String name;
    }
}
