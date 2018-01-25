package com.eegeo.mapapi.services.mapscene;

import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.camera.CameraPosition;
import com.eegeo.mapapi.camera.CameraUpdateFactory;
import com.eegeo.mapapi.geometry.LatLngAlt;

public class MapsceneApplier
{
    private EegeoMap m_map;

    public MapsceneApplier(EegeoMap map)
    {
        this.m_map = map;
    }

    public void ApplyMapscene(final Mapscene mapscene)
    {
        /* Note: Currently only applies camera position due to lacking in APIs for setting the rest. */
        /* Ideally also updated to apply all features of a Mapscene via corresponding APIs */

        LatLngAlt startLocation = mapscene.startLocation.startLocation;

        CameraPosition position = new CameraPosition.Builder()
                .target(startLocation.latitude, startLocation.longitude)
                .bearing(mapscene.startLocation.startLocationBearing)
                .distance(mapscene.startLocation.startLocationDistanceToInterest)
                .indoor(mapscene.startLocation.startLocationIndoorMapId, mapscene.startLocation.startLocationIndoorMapFloorIndex)
                .build();

        m_map.moveCamera(CameraUpdateFactory.newCameraPosition(position));


    }
}
