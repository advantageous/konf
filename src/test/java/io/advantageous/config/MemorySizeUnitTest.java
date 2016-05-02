package io.advantageous.config;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by rick on 5/1/16.
 */
public class MemorySizeUnitTest {
    @Test
    public void parse() throws Exception {

        assertEquals(10, MemorySizeUnit.parse("10 b"));
        assertEquals(10, MemorySizeUnit.parse("10bytes"));
        assertEquals(10, MemorySizeUnit.parse("10byte"));
        assertEquals(10_000_000, MemorySizeUnit.parse("10 MB"));
        assertEquals(10_000_000, MemorySizeUnit.parse("10megabyte"));
        assertEquals(10_000_000, MemorySizeUnit.parse("10   megabytes "));
        assertEquals(10_000_000_000L, MemorySizeUnit.parse(" 10    gigabytes "));
        assertEquals(10_000_000_000L, MemorySizeUnit.parse(" 10    GB "));
        assertEquals(10_000_000_000L, MemorySizeUnit.parse("10gigabyte"));
        assertEquals(MemorySizeUnit.ZETTA_BYTES.getMultiplier(), MemorySizeUnit.parse(" 1    zettabytes "));
    }

}