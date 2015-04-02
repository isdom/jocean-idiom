package org.jocean.idiom;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class JOArraysTestCase {

    @Test
    public void testAddFirstForNullSrc() {
        final Integer[] copied = JOArrays.addFirst(null, new Integer(1), Integer[].class);
        assertTrue( Arrays.equals(new Integer[]{1}, copied) );
    }

    @Test
    public void testAddFirstFor23() {
        final Integer[] copied = JOArrays.addFirst(new Integer[]{2,3}, new Integer(1), Integer[].class);
        assertTrue( Arrays.equals(new Integer[]{1,2,3}, copied) );
    }
}
