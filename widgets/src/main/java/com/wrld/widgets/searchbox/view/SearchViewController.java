package com.wrld.widgets.searchbox.view;


import android.app.SearchableInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.MenuChild;
import com.wrld.widgets.searchbox.model.MenuOption;
import com.wrld.widgets.searchbox.model.SearchQueryModelListener;
import com.wrld.widgets.searchbox.model.SearchProviderQueryResult;
import com.wrld.widgets.searchbox.model.SearchQuery;
import com.wrld.widgets.searchbox.model.SearchQueryModel;
import com.wrld.widgets.searchbox.model.SuggestionQueryModel;

import java.util.List;

public class SearchViewController implements SearchView.OnQueryTextListener, SearchQueryModelListener, View.OnClickListener, MenuViewListener, View.OnFocusChangeListener {

    private SearchView m_view;
    private SearchQueryModel m_searchModel;
    private SuggestionQueryModel m_suggestionModel;
    private SearchViewFocusObserver m_searchViewFocusObserver;
    private MenuViewObserver m_menuViewObserver;
    private View m_spinnerView;
    private ImageView m_clearButton;
    private ImageView m_magIcon;

    public SearchViewController(SearchQueryModel searchModel,
                                SuggestionQueryModel suggestionModel,
                                SearchView view,
                                SearchViewFocusObserver searchViewFocusObserver,
                                View spinnerView,
                                MenuViewObserver menuViewObserver)
    {
        m_searchModel = searchModel;
        m_suggestionModel = suggestionModel;
        m_searchViewFocusObserver = searchViewFocusObserver;
        m_menuViewObserver = menuViewObserver;

        m_searchModel.addListener(this);

        m_view = view;
        m_view.setOnQueryTextListener(this);

        m_spinnerView = spinnerView;

        initialiseView();
        hideSpinner();

        m_clearButton.setOnClickListener(this);
        m_menuViewObserver.addMenuListener(this);
        m_searchViewFocusObserver.addListener(this);

    }

    public void clean() {
        m_searchViewFocusObserver.removeListener(this);
        m_menuViewObserver.removeMenuListener(this);
        m_searchModel.removeListener(this);
    }

    private void initialiseView() {
        // Manually override and fetch some of the child components of the Android SearchView
        String searchViewEditFrameId = "android:id/search_edit_frame";
        String searchViewSearchIconId = "android:id/search_mag_icon";
        String searchViewSearchPlateId = "android:id/search_plate";
        String searchViewTextViewId = "android:id/search_src_text";
        String searchViewClearButtonId = "android:id/search_close_btn";
        String searchViewMicButtonId = "android:id/search_voice_btn";
        clearMargins(searchViewEditFrameId,0);
        clearMargins(searchViewSearchIconId,0);
        clearMargins(searchViewSearchPlateId,0);
        clearMargins(searchViewMicButtonId,dpToPx(4));
        clearMargins(searchViewClearButtonId,dpToPx(8));

        int searchMicId = m_view.getResources().getIdentifier(searchViewTextViewId, null, null);
        TextView textView = (TextView)m_view.findViewById(searchMicId);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        ViewGroup.LayoutParams params =  textView.getLayoutParams();

        int searchMagIconId = m_view.getResources().getIdentifier(searchViewSearchIconId, null, null);
        m_magIcon = (ImageView)m_view.findViewById(searchMagIconId);
        m_magIcon.setImageResource(R.drawable.search_icon);

        int searchClearButtonId = m_view.getResources().getIdentifier(searchViewClearButtonId, null, null);
        m_clearButton = (ImageView)m_view.findViewById(searchClearButtonId);
        m_clearButton.setImageResource(R.drawable.clear_text_button);
        m_clearButton.getLayoutParams().width = m_clearButton.getLayoutParams().height = dpToPx(16);

        int searchVoiceButtonId = m_view.getResources().getIdentifier(searchViewMicButtonId, null, null);
        ImageView voiceButton = (ImageView)m_view.findViewById(searchVoiceButtonId);
        voiceButton.setImageResource(R.drawable.voice_search_button);
        voiceButton.getLayoutParams().width = voiceButton.getLayoutParams().height = dpToPx(24);

        int textPadding = dpToPx(8);
        params.height = ViewGroup.MarginLayoutParams.MATCH_PARENT;
        textView.setLayoutParams(params);
        textView.setPadding(0,0,textPadding,0);
        textView.setGravity(Gravity.CENTER_VERTICAL);


        m_view.requestLayout();
    }

    private void clearMargins(String childId, int marginOverride) {
        int searchMicId = m_view.getResources().getIdentifier(childId, null, null);
        View view = m_view.findViewById(searchMicId);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
        params.setMargins(marginOverride,marginOverride,marginOverride,marginOverride);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.setMarginStart(marginOverride);
            params.setMarginEnd(marginOverride);
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

        refreshSearchIcon(m_searchViewFocusObserver.hasFocus());

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

    public void setSearchableInfo(SearchableInfo searchableInfo) {
        m_view.setSearchableInfo(searchableInfo);
    }

    @Override
    public void onMenuOpened() {
        m_view.clearFocus();
    }

    @Override
    public void onMenuClosed() {

    }

    @Override
    public void onMenuOptionExpanded(MenuOption option) {

    }

    @Override
    public void onMenuOptionCollapsed(MenuOption option) {

    }

    @Override
    public void onMenuOptionSelected(MenuOption option) {

    }

    @Override
    public void onMenuChildSelected(MenuChild option) {

    }

    @Override
    public void onFocusChange(View view, boolean focused) {
        refreshSearchIcon(focused);
    }

    private void refreshSearchIcon(boolean focused) {
        // TODO: Hide/shrink search icon if focused or have searchView query text
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, m_view.getResources().getDisplayMetrics()));
    }
}
