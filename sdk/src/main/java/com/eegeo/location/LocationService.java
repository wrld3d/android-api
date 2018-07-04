// Copyright eeGeo Ltd (2012-2014), All Rights Reserved

package com.eegeo.location;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.eegeo.mapapi.INativeMessageRunner;

@SuppressWarnings("unused")
public class LocationService {
    private final int m_ctorThreadId;

    private final Context m_context;
    private final INativeMessageRunner m_nativeMessageRunner;
    private final long m_nativeCallerPointer;

    private Location m_bestLocation = null;
    private boolean m_isListening = false;
    private boolean m_isAuthorized = false;

    private LocationListener m_locationListener;
    private LocationManager m_locationManager;

    private static int getThreadId() {
        return android.os.Process.myTid();
    }

    private void throwIfCalledOnIncorrectThread() {
        int currentThreadId = getThreadId();

        if(getThreadId() != m_ctorThreadId) {
            throw new IllegalThreadStateException(
                    "was not called on a consistent thread; ctor was called on thread with id '" +
                            m_ctorThreadId + "', but this thread has id '" + currentThreadId + "'");
        }
    }

    @SuppressWarnings("unused")
    public LocationService(@NonNull Context context, @NonNull INativeMessageRunner nativeMessageRunner, long nativeCallerPointer) {
        //noinspection ConstantConditions
        if(context == null) {
            throw new NullPointerException("context");
        }

        //noinspection ConstantConditions
        if(nativeMessageRunner == null) {
            throw new NullPointerException("nativeMessageRunner");
        }

        m_context = context;
        m_nativeMessageRunner = nativeMessageRunner;
        m_nativeCallerPointer = nativeCallerPointer;
        m_ctorThreadId = getThreadId();
    }

    @SuppressWarnings("unused")
    public void startListeningToUpdates() {
        throwIfCalledOnIncorrectThread();

        if (m_isListening) {
            return;
        }

        setupListenerAndLocationManager();

        forceLocationFromCachedProviders(m_locationManager);

        try {
            if (m_locationListener != null) {
                m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, m_locationListener);
                m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, m_locationListener);
            }
        } catch (Exception e) {
            Log.v("Location", e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void stopListeningToUpdates() {
        throwIfCalledOnIncorrectThread();

        if (m_isListening) {
            m_locationManager.removeUpdates(m_locationListener);
            tearDownListener();
            updateNativeAuthorized(false);
        }
    }

    private void forceLocationFromCachedProviders(LocationManager locationManager) {
        List<String> providers = locationManager.getProviders(true);
        Location bestCachedLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (isBetterLocation(l, bestCachedLocation)) {
                bestCachedLocation = l;
            }
        }

        m_bestLocation = bestCachedLocation;

        if (m_bestLocation != null) {
            updateNativeLocation(
                    m_bestLocation.getLatitude(),
                    m_bestLocation.getLongitude(),
                    m_bestLocation.getAltitude(),
                    m_bestLocation.getAccuracy());
        }

        Log.v("Location", "best location set from cache");
    }

    private void tearDownListener() {
        if (m_locationManager != null) {
            m_locationManager = null;
        }

        if (m_locationListener != null) {
            m_locationListener = null;
            m_isListening = false;
        }
    }

    private boolean isAnyProviderEnabled(LocationManager locationManager) {
        boolean gpsIsAuthorized = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkIsAuthorized = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return (gpsIsAuthorized || networkIsAuthorized);
    }

    private void setupListenerAndLocationManager() {
        m_locationManager = (LocationManager) m_context.getSystemService(Context.LOCATION_SERVICE);

        m_isAuthorized = isAnyProviderEnabled(m_locationManager);

        if (m_locationListener == null) {
            m_locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if (isBetterLocation(location, m_bestLocation)) {
                        m_bestLocation = location;

                        updateNativeLocation(
                                m_bestLocation.getLatitude(),
                                m_bestLocation.getLongitude(),
                                m_bestLocation.getAltitude(),
                                m_bestLocation.getAccuracy());

                        Log.v("Location", "best updated from onLocationChanged : " + location.getLatitude() + " , " + location.getLongitude() + " , " + location.getAltitude());
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.v("Location", "onStatusChanged");
                }

                public void onProviderEnabled(String provider) {
                    m_isAuthorized = isAnyProviderEnabled(m_locationManager);
                    updateNativeAuthorized(m_isAuthorized);
                    Log.v("Location", "onProviderEnabled, LocationService.m_isAuthorized : " + m_isAuthorized);
                }

                public void onProviderDisabled(String provider) {
                    m_isAuthorized = isAnyProviderEnabled(m_locationManager);
                    updateNativeAuthorized(m_isAuthorized);
                    Log.v("Location", "onProviderDisabled, LocationService.m_isAuthorized : " + m_isAuthorized);
                }
            };
        }
        m_isListening = true;
    }

    // Hoick http://developer.android.com/guide/topics/location/strategies.html
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();

        final int ONE_MINUTE = 1000 * 60;
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTE;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private void updateNativeLocation(
            final double latitudeDegrees,
            final double longitudeDegrees,
            final double altitudeMeters,
            final double horizontalAccuracyMeters) {

        throwIfCalledOnIncorrectThread();

        LocationServiceJniMethods.UpdateLocation(
                m_nativeCallerPointer,
                latitudeDegrees,
                longitudeDegrees,
                altitudeMeters,
                horizontalAccuracyMeters);
    }

    private void updateNativeAuthorized(final boolean isAuthorized) {
        throwIfCalledOnIncorrectThread();

        LocationServiceJniMethods.UpdateAuthorized(
                m_nativeCallerPointer,
                isAuthorized);

    }
}