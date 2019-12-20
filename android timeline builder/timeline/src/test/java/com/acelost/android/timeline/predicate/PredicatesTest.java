package com.acelost.android.timeline.predicate;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredicatesTest {

    @Test
    public void assert_alwaysTrueReturnsTrue() {
        assertTrue(Predicates.alwaysTrue().evaluate(new Object()));
    }

    @Test
    public void assert_inReturnsTrueIfValueInArray() {
        final String[] array = new String[] { "Foo", "Bar", "Baz" };

        assertTrue(Predicates.in(array).evaluate("Bar"));
    }

    @Test
    public void assert_inReturnsFalseInfValueNotInArray() {
        final String[] array = new String[] { "Foo", "Bar", "Baz" };

        assertFalse(Predicates.in(array).evaluate("Boo"));
    }

    @Test
    public void assert_inReturnsTrueIfValueInCollection() {
        final List<String> collection = Arrays.asList("Foo", "Bar", "Baz");

        assertTrue(Predicates.in(collection).evaluate("Baz"));
    }

    @Test
    public void assert_inReturnsFalseIfValueNotInCollection() {
        final List<String> collection = Arrays.asList("Foo", "Bar", "Baz");

        assertFalse(Predicates.in(collection).evaluate("Boo"));
    }

}
