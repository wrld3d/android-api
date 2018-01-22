package com.wrld.widgets.searchbox;

import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.SearchWidgetModel;

/**
 * Created by malcolm.brown on 19/01/2018.
 */

public class WrldSearchWidget extends Fragment {

    private final SearchWidgetModel m_model;

    public WrldSearchWidget() {
        super();

        m_model = new SearchWidgetModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_layout, container, false);
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SearchModule,
                0, 0);

        //m_numSuggestions = a.getInteger(R.styleable.SearchModule_maxSuggestions, 3);
        //m_maxResults  = a.getInteger(R.styleable.SearchModule_maxResults, 3);

        //m_context = context;
    }



}
