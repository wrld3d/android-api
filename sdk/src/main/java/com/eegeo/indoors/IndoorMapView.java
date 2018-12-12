package com.eegeo.indoors;

import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eegeo.mapapi.EegeoMap;
import com.eegeo.mapapi.MapView;
import com.eegeo.mapapi.R;
import com.eegeo.mapapi.indoors.IndoorMap;
import com.eegeo.mapapi.indoors.OnFloorChangedListener;
import com.eegeo.mapapi.indoors.OnIndoorEnteredListener;
import com.eegeo.mapapi.indoors.OnIndoorExitedListener;

import java.util.ArrayList;
import java.util.List;

public class IndoorMapView implements OnIndoorEnteredListener, OnIndoorExitedListener, OnFloorChangedListener, View.OnClickListener, View.OnTouchListener {
    private final long m_stateChangeAnimationTimeMilliseconds = 200;
    private final long m_stateChangeAnimationDelayMilliseconds = m_stateChangeAnimationTimeMilliseconds * 5;
    private final long m_initialJumpThresholdPx = 5;
    private final float ListItemHeight;
    private EegeoMap m_eegeoMap = null;
    private RelativeLayout m_uiContainer = null;
    private View m_uiRootView = null;
    private View m_topPanel = null;
    private View m_rightPanel = null;
    private ImageView m_backButton = null;
    private TextView m_floorNameView = null;
    private RelativeLayout m_floorButton = null;
    private TextView m_floorButtonText = null;
    private Boolean m_draggingFloorButton;
    private float m_topYPosActive;
    private float m_topYPosInactive;
    private RelativeLayout m_floorListContainer;
    private BackwardsCompatibleListView m_floorList;
    private int m_maxFloorsViewableCount = 5;
    private RelativeLayout m_floorLayout;
    private ImageView m_floorUpArrow;
    private ImageView m_floorDownArrow;
    private boolean m_isScrolling = false;
    private float m_scrollYCoordinate;
    private Handler m_scrollHandler;
    private Runnable m_scrollingRunnable;
    private float m_leftXPosActiveBackButton;
    private float m_leftXPosActiveFloorListContainer;
    private float m_leftXPosInactive;
    private IndoorFloorListAdapter m_floorListAdapter = null;
    private float m_previousYCoordinate;
    private boolean m_isButtonInitialJumpRemoved = false;
    private boolean m_isOnScreen = false;
    private IndoorMap m_indoorMap = null;
    private int m_currentFloorIndex = -1;

    private final int TextColorNormal = Color.parseColor("#1256BE");
    private final int TextColorDown = Color.parseColor("#CDFC0D");

    public IndoorMapView(MapView mapView, final RelativeLayout uiContainer, EegeoMap eegeoMap) {
        m_eegeoMap = eegeoMap;
        m_uiContainer = uiContainer;
        LayoutInflater inflater = LayoutInflater.from(m_uiContainer.getContext());
        m_uiRootView = inflater.inflate(R.layout.interiors_explorer_layout, m_uiContainer, false);
        ListItemHeight = m_uiRootView.getContext().getResources().getDimension(R.dimen.elevator_list_item_height);

        m_topPanel = m_uiRootView.findViewById(R.id.top_panel);
        m_rightPanel = m_uiRootView.findViewById(R.id.right_panel);

        m_backButton = (ImageView) m_uiRootView.findViewById(R.id.back_button);
        m_backButton.setOnClickListener(this);

        m_floorNameView = (TextView) m_uiRootView.findViewById(R.id.floor_name);

        m_floorListContainer = (RelativeLayout) m_uiRootView.findViewById(R.id.interiors_floor_list_container);
        m_floorList = (BackwardsCompatibleListView) m_uiRootView.findViewById(R.id.interiors_floor_item_list);
        m_floorList.setOnTouchListener(new PropagateToViewTouchListener(mapView));
        m_floorList.setItemHeight(ListItemHeight);

        m_floorListAdapter = new IndoorFloorListAdapter(R.layout.interiors_floor_list_item);
        m_floorList.setAdapter(m_floorListAdapter);
        m_floorLayout = (RelativeLayout) m_uiRootView.findViewById(R.id.interiors_floor_layout);
        m_floorUpArrow = (ImageView) m_uiRootView.findViewById(R.id.interiors_elevator_up_arrow);
        m_floorDownArrow = (ImageView) m_uiRootView.findViewById(R.id.interiors_elevator_down_arrow);

        m_floorButton = (RelativeLayout) m_uiRootView.findViewById(R.id.interiors_floor_list_button);
        m_floorButtonText = (TextView) m_uiRootView.findViewById(R.id.interiors_floor_list_button_text);
        m_floorButtonText.setTextColor(TextColorNormal);
        m_draggingFloorButton = false;

        m_floorButton.setOnTouchListener(this);

        m_uiRootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight,
                                       int oldBottom) {
                final float screenWidth = m_uiContainer.getWidth();

                float controlWidth = m_topPanel.getWidth();
                float controlHeight = m_topPanel.getHeight();

                m_topYPosActive = m_uiRootView.getContext().getResources().getDimension(R.dimen.elevator_top_margin);
                m_topYPosInactive = -controlHeight;

                m_topPanel.setX((screenWidth * 0.5f) - (controlWidth * 0.5f));
                m_topPanel.setY(m_topYPosInactive);

                controlWidth = m_floorListContainer.getWidth();
                controlHeight = m_floorListContainer.getHeight();
                int menuButtonMarginPx = (int) m_uiRootView.getContext().getResources().getDimension(R.dimen.elevator_side_margin);

                m_leftXPosActiveBackButton = screenWidth - (menuButtonMarginPx + m_backButton.getWidth());
                m_leftXPosActiveFloorListContainer = screenWidth - (menuButtonMarginPx + controlWidth - (controlWidth - m_backButton.getWidth()) / 2.0f);
                m_leftXPosInactive = screenWidth;

                m_floorListContainer.setX(m_leftXPosInactive);
                m_backButton.setX(m_leftXPosInactive);

                int screenHeight = m_uiContainer.getHeight();

                RelativeLayout.LayoutParams rightPanelLayoutParams = (RelativeLayout.LayoutParams) m_rightPanel.getLayoutParams();
                int rightPanelMarginTop = rightPanelLayoutParams.topMargin;
                int rightPanelMarginBottom = rightPanelLayoutParams.bottomMargin;

                RelativeLayout.LayoutParams floorListContainerLayoutParams = (RelativeLayout.LayoutParams) m_floorListContainer.getLayoutParams();
                int floorListMarginTop = floorListContainerLayoutParams.topMargin;

                int maxFloorContainerHeight = screenHeight - rightPanelMarginTop - m_backButton.getHeight() - floorListMarginTop - rightPanelMarginBottom;

                m_maxFloorsViewableCount = (int) Math.floor(maxFloorContainerHeight / ListItemHeight);

                m_uiRootView.removeOnLayoutChangeListener(this);
            
            }
        });
        m_uiContainer.addView(m_uiRootView);
        m_uiRootView.setVisibility(View.VISIBLE);
        hideFloorLabels();

        m_eegeoMap.addOnIndoorEnteredListener(this);
        m_eegeoMap.addOnIndoorExitedListener(this);
        m_eegeoMap.addOnFloorChangedListener(this);
        m_scrollHandler = new Handler();
        m_scrollingRunnable = new Runnable() {
            @Override
            public void run() {
                scrollingUpdate();
                m_scrollHandler.postDelayed(m_scrollingRunnable, 1);
            }
        };

        forceViewRelayout(mapView);
    }

    @UiThread
    public void onIndoorEntered() {
        m_indoorMap = m_eegeoMap.getActiveIndoorMap();
        m_currentFloorIndex = m_eegeoMap.getCurrentFloorIndex();
        updateFloors(m_indoorMap.floorIds, m_currentFloorIndex);
        setFloorName(m_indoorMap.floorNames[m_currentFloorIndex]);
        setSelectedFloorIndex(m_currentFloorIndex);

        m_uiRootView.setVisibility(View.VISIBLE);
        forceViewRelayout(m_uiRootView);
        animateToActive();
    }

    public void onIndoorExited() {
        m_uiRootView.setVisibility(View.INVISIBLE);
    }

    public void onFloorChanged(int selectedFloor) {
        m_currentFloorIndex = selectedFloor;
        setFloorName(m_indoorMap.floorNames[m_currentFloorIndex]);
        setSelectedFloorIndex(m_currentFloorIndex);
    }

    private int getListViewHeight(ListView list) {
        return list.getCount() * (int) ListItemHeight;
    }

    private int getListViewHeight(int listCount) {
        return listCount * (int) ListItemHeight;
    }

    public void destroy() {
        m_uiContainer.removeView(m_uiRootView);
        m_uiRootView = null;

        m_scrollHandler.removeCallbacks(m_scrollingRunnable);
    }

    public void updateFloors(String[] floorShortNames, int currentlySelectedFloorIndex) {
        List<String> temp = new ArrayList<String>();
        for (int i = floorShortNames.length - 1; i >= 0; --i) {
            temp.add(floorShortNames[i]);
        }

        m_floorListAdapter.setData(temp);
        float controlHeight = getListViewHeight(Math.min(m_floorList.getCount(), m_maxFloorsViewableCount));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) m_floorListContainer.getLayoutParams();
        layoutParams.height = (int) controlHeight;
        m_floorListContainer.setLayoutParams(layoutParams);

        refreshFloorIndicator(currentlySelectedFloorIndex);
        moveButtonToFloorIndex(currentlySelectedFloorIndex, false);

        boolean floorSelectionEnabled = floorShortNames.length > 1;
        m_floorListContainer.setVisibility(floorSelectionEnabled ? View.VISIBLE : View.GONE);

        m_floorUpArrow.setY(m_floorList.getY());
        m_floorDownArrow.setY(m_floorList.getY() + controlHeight - m_floorDownArrow.getHeight());
    }

    private void setArrowState(boolean showUp, boolean showDown) {
        m_floorUpArrow.setVisibility(showUp ? View.VISIBLE : View.INVISIBLE);
        m_floorDownArrow.setVisibility(showDown ? View.VISIBLE : View.INVISIBLE);
    }

    private void moveButtonToFloorIndex(int floorIndex, Boolean shouldAnimate) {
        int floorCount = m_floorListAdapter.getCount();
        int halfMaxFloorsViewableCount = (int) Math.ceil(m_maxFloorsViewableCount / 2.0f);

        int startFloorIndex = floorCount - floorIndex;

        if (floorCount - startFloorIndex >= halfMaxFloorsViewableCount) {
            startFloorIndex = Math.max(startFloorIndex - halfMaxFloorsViewableCount, 0);
        }

        int screenFloorIndex = floorCount - 1 - floorIndex;

        if (floorCount > m_maxFloorsViewableCount) {
            startFloorIndex = Math.min(startFloorIndex, floorCount - m_maxFloorsViewableCount);

            screenFloorIndex = floorCount - 1 - floorIndex - startFloorIndex;
        }

        float topY = (m_floorList.getY()) + (ListItemHeight * 0.5f) - m_floorButton.getWidth() * 0.5f;
        float newY = topY + screenFloorIndex * ListItemHeight;
        newY = Math.max(0.0f, Math.min(getListViewHeight(m_floorList), newY));

        if (shouldAnimate) {
            m_floorButton.animate()
                    .y(newY)
                    .setDuration(m_stateChangeAnimationTimeMilliseconds)
                    .start();

            m_floorList.smoothScrollToPositionFromTop(startFloorIndex, 0);
        } else {
            m_floorButton.setY(newY);

            m_floorList.setSelection(startFloorIndex);
        }

        if (floorCount > m_maxFloorsViewableCount) {
            setArrowState(floorCount - floorIndex > halfMaxFloorsViewableCount, floorCount - startFloorIndex > m_maxFloorsViewableCount);
        } else {
            setArrowState(false, false);
        }
    }

    public void setFloorName(String name) {
        m_floorNameView.setText(name);
    }

    public void setSelectedFloorIndex(int index) {
        refreshFloorIndicator(index);

        if (!m_draggingFloorButton) {
            moveButtonToFloorIndex(index, true);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (view == m_floorButton) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startDraggingButton(event.getRawY());
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                updateDraggingButton(event.getRawY());
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                endDraggingButton();
            }
            return true;
        }
        return false;
    }

    private void startDraggingButton(float initialYCoordinate) {
        showFloorLabels();
        m_floorButton.getBackground().setState(new int[]{android.R.attr.state_pressed});
        m_floorButtonText.setTextColor(TextColorDown);
        m_previousYCoordinate = initialYCoordinate;
        m_draggingFloorButton = true;
        m_isButtonInitialJumpRemoved = false;
        m_eegeoMap.expandIndoor();
        startScrollingUpdate();
    }

    private void updateDraggingButton(float yCoordinate) {
        m_scrollYCoordinate = yCoordinate;

        if (!m_isButtonInitialJumpRemoved) {
            detectAndRemoveInitialJump(m_scrollYCoordinate);
        }
    }

    private void endDraggingButton() {
        endScrollingUpdate();

        hideFloorLabels();
        m_draggingFloorButton = false;
        m_floorButton.getBackground().setState(new int[]{});
        m_floorButtonText.setTextColor(TextColorNormal);

        View firstVisibleChild = m_floorList.getChildAt(0);
        float topY = (m_floorList.getFirstVisiblePosition() * ListItemHeight) - firstVisibleChild.getTop();

        float dragParameter = 1.0f - ((topY + m_floorButton.getY()) / (getListViewHeight(m_floorList.getCount() - 1)));
        int floorCount = m_floorListAdapter.getCount() - 1;
        int selectedFloor = Math.round(dragParameter * floorCount);
        moveButtonToFloorIndex(selectedFloor, true);
        m_eegeoMap.collapseIndoor();
        m_eegeoMap.setIndoorFloor(selectedFloor);
    }

    private float getScrollSpeed(float t) {
        final float maxScrollSpeed = m_uiRootView.getContext().getResources().getDimension(R.dimen.elevator_max_scroll_speed);

        t = Math.max(-1, Math.min(1, t));
        return t * Math.abs(t) * maxScrollSpeed;
    }

    /**
     * This function will remove the starting jump on slider if detected
     *
     * @param yCoordinate
     */
    private void detectAndRemoveInitialJump(float yCoordinate) {
        float y = yCoordinate;
        float deltaY = y - m_previousYCoordinate;
        if (Math.abs(deltaY) > m_initialJumpThresholdPx) {
            m_previousYCoordinate += deltaY;
            m_isButtonInitialJumpRemoved = true;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == m_backButton) {
            animateToInactive();
            m_uiRootView.setVisibility(View.INVISIBLE);
            m_eegeoMap.onExitIndoorClicked();
        }
    }

    public void animateToActive() {
        m_isOnScreen = true;

        animateViewToY((int) m_topYPosActive);
        animateViewToX((int) m_leftXPosActiveBackButton, (int) m_leftXPosActiveFloorListContainer, m_isOnScreen);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] rootViewPosition = {0, 0};
                m_uiRootView.getLocationInWindow(rootViewPosition);
                int[] backButtonPosition = {0, 0};
                m_backButton.getLocationInWindow(backButtonPosition);
                int[] floorButtonPosition = {0, 0};
                m_floorButton.getLocationInWindow(floorButtonPosition);
            }
        }, m_stateChangeAnimationTimeMilliseconds + (m_isOnScreen ? m_stateChangeAnimationDelayMilliseconds : 0));
    }

    public void animateToInactive() {
        endScrollingUpdate();
        m_isOnScreen = false;

        animateViewToY((int) m_topYPosInactive);
        animateViewToX((int) m_leftXPosInactive, (int) m_leftXPosInactive, m_isOnScreen);
    }

    protected void animateViewToY(final int yAsPx) {
        m_topPanel.animate()
                .y(yAsPx)
                .setDuration(m_stateChangeAnimationTimeMilliseconds);
    }

    protected void animateViewToX(final int xAsPxBackButton, final int xAsPxFloorListContainer, final boolean addDelay) {
        long delay = addDelay ? m_stateChangeAnimationDelayMilliseconds : 0;

        m_floorListContainer.animate()
                .x(xAsPxFloorListContainer)
                .setDuration(m_stateChangeAnimationTimeMilliseconds)
                .setStartDelay(delay);

        m_backButton.animate()
                .x(xAsPxBackButton)
                .setDuration(m_stateChangeAnimationTimeMilliseconds)
                .setStartDelay(delay);
    }

    public void notifyOnPause() {
        endScrollingUpdate();
    }

    private void refreshFloorIndicator(int floorIndex) {
        int nameIndex = (m_floorListAdapter.getCount() - 1) - floorIndex;
        m_floorButtonText.setText((String) m_floorListAdapter.getItem(nameIndex));
    }

    private void hideFloorLabels() {
        m_floorLayout.animate().alpha(0.5f).setDuration(m_stateChangeAnimationTimeMilliseconds);
    }

    private void showFloorLabels() {
        m_floorLayout.animate().alpha(1.0f).setDuration(m_stateChangeAnimationTimeMilliseconds);
    }

    private void startScrollingUpdate() {
        if (!m_isScrolling) {
            m_previousYCoordinate = m_scrollYCoordinate;
            m_scrollingRunnable.run();
        }

        m_isScrolling = true;
    }

    private void scrollingUpdate() {
        if (m_isScrolling) {
            final float joystickScrollThresholdDistance = 0.25f;

            if (!m_isButtonInitialJumpRemoved) {
                detectAndRemoveInitialJump(m_scrollYCoordinate);
            }

            float newY = m_floorButton.getY() + (m_scrollYCoordinate - m_previousYCoordinate);
            newY = Math.max(0.0f, Math.min(Math.min(getListViewHeight(m_maxFloorsViewableCount - 1), getListViewHeight(m_floorList.getCount() - 1)), newY));
            m_floorButton.setY(newY);
            m_previousYCoordinate = m_scrollYCoordinate;

            float scrollDelta = 0;

            float listViewHeightOnScreen = getListViewHeight(m_maxFloorsViewableCount - 1);
            float normalisedNewY = newY / listViewHeightOnScreen;

            if (normalisedNewY <= joystickScrollThresholdDistance) {
                float localT = normalisedNewY / joystickScrollThresholdDistance;
                scrollDelta = getScrollSpeed(1 - localT);
            } else if (normalisedNewY >= 1 - joystickScrollThresholdDistance) {
                float localT = (normalisedNewY - (1 - joystickScrollThresholdDistance)) / joystickScrollThresholdDistance;
                scrollDelta = getScrollSpeed(-localT);
            }

            m_floorList.scrollListBy_compat(-Math.round(scrollDelta));

            View firstVisibleChild = m_floorList.getChildAt(0);
            float topY = (m_floorList.getFirstVisiblePosition() * ListItemHeight) - firstVisibleChild.getTop();

            float dragParameter = 1.0f - ((topY + newY) / (getListViewHeight(m_floorList.getCount() - 1)));
            float floorParam = dragParameter * (m_floorList.getCount() - 1);
            m_eegeoMap.setIndoorFloorInterpolation(floorParam);

            int nearestFloorIndex = Math.round(floorParam);
            setFloorName(m_indoorMap.floorNames[nearestFloorIndex]);
            refreshFloorIndicator(nearestFloorIndex);

            int floorCount = m_floorList.getCount();
            if (floorCount > m_maxFloorsViewableCount) {
                int firstFloorVisibleInView = m_floorList.getFirstVisiblePosition();
                boolean showUp = firstFloorVisibleInView > 0 || firstVisibleChild.getTop() < -(ListItemHeight * 0.5f);
                boolean showDown = floorCount - firstFloorVisibleInView - (firstVisibleChild.getTop() < 0 ? 1 : 0) > m_maxFloorsViewableCount;
                setArrowState(showUp, showDown);
            }
        }
    }

    private void endScrollingUpdate() {
        m_isScrolling = false;

        m_scrollHandler.removeCallbacks(m_scrollingRunnable);
    }

    private class PropagateToViewTouchListener implements View.OnTouchListener {
        private View m_target;

        PropagateToViewTouchListener(View target) {
            m_target = target;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            return m_target.onTouchEvent(event);
        }
    }

    @UiThread
    private void forceViewRelayout(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                     View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }
}
