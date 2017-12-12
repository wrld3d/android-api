package com.wrld.widgets.searchbox;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.SearchView;
import com.wrld.widgets.R;
import com.wrld.widgets.ui.UiScreenController;

class SearchController implements UiScreenController {

    private View m_rootView;
    private SearchView m_searchView;

    private Animation m_showAnim;
    private Animation m_hideAnim;

    private ScreenState m_screenState;

    public ScreenState getScreenState() { return m_screenState; }

    private SearchModuleController m_searchModuleMediator;

    SearchController(ViewGroup searchBoxRootContainer, SearchModuleController searchModuleMediator) {

        m_searchModuleMediator = searchModuleMediator;

        m_rootView = searchBoxRootContainer;
        m_searchView = (SearchView) searchBoxRootContainer.findViewById(R.id.searchbox_search_querybox);

        final UiScreenController selfAsMediatorElement = this;

        m_searchView.setOnQueryTextListener(
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText != null && !TextUtils.isEmpty(newText.trim())) {
                        m_searchModuleMediator.doAutocomplete(newText);
                    }
                    else {
                        m_searchModuleMediator.hideResults(selfAsMediatorElement);
                    }

                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    m_searchModuleMediator.doSearch(selfAsMediatorElement, query);
                    return true;
                }
            }
        );

        final View performSearchButton = searchBoxRootContainer.findViewById(R.id.searchbox_search_perform);
        performSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                performQuery(m_searchView.getQuery());
            }
        });

        /*final View showMenuButton = searchBoxRootContainer.findViewById(R.id.searchbox_search_menu);
        showMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_searchModuleMediator.showMenu(selfAsMediatorElement);
            }
        });*/

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

    public void performQuery(CharSequence query){
        m_searchView.setQuery(query, true);
    }
    public void clear(){
        m_searchView.setQuery("", false);
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
}
