package org.jocean.idiom;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class OrderedTestCase {

    @Test
    public final void testASC() {
        final Ordered small = new Ordered() {
            @Override
            public int ordinal() {
                return 0;
            }};
        final Ordered big= new Ordered() {
            @Override
            public int ordinal() {
                return 100;
            }};
        final Ordered[] sorted = new Ordered[]{big, small};
        
        Arrays.sort(sorted, Ordered.ASC);
        
        assertTrue(Arrays.equals(new Ordered[]{small, big}, sorted));
    }

    @Test
    public final void testDESC() {
        final Ordered small = new Ordered() {
            @Override
            public int ordinal() {
                return 0;
            }};
        final Ordered big= new Ordered() {
            @Override
            public int ordinal() {
                return 100;
            }};
        final Ordered[] sorted = new Ordered[]{small, big};
        
        Arrays.sort(sorted, Ordered.DESC);
        
        assertTrue(Arrays.equals(new Ordered[]{big, small}, sorted));
    }
}
