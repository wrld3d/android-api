package com.wrld.searchproviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class VolleyStringRequestBuilder {

    private int m_method;
    private String m_url;
    private HashMap<String, String> m_params;
    private HashMap<String, String> m_headers;
    private Response.Listener<String> m_responseListener;

    public VolleyStringRequestBuilder(int method, String url, Response.Listener<String> responseListener){
        m_method = method;
        m_url = url;
        m_params = new HashMap<String, String>();
        m_headers = new HashMap<String, String>();
        m_responseListener = responseListener;
    }

    public VolleyStringRequestBuilder setParams(HashMap<String, String> params){
        m_params = params;
        return this;
    }

    public VolleyStringRequestBuilder setHeaders(HashMap<String, String> headers){
        m_headers = headers;
        return this;
    }

    public StringRequest build(){
        StringRequest request = new StringRequest(m_method, m_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    m_responseListener.onResponse(response);
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
                    return m_params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return m_headers;
                }
            };

            return request;
        }
}
