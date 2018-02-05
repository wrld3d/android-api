package com.wrld.widgets.searchbox;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.ISearchProvider;
import com.wrld.widgets.searchbox.model.ISuggestionProvider;
import com.wrld.widgets.searchbox.model.SearchWidgetSearchModel;
import com.wrld.widgets.searchbox.view.SearchViewController;
import com.wrld.widgets.searchbox.view.SuggestionResultsController;


public class WrldSearchWidget extends Fragment {

    private SearchWidgetSearchModel m_searchModel;
    private SearchView m_searchView;
    private SearchViewController m_searchViewController;
    private SuggestionResultsController m_searchSuggestionResultsController;

    public WrldSearchWidget() {
        super();
    }

    public void addSearchProvider(ISearchProvider searchProvider)
    {
        m_searchModel.addSearchProvider(searchProvider);
    }

    public void addSuggestionProvider(ISuggestionProvider suggestionProvider)
    {
        m_searchModel.addSuggestionProvider(suggestionProvider);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_layout, container, false);
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        // Grab attributes here.

        //m_numSuggestions = a.getInteger(R.styleable.SearchModule_maxSuggestions, 3);
        //m_maxResults  = a.getInteger(R.styleable.SearchModule_maxResults, 3);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        m_searchView = (SearchView)getView().findViewById(R.id.searchbox_search_searchview);
        ListView suggestionResultsView = (ListView)getView().findViewById(R.id.searchbox_autocomplete_container);

        m_searchModel = new SearchWidgetSearchModel();
        //m_model.setListener(this);

        m_searchViewController = new SearchViewController(m_searchModel, m_searchView);
        m_searchSuggestionResultsController = new SuggestionResultsController(m_searchModel, suggestionResultsView);
    }

    public void doSearch() {

    }

    public void repeatSearch() {

    }



}
