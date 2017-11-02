// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import com.wrld.widgets.R;
import java.util.ArrayList;

public class SearchModule implements SearchModuleFacade {

    private boolean m_isExpanded = true;
    private Button m_button;

    private DefaultSearchResultSetsContainer m_searchResultsContainer;
    private ViewGroup m_searchboxRootContainer;
    private LayoutInflater m_inflater;

    private SearchBoxController m_searchboxController;

    private ArrayList<ProviderResultSetPair> m_searchProviders;
    private ArrayList<AutoCompleteProviderResultSetPair> m_autocompleteProviders;

    private SearchResultViewFactory m_defaultFactory;

    private class ProviderResultSetPair {
        private SearchProvider m_searchProvider;
        private DefaultSearchResultSet m_resultSet;

        public SearchProvider getSearchProvider() {return m_searchProvider;}
        public DefaultSearchResultSet getResultSet() {return m_resultSet;}

        public ProviderResultSetPair(SearchProvider provider, DefaultSearchResultSet results){
            m_searchProvider = provider;
            m_resultSet = results;
        }
    }

    private class AutoCompleteProviderResultSetPair {
        private AutocompleteProvider m_autocompleteProvider;
        private DefaultSearchResultSet m_resultSet;

        public AutocompleteProvider getautocompleteProvider() {return m_autocompleteProvider;}
        public DefaultSearchResultSet getResultSet() {return m_resultSet;}

        public AutoCompleteProviderResultSetPair(AutocompleteProvider provider, DefaultSearchResultSet results){
            m_autocompleteProvider = provider;
            m_resultSet = results;
        }
    }

    public SearchModule(ViewGroup appSearchAreaView) {
        m_inflater = LayoutInflater.from(appSearchAreaView.getContext());
        m_searchboxRootContainer = (ViewGroup) m_inflater.inflate(R.layout.search_layout_test, appSearchAreaView, true);



//        (ListView)m_searchboxRootContainer.findViewById(R.id.search_result_sets_container)
        m_searchResultsContainer = new DefaultSearchResultSetsContainer((ViewGroup) m_searchboxRootContainer.findViewById(R.id.search_result_sets_container));

        m_searchboxController = new SearchBoxController(m_searchboxRootContainer);

        m_searchboxController.addQuerySubmittedCallback(new SearchBoxController.OnSearchQueryUpdatedCallback() {
            @Override
            public void performQuery(String query) {
                doSearch(query);
            }
        });

        m_searchboxController.addQueryUpdatedCallback(new SearchBoxController.OnSearchQueryUpdatedCallback() {
            @Override
            public void performQuery(String query) {
                doAutoCompleteQuery(query);
            }
        });

        m_defaultFactory = new DefaultSearchResultViewFactory(R.layout.search_result);

        m_searchProviders = new ArrayList<ProviderResultSetPair>();
        m_autocompleteProviders = new ArrayList<AutoCompleteProviderResultSetPair>();

        configureTags(R.id.search_tags);
    }

    private void configureTags(int tagContainerId) {
        ViewGroup tagContainer = (ViewGroup) m_searchboxRootContainer.findViewById(tagContainerId);

        for(int index=0; index<tagContainer.getChildCount(); ++index) {
            final Button button = (Button)tagContainer.getChildAt(index);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    doSearch(button.getText().toString());
                }
            });
        }
    }

    @Override
    public void setDefaultSearchResultViewFactory(SearchResultViewFactory factory) {
        m_defaultFactory = factory;
    }

    @Override
    public void addSearchProvider(SearchProvider provider) {
        addSearchProvider(provider, m_defaultFactory);
    }

    private void doSearch(String query) {
        for(ProviderResultSetPair providerResultsPair : m_searchProviders){
            providerResultsPair.getSearchProvider().getSearchResults(query, providerResultsPair.getResultSet().updateResultsViewCallback());
        }
    }

    private void doAutoCompleteQuery(String query) {
        for(AutoCompleteProviderResultSetPair providerResultsPair : m_autocompleteProviders){
            providerResultsPair.getautocompleteProvider().getSuggestions(query, providerResultsPair.getResultSet().updateResultsViewCallback());
        }
    }

    @Override
    public void addSearchProvider(SearchProvider provider, SearchResultViewFactory factory) {
        DefaultSearchResultSet resultSet = new DefaultSearchResultSet();
        m_searchResultsContainer.addSearchResultSet(resultSet, factory);
        m_searchProviders.add(new ProviderResultSetPair(provider, resultSet));
    }

    @Override
    public void addAutoCompleteProvider(AutocompleteProvider provider, SearchResultViewFactory factory) {
        DefaultSearchResultSet resultSet = new DefaultSearchResultSet();
        m_searchResultsContainer.addSearchResultSet(resultSet, factory);
        m_autocompleteProviders.add(new AutoCompleteProviderResultSetPair(provider, resultSet));
    }

    @Override
    public void addAutoCompleteProvider(AutocompleteProvider provider) {
        addAutoCompleteProvider(provider, m_defaultFactory);
    }

    @Override
    public void addConsumer(SearchResultConsumer consumer) {

    }

    public void setButton(Button button){


        m_button = button;

        m_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                setIsExpanded(!m_isExpanded);
            }
        });
    }

    public void setIsExpanded(boolean expand){
        m_isExpanded = expand;

        TranslateAnimation animation;

        if(expand){
            animation = buildTranslationXAnimation(-m_searchboxRootContainer.getWidth(), 0);
        }
        else {
            animation = buildTranslationXAnimation(0, -m_searchboxRootContainer.getWidth());
        }
        animation.setDuration(1000);
        animation.setFillAfter(true);

        m_searchboxRootContainer.startAnimation(animation);
    }

    private TranslateAnimation buildTranslationXAnimation(float startX,float endX){
        return new TranslateAnimation(startX, endX, 0, 0);
    }
}
