package com.wrld.widgets.searchbox;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;

import com.wrld.widgets.R;
import com.wrld.widgets.ui.Accordion;
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

    private Accordion m_accordion;

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
                m_accordion.expandGroup(0);
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

        ViewGroup accordionSpace = (ViewGroup)m_rootContainer.findViewById(R.id.searchbox_menu_groups_space);
        m_accordion = new Accordion(accordionSpace, m_contentProvider, R.layout.searchbox_menu_group);
        m_accordion.addGroups(m_contentProvider.getGroupCount());
     }

    public void setDefaultMenuContent(Context context){
        SearchBoxMenuGroup find = m_contentProvider.addGroup("Find:");

        find.add( new SearchBoxMenuChild[]{
               new SearchBoxMenuChild(context.getString(R.string.searchbox_menu_category_around_me)),
               new SearchBoxMenuChild(context.getString(R.string.searchbox_menu_category_accomodation)),
               new SearchBoxMenuChild(context.getString(R.string.searchbox_menu_category_art_and_museums)),
               new SearchBoxMenuChild(context.getString(R.string.searchbox_menu_category_business)),
               new SearchBoxMenuChild(context.getString(R.string.searchbox_menu_category_entertainment)),
               new SearchBoxMenuChild(context.getString(R.string.searchbox_menu_category_food_and_drink)),
               new SearchBoxMenuChild(context.getString(R.string.searchbox_menu_category_amenities)),
               new SearchBoxMenuChild(context.getString(R.string.searchbox_menu_category_health)),
               new SearchBoxMenuChild(context.getString(R.string.searchbox_menu_category_shopping)),
               new SearchBoxMenuChild(context.getString(R.string.searchbox_menu_category_sport_and_leisure)),
               new SearchBoxMenuChild(context.getString(R.string.searchbox_menu_category_tourism)),
               new SearchBoxMenuChild(context.getString(R.string.searchbox_menu_category_transport))});
        find.addOnClickListenerToAllChildren(onFindClickListener());
    }

    private SearchBoxMenuItem.OnClickListener onFindClickListener(){
        final UiScreenController self = this;
        return new SearchBoxMenuItem.OnClickListener(){
            @Override
            public void onClick(SearchBoxMenuItem clickedItem) {
                m_searchModuleController.doSearch(self, clickedItem.getTitle());
            }
        };
    }
}

