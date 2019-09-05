package org.jocean.idiom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class MapUtilTestCase {

    @Test
    public final void testFromStringArray0() {
        final Map<String, String> map1 = MapUtil.fromStringArray();

        assertEquals(0, map1.entrySet().size());
    }

    @Test
    public final void testFromStringArray1() {
        final Map<String, String> map1 = MapUtil.fromStringArray("k1", "v1");

        assertEquals(1, map1.entrySet().size());
        assertEquals("v1", map1.get("k1"));
    }

    @Test
    public final void testFromStringArray2() {
        final Map<String, String> map1 = MapUtil.fromStringArray("k1", "v1", "k2");

        assertEquals(1, map1.entrySet().size());
        assertEquals("v1", map1.get("k1"));
        assertTrue( null == map1.get("k2"));
    }

    @Test
    public final void testFromStringArray3() {
        final Map<String, String> map1 = MapUtil.fromStringArray("k1", "v1", "k2", "v2");

        assertEquals(2, map1.entrySet().size());
        assertEquals("v1", map1.get("k1"));
        assertEquals("v2", map1.get("k2"));
    }
}
