package io.advantageous.config;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static io.advantageous.boon.core.Maps.map;
import static org.junit.Assert.assertEquals;

public class ConfigImplTest {


    Map map;
    Config config;

    @Before
    public void setUp() throws Exception {


        map = map("int1", 1,
                "float1", 1.0,
                "double1", 1.0,
                "long1", 1L,
                "configInner", map(
                        "int2", 2,
                        "float2", 2.0
                ));
        config = new ConfigImpl(map);
    }

    @Test
    public void testSimple() throws Exception {

        assertEquals(1, config.getInt("int1"));
        assertEquals(1.0f, config.getFloat("float1"), 0.001);
    }


    @Test
    public void testSimplePath() throws Exception {
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
}