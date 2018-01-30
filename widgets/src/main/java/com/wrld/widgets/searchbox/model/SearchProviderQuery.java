package com.wrld.widgets.searchbox.model;

/**
 * A model of an individual call to an ISearchProvider for a given SearchQuery
 */
class SearchProviderQuery extends SearchProviderQueryBase  {

    private MappedSearchProvider m_provider;

    SearchProviderQuery(MappedSearchProvider provider)
    {
        super(provider.getId());
        m_provider = provider;

        // How to remove callback?
        m_provider.getSearchProvider().addSearchCompletedCallback(this);
    }

    @Override
    protected void doSearch(String queryText, Object queryContext)
    {
        m_provider.getSearchProvider().getSearchResults(queryText, queryContext);
    }

    @Override
    public void cancel() {
        if (m_state == SearchProviderQueryState.InProgress)
        {
            m_state = SearchProviderQueryState.Cancelled;
            m_provider.getSearchProvider().cancelSearch();
        }
    }
}
