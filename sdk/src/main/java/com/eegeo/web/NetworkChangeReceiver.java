package com.eegeo.web;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eegeo.mapapi.INativeMessageRunner;

public class NetworkChangeReceiver extends BroadcastReceiver
{
	public static String NETWORK_STATUS_CHANGED_INTENT = "com.eegeo.web.NETWORK_STATUS_CHANGED";
	protected long m_nativeCallerPointer;
	private INativeMessageRunner m_messageRunner;

	public NetworkChangeReceiver(INativeMessageRunner messageRunner, long nativeCallerPointer)
	{
		m_messageRunner = messageRunner;
		m_nativeCallerPointer = nativeCallerPointer;
	}

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		final Context localContext = context;
		m_messageRunner.runOnNativeThread(new Runnable()
		{
			public void run()
			{
				final int networkStatus = ConnectivityQuerier.getConnectivityStatus(localContext);
				final String ssid = networkStatus == ConnectivityQuerier.WIFI ? WifiSSIDQuerier.getWifiSSID(localContext) : "";
				ConnectivityServiceJniMethods.SetConnectivityType(m_nativeCallerPointer, networkStatus, ssid);
			}
		});
	}
}
