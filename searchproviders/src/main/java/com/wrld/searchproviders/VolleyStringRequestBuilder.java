package com.wrld.searchproviders;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VolleyStringRequestBuilder {

    private int m_method;
    private String m_url;
    private Map<String, String> m_params;
    private Map<String, String> m_headers;
    private Response.Listener<String> m_responseListener;
    private Response.ErrorListener m_errorListener;
    private RetryPolicy m_retryPolicy;

    public VolleyStringRequestBuilder(int method, String url, Response.Listener<String> responseListener){
        m_method = method;
        m_url = url;
        m_responseListener = responseListener;

        m_params = null;
        m_headers = Collections.emptyMap();
        m_errorListener = null;
        m_retryPolicy = null;
    }

    public VolleyStringRequestBuilder setParams(Map<String, String> params){
        m_params = params;
        return this;
    }

    public VolleyStringRequestBuilder setHeaders(Map<String, String> headers){
        m_headers = headers;
        return this;
    }

    public VolleyStringRequestBuilder setErrorListener(Response.ErrorListener errorListener){
        m_errorListener = errorListener;
        return this;
    }

    public VolleyStringRequestBuilder setRetryPolicy(int timeoutMs, int retries){
        m_retryPolicy = new DefaultRetryPolicy(timeoutMs, retries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return this;
    }

    public StringRequest build(){
        StringRequest request = new StringRequest(m_method, m_url, m_responseListener, m_errorListener){
                @Override
                protected Map<String, String> getParams()
                {
                    return m_params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError { return m_headers; }
            };

            if(m_retryPolicy != null){
                request.setRetryPolicy(m_retryPolicy);
            }

            return request;
        }
}
