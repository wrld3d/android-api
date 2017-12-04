// Copyright Wrld3d Ltd (2012-2017), All Rights Reserved

package com.wrld.searchproviders;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wrld.widgets.searchbox.SearchResult;
import com.wrld.widgets.searchbox.SearchResultViewFactory;
import com.wrld.widgets.searchbox.SearchResultViewHolder;

import java.util.HashMap;

public class YelpSearchResultViewFactory implements SearchResultViewFactory {

    private int m_layoutId;
    private String m_reviewText;
    private Context m_context;

    private HashMap<Double, Integer> m_starCache;

    public YelpSearchResultViewFactory(Context context){
        m_context = context;
        m_layoutId = R.layout.yelp_search_result;
        m_reviewText = context.getString(R.string.yelp_review_text);

        m_starCache = new HashMap<Double, Integer>();
    }

    public void cacheRatingStarImages(){
        m_starCache.put(0.0, R.drawable.stars_small_0);
        m_starCache.put(1.0, R.drawable.stars_small_1);
        m_starCache.put(1.5, R.drawable.stars_small_1_half);
        m_starCache.put(2.0, R.drawable.stars_small_2);
        m_starCache.put(2.5, R.drawable.stars_small_2_half);
        m_starCache.put(3.0, R.drawable.stars_small_3);
        m_starCache.put(3.5, R.drawable.stars_small_3_half);
        m_starCache.put(4.0, R.drawable.stars_small_4);
        m_starCache.put(4.5, R.drawable.stars_small_4_half);
        m_starCache.put(5.0, R.drawable.stars_small_5);
    }

    @Override
    public View makeSearchResultView(LayoutInflater inflater, SearchResult model, ViewGroup parent) {
        return inflater.inflate(m_layoutId, parent, false);
    }

    private class SearchResultViewHolderImpl implements SearchResultViewHolder {
        private TextView m_title;
        private ImageButton m_rating;
        private ImageButton m_yelpLogo;
        private TextView m_reviewCount;
        private String m_url;

        public SearchResultViewHolderImpl(){}

        public void initialise(View view) {
            m_title = (TextView) view.findViewById(R.id.search_result_title);
            m_rating = (ImageButton) view.findViewById(R.id.yelp_rating);
            m_yelpLogo = (ImageButton) view.findViewById(R.id.yelp_logo);
            m_reviewCount = (TextView) view.findViewById(R.id.yelp_reviews);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLink();
                }
            };

            m_yelpLogo.setOnClickListener(onClickListener);
            m_rating.setOnClickListener(onClickListener);
        }

        public void populate(SearchResult result) {
            m_title.setText(result.getTitle());
            m_rating.setImageResource(m_starCache.get(result.getProperty(YelpSearchResult.RatingKey).getValue()));
            m_url = (String) result.getProperty(YelpSearchResult.BusinessLink).getValue();

            String reviewCountText = String.format(m_reviewText, result.getProperty(YelpSearchResult.ReviewCountKey).getValue());
            m_reviewCount.setText(reviewCountText);
        }

        private void openLink(){
            Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( m_url ) );
            m_context.startActivity( browse );
        }
    }

    @Override
    public SearchResultViewHolder makeSearchResultViewHolder() {
        return new YelpSearchResultViewFactory.SearchResultViewHolderImpl();
    }
}
