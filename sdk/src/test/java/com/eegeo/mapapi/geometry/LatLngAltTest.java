package com.eegeo.mapapi.geometry;

import org.junit.Test;

import static org.junit.Assert.*;

public class LatLngAltTest {

    private final static double epsilon = 1e-12;


    @Test
    public void testCreate() {
        LatLngAlt latLngAlt = new LatLngAlt(0.0, 0.0, 0.0);
        assertNotNull("latLngAlt is not null", latLngAlt);
    }

    @Test
    public void testCtorFieldsInitialised() {
        double lat = 12.3;
        double lng = 45.6;
        double alt = 78.9;
        LatLngAlt latLngAlt = new LatLngAlt(lat, lng, alt);
        assertEquals("latitude equals", latLngAlt.latitude, lat, epsilon);
        assertEquals("longitude equals", latLngAlt.longitude, lng, epsilon);
        assertEquals("altitude equals", latLngAlt.altitude, alt, epsilon);
    }

    @Test
    public void testEqualsOther() {
        double lat = 12.3;
        double lng = 45.6;
        double alt = 78.9;
        LatLngAlt a = new LatLngAlt(lat, lng, alt);
        LatLngAlt b = new LatLngAlt(lat, lng, alt);
        assertEquals("LatLngAlt objects are equal", a, b);
    }

    @Test
    public void testEqualsSelf() {
        double lat = 12.3;
        double lng = -45.6;
        double alt = 78.9;
        LatLngAlt a = new LatLngAlt(lat, lng, alt);
        assertEquals("LatLngAlt objects are equal", a, a);
    }

    @Test
    public void testDoesNotEqualNull() {
        LatLngAlt a = new LatLngAlt(0.0, 0.0, 0.0);
        assertNotEquals("LatLngAlt does not equal null", a, null);
    }

    @Test
    public void toLatLng() {
        LatLngAlt lla = new LatLngAlt(0.0, 0.0, 0.0);
        LatLng ll = lla.toLatLng();
        assertNotNull("LatLngAlt.toLatLng() results in LatLng", ll);
    }

    @Test
    public void testToLatLngFieldsEqual() {
        double lat = 12.3;
        double lng = 45.6;
        double alt = 78.9;
        LatLngAlt lla = new LatLngAlt(lat, lng, alt);
        LatLng ll = lla.toLatLng();
        assertEquals(ll.latitude, lla.latitude, epsilon);
        assertEquals(ll.longitude, lla.longitude, epsilon);
    }

}