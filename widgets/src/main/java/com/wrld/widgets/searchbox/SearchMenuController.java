package com.wrld.widgets.searchbox;

import android.content.Context;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.widget.ExpandableListView;

import com.wrld.widgets.R;
import com.wrld.widgets.ui.UiScreenController;

public class SearchMenuController implements UiScreenController {

    private Animation m_showAnim;
    private Animation m_hideAnim;

    private ScreenState m_screenState;
    private ViewStub m_stub;
    private View m_rootContainer;

    public ScreenState getScreenState() { return m_screenState; }

    private SearchModuleController m_searchModuleController;

    private boolean m_hasInflated;

    private SearchMenuContent m_contentProvider;

    private ExpandableListView m_expandableListView;

    public SearchMenuController(ViewStub stub, SearchModuleController searchModuleController, SearchMenuContent contentProvider){

        m_stub = stub;
        m_searchModuleController = searchModuleController;
        m_contentProvider = contentProvider;

        m_hasInflated= false;
        m_screenState = ScreenState.GONE;

        m_showAnim = new Animation(){
            @Override
            public void start() {
                super.start();
                if(!m_hasInflated){
                    inflate();
                }
                m_expandableListView.expandGroup(0);
                m_rootContainer.setVisibility(View.VISIBLE);
                m_screenState = ScreenState.VISIBLE;
            }
        };
        m_hideAnim = new Animation(){
            @Override
            public void start() {
                super.start();
                m_rootContainer.setVisibility(View.GONE);
                m_screenState = ScreenState.GONE;
            }
        };
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

    private void inflate(){
        m_rootContainer = m_stub.inflate();

        View backButton = m_rootContainer.findViewById(R.id.searchbox_menu_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_searchModuleController.back();
            }
        });
        m_hasInflated = true;

        m_expandableListView = (ExpandableListView) m_rootContainer.findViewById(R.id.searchbox_menu_contents);
        m_expandableListView.setAdapter(m_contentProvider);
        m_expandableListView.setOnGroupClickListener(onGroupClicked());
        m_expandableListView.setOnChildClickListener(onChildClicked());
     }

     private ExpandableListView.OnChildClickListener onChildClicked(){
         return new ExpandableListView.OnChildClickListener() {
             @Override
             public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                 m_contentProvider.getSearchBoxMenuGroup(groupPosition).get(childPosition).clicked();
                 return true;
             }
         };
     }

    private ExpandableListView.OnGroupClickListener onGroupClicked(){
        return new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(parent.isGroupExpanded(groupPosition)){
                    parent.collapseGroup(groupPosition);
                }
                else{
                    parent.expandGroup(groupPosition);
                }
                m_contentProvider.getSearchBoxMenuGroup(groupPosition).clicked();
                return true;
            }
        };
    }

    public void setDefaultMenuContent(Context context){
        SearchBoxMenuGroup find = m_contentProvider.addGroup("Find:");

        find.add( new String[]{
                context.getString(R.string.searchbox_menu_category_around_me),
                context.getString(R.string.searchbox_menu_category_accomodation),
                context.getString(R.string.searchbox_menu_category_art_and_museums),
                context.getString(R.string.searchbox_menu_category_business),
                context.getString(R.string.searchbox_menu_category_entertainment),
                context.getString(R.string.searchbox_menu_category_food_and_drink),
                context.getString(R.string.searchbox_menu_category_amenities),
                context.getString(R.string.searchbox_menu_category_health),
                context.getString(R.string.searchbox_menu_category_shopping),
                context.getString(R.string.searchbox_menu_category_sport_and_leisure),
                context.getString(R.string.searchbox_menu_category_tourism),
                context.getString(R.string.searchbox_menu_category_transport)});
        find.addOnClickListenerToAllChildren(onFindClickListener());
    }

    private SearchBoxMenuItem.OnClickListener onFindClickListener(){
        final UiScreenController self = this;
        return new SearchBoxMenuItem.OnClickListener(){
            @Override
            public void onClick(SearchBoxMenuItem clicked) {
                m_searchModuleController.doSearch(self, clicked.getTitle());
            }
        };
    }
}

