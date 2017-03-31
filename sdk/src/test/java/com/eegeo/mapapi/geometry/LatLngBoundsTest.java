package com.eegeo.mapapi.geometry;

import org.junit.Test;

import static org.junit.Assert.*;

public class LatLngBoundsTest {

    @Test
    public void testCreate() {
        LatLng sw = new LatLng(0.0, 0.0);
        LatLng ne = new LatLng(0.0, 0.0);
        LatLngBounds llb = new LatLngBounds(sw, ne);
        assertNotNull("LatLngBounds is not null", llb);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCtorNorthBelowSouthThrowsIllegalArgumentException() {
        LatLng sw = new LatLng(20.0, 0.0);
        LatLng ne = new LatLng(10, 0.0);
        LatLngBounds llb = new LatLngBounds(sw, ne);
    }

    @Test
    public void testIncludingCreateNewBoundsEncompassingPoint() {
        LatLng sw = new LatLng(0.0, 0.0);
        LatLngBounds llb = new LatLngBounds(sw, sw);
        LatLng p = new LatLng(1.0, 2.0);
        LatLngBounds result = llb.including(p);
        LatLngBounds expected = new LatLngBounds(sw, p);
        assertEquals("LatLngBounds.including returns expected result", expected, result);
    }

    @Test
    public void testEqualsSelf() {
        LatLngBounds llb = new LatLngBounds(new LatLng(0.0, 1.0f), new LatLng(2.0, 3.0));
        assertEquals("LatLngBounds is equal to self", llb, llb);
    }

    @Test
    public void testNotEqualsNull() {
        LatLngBounds llb = new LatLngBounds(new LatLng(0.0, 1.0f), new LatLng(2.0, 3.0));
        assertNotEquals("LatLngBounds is not equal to null", llb, null);
    }

    @Test
    public void testBuilderCreate() {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        assertNotNull("LatLngBounds.Builder is not null", b);
    }


    @Test
    public void testBuilderBuildResultsInExpectedForSingleIncludeBuilder() {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        LatLng p = new LatLng(1.0, 2.0);
        LatLngBounds result = b.include(p).build();
        LatLngBounds expected = new LatLngBounds(p, p);
        assertEquals("LatLngBounds.Builder.build for single included point results in expected value", expected, result);
    }

    @Test
    public void testBuilderBuildResultsInExpectedForMultipleIncludeBuilder() {
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        LatLng p = new LatLng(1.0, 2.0);
        LatLngBounds result = b.include(p).include(p).build();
        LatLngBounds expected = new LatLngBounds(p, p);
        assertEquals("LatLngBounds.Builder.build for multiple included points results in expected value", expected, result);
    }

    @Test(expected = IllegalStateException.class)
    public void testBuilderBuildThrowsIllegalStateExceptionIfNoPointIncluded() {
        new LatLngBounds.Builder().build();
    }

}