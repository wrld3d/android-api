package com.eegeo.web;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiSSIDQuerier
{
    public static String getWifiSSID(Context context)
    {
        Context applicationContext = context.getApplicationContext();
        WifiManager wifiManager = (WifiManager) applicationContext.getSystemService (applicationContext.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();

        if(info == null)
        {
            return "";
        }

        return info.getSSID();
    }
}
