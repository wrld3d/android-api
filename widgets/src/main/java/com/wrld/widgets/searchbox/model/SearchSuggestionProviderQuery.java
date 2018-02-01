package com.wrld.widgets.searchbox.model;

class SearchSuggestionProviderQuery extends SearchProviderQueryBase {

    private MappedSuggestionProvider m_provider;

    public SearchSuggestionProviderQuery(MappedSuggestionProvider provider) {
        super(provider.getId());
        m_provider = provider;

        // TODO: Does this need removing?
        m_provider.getSuggestionProvider().addSuggestionsReceivedCallback(this);
    }

    @Override
    public void doSearch(String queryText, Object queryContext) {
        m_provider.getSuggestionProvider().getSuggestions(queryText,queryContext);
    }

    @Override
    public void cancel() {
        if (m_state == SearchProviderQueryState.InProgress) {
            m_provider.getSuggestionProvider().cancelSuggestions();
        }
    }

}