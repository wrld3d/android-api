package com.wrld.widgets.searchbox.view;


import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.wrld.widgets.searchbox.model.IOnSearchListener;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchQuery;
import com.wrld.widgets.searchbox.model.SearchWidgetSearchModel;
import com.wrld.widgets.searchbox.model.SearchWidgetSuggestionModel;

import java.util.ArrayList;
import java.util.List;

public class SearchViewController implements SearchView.OnQueryTextListener, IOnSearchListener, View.OnClickListener {

    private SearchView m_view;
    private SearchWidgetSearchModel m_searchModel;
    private SearchWidgetSuggestionModel m_suggestionModel;
    private SearchViewFocusObserver m_searchViewFocusObserver;
    private View m_spinnerView;
    private ImageView m_clearButton;

    public SearchViewController(SearchWidgetSearchModel searchModel,
                                SearchWidgetSuggestionModel suggestionModel,
                                SearchView view,
                                SearchViewFocusObserver searchViewFocusObserver,
                                View spinnerView)
    {
        m_searchModel = searchModel;
        m_suggestionModel = suggestionModel;
        m_searchViewFocusObserver = searchViewFocusObserver;
        m_searchModel.setSearchListener(this);

        m_view = view;
        m_view.setOnQueryTextListener(this);

        m_spinnerView = spinnerView;

        initialiseView();
        hideSpinner();

        m_clearButton.setOnClickListener(this);

    }

    private void initialiseView() {
        clearMargins("android:id/search_edit_frame");
        clearMargins("android:id/search_mag_icon");
        clearMargins("android:id/search_plate");

        int searchMicId = m_view.getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView)m_view.findViewById(searchMicId);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        ViewGroup.LayoutParams params =  textView.getLayoutParams();

        int searchClearButtonId = m_view.getResources().getIdentifier("android:id/search_close_btn", null, null);
        m_clearButton = (ImageView)m_view.findViewById(searchClearButtonId);

        int textPadding = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, m_view.getResources().getDisplayMetrics()));
        params.height = ViewGroup.MarginLayoutParams.MATCH_PARENT;//
        textView.setLayoutParams(params);
        textView.setPadding(0,0,textPadding,0);
        textView.setGravity(Gravity.CENTER_VERTICAL);


        m_view.requestLayout();
    }

    private void clearMargins(String childId) {
        int searchMicId = m_view.getResources().getIdentifier(childId, null, null);
        View view = m_view.findViewById(searchMicId);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
        params.setMargins(0,0,0,0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.setMarginStart(0);
            params.setMarginEnd(0);
        }

        params.height = ViewGroup.MarginLayoutParams.MATCH_PARENT;
        view.setPadding(0,0,0,0);
        view.setLayoutParams(params);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        m_view.clearFocus();
        if(!TextUtils.isEmpty(s)) {
            m_searchModel.doSearch(s);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        boolean hasQueryAndNewTextDiffers = (m_searchModel.getCurrentQuery() != null &&
                !s.contentEquals(m_searchModel.getCurrentQuery().getQueryString()));
        if(m_searchViewFocusObserver.hasFocus() && hasQueryAndNewTextDiffers) {
            m_searchModel.clear();
        }

        if(!s.isEmpty())
        {
            m_suggestionModel.doSuggestions(s);
        }
        else
        {
            m_suggestionModel.clear();
        }
        return false;
    }

    @Override
    public void onSearchQueryStarted(SearchQuery query) {
        m_suggestionModel.clear();

        m_view.setQuery(query.getQueryString(), false);
        showSpinner();
    }

    @Override
    public void onSearchQueryCompleted(SearchQuery query, List<SearchProviderQueryResult> results) {
        hideSpinner();
    }

    @Override
    public void onSearchQueryCancelled(SearchQuery query) {
        hideSpinner();
    }

    private void showSpinner() {
        expand(m_spinnerView);
    }

    private void hideSpinner() {
        collapse(m_spinnerView);
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int targetHeight = v.getMeasuredHeight();
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, targetHeight);
        final int trueTargetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(trueTargetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(200);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(200);
        v.startAnimation(a);
    }

    @Override
    public void onClick(View view) {
        if(view == m_clearButton) {
            m_view.setQuery("", false);
            m_searchModel.clear();
            m_suggestionModel.clear();
        }
    }
}
