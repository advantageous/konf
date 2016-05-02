package io.advantageous.config;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigMemorySizeTest {

    @Test
    public void equals() throws Exception {

        final ConfigMemorySize unit1 = ConfigMemorySize.valueOf(" 10 b ");
        final ConfigMemorySize unit2 = MemorySizeUnit.parseToConfigMemorySize(" 10 b ");
        final ConfigMemorySize unit3 = MemorySizeUnit.parseToConfigMemorySize(" 1 b ");

        assertTrue(unit1.equals(unit2));
        assertFalse(unit1.equals(unit3));
    }

    @Test
    public void hashCodeTest() throws Exception {


        final ConfigMemorySize unit1 = MemorySizeUnit.parseToConfigMemorySize(" 10 b ");
        final ConfigMemorySize unit2 = MemorySizeUnit.parseToConfigMemorySize(" 10 b ");


        assertTrue(unit1.hashCode() == unit2.hashCode());
    }

    @Test
    public void toStringTest() throws Exception {


        final ConfigMemorySize unit1 = MemorySizeUnit.parseToConfigMemorySize(" 10 b ");
        final ConfigMemorySize unit2 = MemorySizeUnit.parseToConfigMemorySize(" 10 b ");

        assertTrue(unit1.toString().equals(unit2.toString()));
    }

}