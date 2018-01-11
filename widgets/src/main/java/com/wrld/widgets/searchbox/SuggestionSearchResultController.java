package com.wrld.widgets.searchbox;

import android.animation.Animator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.api.SearchResult;
import com.wrld.widgets.searchbox.api.SearchResultViewFactory;
import com.wrld.widgets.searchbox.api.SearchResultViewHolder;

import java.util.ArrayList;

class SuggestionSearchResultController extends BaseAdapter implements SearchResultsController {

    private LayoutInflater m_inflater;

    private ListView m_container;

    private SetCollection m_sets;
    private ArrayList<SearchResultViewFactory> m_viewFactories;

    private boolean m_canBeShown;
    private boolean m_resultsOnScreen;

    private int m_animateInDurationMs;
    private int m_animateOutDurationMs;

    public SuggestionSearchResultController( ListView container, SetCollection resultSet) {
        m_container = container;
        m_inflater = LayoutInflater.from(container.getContext());
        m_sets = resultSet;
        m_canBeShown = false;
        m_resultsOnScreen = false;
        m_container.setAlpha(0);

        m_animateInDurationMs = m_container.getContext().getResources().getInteger(R.integer.suggestion_animate_in_duration_in_ms);
        m_animateOutDurationMs = m_container.getContext().getResources().getInteger(R.integer.suggestion_animate_out_duration_in_ms);
    }

    @Override
    public SearchResultSet.OnResultChanged getUpdateCallback(){
        return new SearchResultSet.OnResultChanged() {
            @Override
            public void invoke() {
                refreshContent();
            }
        };
    }

    public void setViewFactories(ArrayList<SearchResultViewFactory> factories){
        m_viewFactories = factories;
        m_container.setAdapter(this);
    }

    @Override
    public int getCount() {
        return m_sets.getAllResultsCount();
    }

    @Override
    public Object getItem(int position) {
        return m_sets.getResultAtIndex(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount(){
        return m_viewFactories.size();
    }

    @Override
    public int getItemViewType(int position){
        return m_sets.getSetForAbsolutePosition(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            SearchResultViewFactory viewFactory = m_viewFactories.get(getItemViewType(position));
            convertView = viewFactory.makeSearchResultView(m_inflater, parent);
            SearchResultViewHolder viewHolder = viewFactory.makeSearchResultViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);
        }

        SearchResult result = m_sets.getResultAtIndex(position);
        ((SearchResultViewHolder)convertView.getTag()).populate(result);

        return convertView;
    }

    @Override
    public void refreshContent() {
        if(m_canBeShown && !m_resultsOnScreen && getCount() > 0){
            animateIn();
        }
        else if(getCount() == 0){
            animateOut();
        }

        if(m_canBeShown){
            notifyDataSetChanged();
        }
    }

    public void show(){
        if(!m_canBeShown) {
            m_container.setVisibility(View.VISIBLE);
            m_canBeShown = true;
        }
    }

    public void hide() {
        if(m_canBeShown){
            m_canBeShown = false;
            if(m_resultsOnScreen) {
                animateOut();
            }
        }
    }

    private void animateIn(){
        if(m_canBeShown) {
            m_container.animate().alpha(1.0f).setDuration(m_animateInDurationMs);
            m_resultsOnScreen = true;
        }
    }

    private void animateOut(){
        ViewPropertyAnimator out = m_container.animate().alpha(0.0f).setDuration(m_animateOutDurationMs);
        out.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!m_canBeShown){
                    m_container.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        m_resultsOnScreen = false;
    }
}
