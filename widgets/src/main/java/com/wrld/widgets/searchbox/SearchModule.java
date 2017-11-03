// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.widgets.searchbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import com.wrld.widgets.R;
import java.util.ArrayList;

public class SearchModule implements SearchModuleFacade {

    private boolean m_isExpanded = true;
    private Button m_button;

    private DefaultSearchResultSetsContainer m_searchResultsContainer;
    private ViewGroup m_searchboxRootContainer;
    private LayoutInflater m_inflater;

    private SearchBoxController m_searchboxController;

    private ArrayList<SearchProvider> m_searchProviders;
    private ArrayList<SuggestionProvider> m_suggestionProviders;

    private SearchResultViewFactory m_defualtViewFactory;

    public SearchModule(ViewGroup appSearchAreaView) {
        m_inflater = LayoutInflater.from(appSearchAreaView.getContext());
        m_searchboxRootContainer = (ViewGroup) m_inflater.inflate(R.layout.search_layout_test, appSearchAreaView, true);

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

        m_searchProviders = new ArrayList<SearchProvider>();
        m_suggestionProviders = new ArrayList<SuggestionProvider>();

        setDefaultSearchResultViewFactory(new DefaultSearchResultViewFactory(R.layout.search_result));

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
        m_defualtViewFactory = factory;
    }

    @Override
    public void addSearchProvider(SearchProvider provider, boolean doSuggestions) {

        DefaultSearchResultSet setResults = new DefaultSearchResultSet();
        DefaultSearchResultSet setSuggestions = new DefaultSearchResultSet();

        SearchResultViewFactory factoryResults = provider.getResultViewFactory();
        if(factoryResults == null) {
            factoryResults = m_defualtViewFactory;
        }

        addSearchProvider(provider,setResults);

        SearchResultViewFactory factorySuggestions = m_defualtViewFactory;
        if(doSuggestions) {

            if (provider instanceof SuggestionProvider) {
                SuggestionProvider providerSuggestion = (SuggestionProvider)provider;
                factorySuggestions = providerSuggestion.getSuggestionViewFactory();
                if(factorySuggestions == null) {
                    factorySuggestions = m_defualtViewFactory;
                }

                addSuggestionProvider(providerSuggestion, setSuggestions);
            }
            else
            {
                android.util.Log.w("Search Widget", "Tried to add a Suggestion Provider that doesn't implement the SuggestionProvider Interface");
            }
        }

        m_searchResultsContainer.addSearchResultSet(setResults, setSuggestions, factoryResults ,factorySuggestions );

    }

    private void addSearchProvider(SearchProvider provider, final DefaultSearchResultSet resultSet){
        provider.addOnResultsRecievedCallback(new OnResultsReceivedCallback() {
            @Override
            public void onResultsReceived(SearchResult[] results) {
                resultSet.updateSetResults(results);
            }
        });

        m_searchProviders.add(provider);
    }

    private void addSuggestionProvider(SuggestionProvider provider, final DefaultSearchResultSet suggestionSet){

        provider.addOnSuggestionsRecievedCallback(new OnResultsReceivedCallback() {
            @Override
            public void onResultsReceived(SearchResult[] results) {
                suggestionSet.updateSetResults(results);
            }
        });

        m_suggestionProviders.add(provider);
    }

    private void doSearch(String query) {
        m_searchResultsContainer.showSuggestions(false);

        for(SearchProvider provider:m_searchProviders){
            provider.getSearchResults(query);
        }
    }

    private void doAutoCompleteQuery(String query) {
        m_searchResultsContainer.showSuggestions(true);

        for(SuggestionProvider provider:m_suggestionProviders){
            provider.getSuggestions(query);
        }
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
