package com.eegeo.mapapi.geometry;

import org.junit.Test;

import static org.junit.Assert.*;

public class LatLngTest {

    private final static double epsilon = 1e-12;

    @Test
    public void testCreate() {
        LatLng latLng = new LatLng(0.0, 0.0);
        assertNotNull("latLng is not null", latLng);
    }

    @Test
    public void testCtorFieldsInitialised() {
        double lat = 12.3;
        double lng = 45.6;
        LatLng latLng = new LatLng(lat, lng);
        assertEquals("latitude equals", latLng.latitude, lat, epsilon);
        assertEquals("longitude equals", latLng.longitude, lng, epsilon);
    }

    @Test
    public void testEqualsOther() {
        double lat = 12.3;
        double lng = 45.6;
        LatLng a = new LatLng(lat, lng);
        LatLng b = new LatLng(lat, lng);
        assertEquals("LatLng objects are equal", a, b);
    }

    @Test
    public void testEqualsSelf() {
        double lat = 12.3;
        double lng = -45.6;
        LatLng a = new LatLng(lat, lng);
        assertEquals("LatLng objects are equal", a, a);
    }

    @Test
    public void testDoesNotEqualNull() {
        LatLng a = new LatLng(0.0, 0.0);
        assertNotEquals("LatLng does not equal null", a, null);
    }

}