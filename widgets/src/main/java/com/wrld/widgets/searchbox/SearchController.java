package com.wrld.widgets.searchbox;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.wrld.widgets.R;
import com.wrld.widgets.ui.UiScreenController;
import com.wrld.widgets.ui.UiScreenMemento;
import com.wrld.widgets.ui.UiScreenMementoOriginator;
import com.wrld.widgets.ui.UiScreenVisibilityState;

class SearchController implements UiScreenController, UiScreenMementoOriginator<UiScreenVisibilityState> {

    private View m_rootView;
    private SearchView m_searchView;

    private Animation m_showAnim;
    private Animation m_hideAnim;

    private ScreenState m_screenState;

    public ScreenState getScreenState() { return m_screenState; }

    private SearchModuleController m_searchModuleMediator;

    private boolean m_performSuggestionOnChange;

    SearchController(ViewGroup searchBoxRootContainer, SearchModuleController searchModuleMediator) {

        m_searchModuleMediator = searchModuleMediator;
        m_performSuggestionOnChange = true;

        m_rootView = searchBoxRootContainer;
        m_searchView = (SearchView) searchBoxRootContainer.findViewById(R.id.searchbox_search_searchview);

        final UiScreenController selfAsScreenController = this;

        m_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                @Override
                                                public boolean onQueryTextSubmit(String query) {
                                                    performSearch();
                                                    return true;
                                                }

                                                @Override
                                                public boolean onQueryTextChange(String newText) {
                                                    if (!TextUtils.isEmpty(newText)) {
                                                        if(m_performSuggestionOnChange) {
                                                            m_searchModuleMediator.doAutocomplete(newText);
                                                        }
                                                    }
                                                    else {
                                                        m_searchModuleMediator.hideResults(selfAsScreenController);
                                                    }
                                                    return false;
                                                }
                                            }
        );

        m_searchView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                }
            }
        });

        final View showMenuButton = searchBoxRootContainer.findViewById(R.id.searchbox_search_menu);
        showMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_searchModuleMediator.showMenu(selfAsScreenController);
            }
        });

        m_showAnim = new Animation(){
            @Override
            public void start() {
                super.start();
                m_rootView.setVisibility(View.VISIBLE);
                m_screenState = ScreenState.VISIBLE;
            }
        };
        m_hideAnim = new Animation(){
            @Override
            public void start() {
                super.start();
                m_rootView.setVisibility(View.GONE);
                m_screenState = ScreenState.GONE;
            }
        };

        m_screenState = ScreenState.VISIBLE;
    }

    private void hideKeyboard() {
        InputMethodManager imm =  (InputMethodManager) m_searchView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(m_searchView.getWindowToken(), 0);
    }

    private void performSearch(){
        m_searchView.clearFocus();
        if(!TextUtils.isEmpty(m_searchView.getQuery()))
        {
            m_searchModuleMediator.doSearch(this, m_searchView.getQuery().toString());
        }
    }

    public void setQueryDisplayString(CharSequence query){
        m_performSuggestionOnChange = false;
        m_searchView.setQuery(query, false);
        m_performSuggestionOnChange = true;
    }

    public void clear(){
        m_searchView.setQuery("", false);
        m_searchView.clearFocus();
    }

    @Override
    public Animation transitionToVisible() {
        m_showAnim.reset();
        return m_showAnim;
    }

    @Override
    public Animation transitionToGone() {
        m_hideAnim.reset();
        return m_hideAnim;
    }

    @Override
    public UiScreenMemento<UiScreenVisibilityState> generateMemento() {
        return new UiScreenVisibilityState(this);
    }

    @Override
    public void resetTo(UiScreenMemento<UiScreenVisibilityState> memento) {
        memento.getState().apply();
    }
}
