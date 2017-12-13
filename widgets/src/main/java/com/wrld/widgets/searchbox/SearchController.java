package com.wrld.widgets.searchbox;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wrld.widgets.R;
import com.wrld.widgets.ui.UiScreenController;

class SearchController implements UiScreenController {

    private View m_rootView;
    private EditText m_searchView;
    private View m_clearText;

    private Animation m_showAnim;
    private Animation m_hideAnim;

    private ScreenState m_screenState;

    public ScreenState getScreenState() { return m_screenState; }

    private SearchModuleController m_searchModuleMediator;

    SearchController(ViewGroup searchBoxRootContainer, SearchModuleController searchModuleMediator) {

        m_searchModuleMediator = searchModuleMediator;

        m_rootView = searchBoxRootContainer;
        m_searchView = (EditText) searchBoxRootContainer.findViewById(R.id.searchbox_search_querybox);
        m_clearText = searchBoxRootContainer.findViewById(R.id.searchbox_search_clear);

        final UiScreenController selfAsScreenController = this;

        m_searchView.addTextChangedListener(
            new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!TextUtils.isEmpty(s)) {
                        m_searchModuleMediator.doAutocomplete(s.toString());
                        m_clearText.setVisibility(View.VISIBLE);
                    }
                    else {
                        m_searchModuleMediator.hideResults(selfAsScreenController);
                        m_clearText.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            }
        );

        m_clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        m_clearText.setVisibility(View.GONE);

        m_searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        final View performSearchButton = searchBoxRootContainer.findViewById(R.id.searchbox_search_perform);
        performSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                performSearch();
            }
        });

        final View showMenuButton = searchBoxRootContainer.findViewById(R.id.searchbox_search_menu);
        showMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_searchModuleMediator.showMenu(selfAsScreenController);
            }
        });

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

    private void performSearch(){
        m_searchModuleMediator.doSearch(this, m_searchView.getText().toString());
    }

    public void setQuery(CharSequence query){
        m_searchView.setText(query);
        m_searchView.setSelection(m_searchView.getText().length());
    }

    public void clear(){
        m_searchView.setText("");
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
