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
        /* Should be updated when the new Camera API is added to support Interior starting positions */
        /* Ideally also updated to apply all features of a Mapscene via corresponding APIs */

        LatLngAlt startLocation = mapscene.startLocation.startLocation;
        double zoom = CameraPosition.Builder.DistanceToZoom(mapscene.startLocation.startLocationDistanceToInterest);

        CameraPosition position = new CameraPosition.Builder()
                .target(startLocation.latitude, startLocation.longitude)
                .bearing(mapscene.startLocation.startLocationBearing)
                .zoom(zoom)
                .build();

        m_map.moveCamera(CameraUpdateFactory.newCameraPosition(position));


    }
}
