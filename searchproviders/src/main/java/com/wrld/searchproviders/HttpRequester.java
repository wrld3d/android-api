// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.searchproviders;

import android.content.Context;
import android.support.annotation.UiThread;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpRequester {
    private RequestQueue m_requestQueue;

    public interface StringResponseHandler {
        void responseReceived(String response);
    }

    public HttpRequester(Context context){
        m_requestQueue = Volley.newRequestQueue(context);
    }

    public void startQueue(){
        m_requestQueue.start();
    }

    @UiThread
    public void makeStringRequest(int method, String url, final HashMap<String, String> params, final StringResponseHandler responseHandler) {

        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseHandler.responseReceived(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        android.util.Log.w("httpRequest", error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
        };

        m_requestQueue.add(stringRequest);
    }

    @UiThread
    public void makeStringRequestWithHeaders(int method, String url, final HashMap<String, String> headers, final StringResponseHandler responseHandler) {

        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseHandler.responseReceived(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        android.util.Log.w("httpRequest", error.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        m_requestQueue.add(stringRequest);
    }

    public void makeStringRequest(int method, String url, final HashMap<String, String> headers, final HashMap<String, String> params, final StringResponseHandler responseHandler) {

        StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    responseHandler.responseReceived(response);
                }
            },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        android.util.Log.w("httpRequest", error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams()
            {
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        m_requestQueue.add(stringRequest);
    }
}
