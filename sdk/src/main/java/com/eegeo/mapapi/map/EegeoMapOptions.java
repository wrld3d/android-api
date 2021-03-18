package com.eegeo.mapapi.map;


import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.eegeo.mapapi.R;
import com.eegeo.mapapi.camera.CameraPosition;

public class EegeoMapOptions {

    private CameraPosition m_camera;
    private float m_targetFrameRate = 30.f;
    private String m_coverageTreeManifest = null;
    private String m_environmentThemesManifest = null;

    /**
     * Specifies configuration for creating an EegeoMap. If you add an eeGeo MapView using XML, you
     * can create these options by using XML tags. For further information, see
     * [MapView Resources]({{ site.baseurl }}/docs/api/MapViewResources).
     * <br>
     * If instead you create a MapView programmatically, these options can be passed as a
     * constructor parameter for MapView.
     */
    public EegeoMapOptions() {
    }

    /**
     * Creates a EegeoMapOptions object from an AttributeSet.
     *
     * @param context      The application context
     * @param attributeSet A set of properties specified in an XML resource file
     * @return a new EegeoMapOptions object
     */
    public static EegeoMapOptions createFromAttributeSet(@NonNull Context context, @Nullable AttributeSet attributeSet) {

        EegeoMapOptions eegeoMapOptions = new EegeoMapOptions();
        TypedArray styledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.eegeo_MapView, 0, 0);

        try {
            eegeoMapOptions.camera(new CameraPosition.Builder(styledAttributes).build());
            eegeoMapOptions.coverageTreeManifest(styledAttributes.getString(R.styleable.eegeo_MapView_coverage_tree_manifest));
            eegeoMapOptions.environmentThemesManifest(styledAttributes.getString(R.styleable.eegeo_MapView_environment_themes_manifest));
        } finally {
            styledAttributes.recycle();
        }
        return eegeoMapOptions;

    }

    /**
     * Defines the initial camera viewpoint for the map
     *
     * @param camera A CameraPosition object, specifying the initial position, orientation and zoom
     *               level for the map.
     * @return this EegeoMapOptions object updated with the new camera position.
     */
    public EegeoMapOptions camera(CameraPosition camera) {
        this.m_camera = camera;
        return this;
    }

    /**
     * Defines the coverage tree manifest url for the map.  By default, the map will load the latest
     * public manifest which is updated regularly.
     *
     * @param coverageTreeManifest A String, specifying the coverage tree manifest url for the map.
     * @return this EegeoMapOptions object updated with the specified coverage tree manifest.
     */
    public EegeoMapOptions coverageTreeManifest(String coverageTreeManifest) {
        this.m_coverageTreeManifest = coverageTreeManifest;
        return this;
    }

    /**
     * Defines the environment themes manifest url for the map. By default, the map will load the latest
     * themes manifest which is updated regularly.
     *
     * @param environmentThemesManifest A String, specifying the environment themes manifest url for the map.
     * @return this EegeoMapOptions object updated with the specified environment themes manifest.
     */
    public EegeoMapOptions environmentThemesManifest(String environmentThemesManifest) {
        this.m_environmentThemesManifest = environmentThemesManifest;
        return this;
    }

    /**
     * @return the camera option
     */
    public CameraPosition getCamera() {
        return m_camera;
    }

    /**
     * @return the target frame rate option
     */
    public float getTargetFrameRate() {
        return m_targetFrameRate;
    }

    /**
     * @return the coverage tree manifest option
     */
    public String getCoverageTreeManifest() { return m_coverageTreeManifest; }

    /**
     * @return the environment themes manifest option
     */
    public String getEnvironmentThemesManifest() { return m_environmentThemesManifest; }
}
