package com.eegeo.mapapi.heatmaps;

/**
 * Represents a category of visual feature on an outdoor map, used when styling how heatmaps are
 * drawn when occluded by a map feature.
 */
public enum HeatmapOcclusionMapFeature {
    /**
     * Terrain geometry.
     */
    ground,

    /**
     * Exterior building geometry.
     */
    buildings,

    /**
     * Trees and forest geometry.
     */
    trees,

    /**
     * Road and rail geometry.
     */
    transport
}
