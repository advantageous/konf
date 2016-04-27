package io.advantageous.config;

import static io.advantageous.boon.core.Maps.map;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ConfigImplTest {


    Map map;
    Config config;
    @Before
    public void setUp() throws Exception {


        map = map(  "int1", 1,
                    "float1", 1.0,
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
//        final Config configInner = config.getConfig("configInner");
//        assertEquals(2, configInner.getInt("int1"));
//        assertEquals(2.0f, configInner.getFloat("float1"), 0.001);
    }
}