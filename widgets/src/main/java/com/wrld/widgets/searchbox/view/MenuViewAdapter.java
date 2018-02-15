package com.wrld.widgets.searchbox.view;


import android.database.DataSetObserver;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ExpandableListAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.wrld.widgets.R;
import com.wrld.widgets.searchbox.model.MenuChild;
import com.wrld.widgets.searchbox.model.MenuGroup;
import com.wrld.widgets.searchbox.model.MenuOption;
import com.wrld.widgets.searchbox.model.SearchWidgetMenuModel;

interface IMenuOptionViewHolder {
    void initialise(View view);
    void populate(String text);
}

class MenuOptionViewFactory {

    public MenuOptionViewFactory() {
    }

    public View makeMenuOptionView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.searchbox_menu_group_header, parent, false);
    }

    private class MenuOptionViewHolder implements IMenuOptionViewHolder {
        private TextView m_title;

        public MenuOptionViewHolder(){}

        public void initialise(View view){
            m_title = (TextView) view.findViewById(R.id.searchbox_menu_item_text);
        }

        public void populate(String text) {
            m_title.setText(text);
        }
    }

    public IMenuOptionViewHolder makeMenuOptionViewHolder() {
        return new MenuOptionViewHolder();
    }
}

interface IMenuChildViewHolder {
    void initialise(View view);
    void populate(MenuChild child);
}

class MenuChildViewFactory {

    public MenuChildViewFactory() {
    }

    public View makeMenuChildView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.searchbox_menu_group_content, parent, false);
    }

    private class MenuChildViewHolder implements IMenuChildViewHolder {
        private TextView m_title;

        public MenuChildViewHolder(){}

        public void initialise(View view){
            m_title = (TextView) view.findViewById(R.id.searchbox_menu_item_text);
        }

        public void populate(MenuChild child) {
            m_title.setText(child.getText());
        }
    }

    public IMenuChildViewHolder makeMenuChildViewHolder() {
        return new MenuChildViewHolder();
    }
}

public class MenuViewAdapter implements ExpandableListAdapter {

    private final SearchWidgetMenuModel m_model;
    private final LayoutInflater m_inflater;

    private final MenuOptionViewFactory m_menuOptionViewFactory;
    private final MenuChildViewFactory m_menuChildViewFactory;

    public MenuViewAdapter(SearchWidgetMenuModel model, LayoutInflater inflater) {
        m_model = model;
        m_inflater = inflater;
        m_menuOptionViewFactory = new MenuOptionViewFactory();
        m_menuChildViewFactory = new MenuChildViewFactory();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO
    }

    @Override
    public int getGroupCount() {
        int expandableListViewGroupCount = 0;
        for (MenuGroup group : m_model.getGroups()) {
            if (group.hasTitle()) {
                expandableListViewGroupCount++;
            }
            expandableListViewGroupCount += group.getOptions().size();
        }
        return expandableListViewGroupCount;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition < 0) { return 0; }

        int expandableListViewGroupPosition = 0;
        for (MenuGroup group : m_model.getGroups()) {
            if (group.hasTitle()) {
                if (expandableListViewGroupPosition == groupPosition) {
                    // Titles have no children and are not selectable.
                    return 0;
                }
                expandableListViewGroupPosition++;
            }

            if (groupPosition < expandableListViewGroupPosition + group.getOptions().size()) {
                return group.getOptions().get(groupPosition - expandableListViewGroupPosition).getChildren().size();
            }

            expandableListViewGroupPosition += group.getOptions().size();
        }

        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (groupPosition < 0) { return null; }

        int expandableListViewGroupPosition = 0;
        for (MenuGroup group : m_model.getGroups()) {
            if (group.hasTitle()) {
                if (expandableListViewGroupPosition == groupPosition) {
                    // Titles have no children and are not selectable.
                    return group;
                }
                expandableListViewGroupPosition++;
            }

            if (groupPosition < expandableListViewGroupPosition + group.getOptions().size()) {
                return group.getOptions().get(groupPosition - expandableListViewGroupPosition);
            }

            expandableListViewGroupPosition += group.getOptions().size();
        }

        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        MenuOption option = (MenuOption)getGroup(groupPosition);

        if (option != null) {
            if (childPosition > -1 && childPosition < option.getChildren().size()) {
                return option.getChildren().get(childPosition);
            }
        }

        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return ((Integer)groupPosition).longValue();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return ((Integer)childPosition).longValue();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = m_menuOptionViewFactory.makeMenuOptionView(m_inflater, parent);
            IMenuOptionViewHolder viewHolder = m_menuOptionViewFactory.makeMenuOptionViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);
        }

        Object expandableListViewGroup = getGroup(groupPosition);
        if (MenuOption.class.isInstance(expandableListViewGroup)) {
            MenuOption menuOption = (MenuOption)expandableListViewGroup;
            ((IMenuOptionViewHolder) convertView.getTag()).populate(menuOption.getText());

            ImageView arrow = (ImageView) convertView.findViewById(R.id.searchbox_menu_group_icon);
            if (menuOption.hasChildren()) {
                arrow.setVisibility(View.VISIBLE);
            } else {
                arrow.setVisibility(View.GONE);
            }

            TextView text = (TextView) convertView.findViewById(R.id.searchbox_menu_item_text);
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, convertView.getResources().getDimension(R.dimen.font_size_menu_option_title));
            if (isExpanded) {
                convertView.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), R.color.wrld_blue));
                text.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.white));
                arrow.setImageResource(R.drawable.expander_expanded);
            } else {
                convertView.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), R.color.white));
                text.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.wrld_blue));
                arrow.setImageResource(R.drawable.expander);
            }
        }
        else if (MenuGroup.class.isInstance(expandableListViewGroup)){
            MenuGroup menuGroup = (MenuGroup)expandableListViewGroup;
            ((IMenuOptionViewHolder) convertView.getTag()).populate(menuGroup.getTitle());
            TextView text = (TextView) convertView.findViewById(R.id.searchbox_menu_item_text);
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, convertView.getResources().getDimension(R.dimen.font_size_menu_group_title));
        }

        if (requiresGroupDivider(groupPosition)) {
            convertView.findViewById(R.id.group_divider).setVisibility(View.VISIBLE);
        }
        else {
            convertView.findViewById(R.id.group_divider).setVisibility(View.GONE);
        }

        if (requiresOptionDivider(groupPosition, isExpanded)) {
            convertView.findViewById(R.id.option_divider).setVisibility(View.VISIBLE);
        }
        else {
            convertView.findViewById(R.id.option_divider).setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = m_menuChildViewFactory.makeMenuChildView(m_inflater, parent);
            IMenuChildViewHolder viewHolder = m_menuChildViewFactory.makeMenuChildViewHolder();
            viewHolder.initialise(convertView);
            convertView.setTag(viewHolder);
        }

        MenuChild child = (MenuChild)getChild(groupPosition, childPosition);
        ((IMenuChildViewHolder)convertView.getTag()).populate(child);

        if (isLastChild) {
            convertView.findViewById(R.id.option_divider).setVisibility(View.GONE);
        }
        else {
            convertView.findViewById(R.id.option_divider).setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return m_model.getGroups().isEmpty();
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return groupId * 10000L + childId;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return groupId * 10000L;
    }

    private boolean requiresGroupDivider(int groupPosition) {
        if (groupPosition < 0) { return false; }

        int expandableListViewGroupPosition = 0;
        for (MenuGroup group : m_model.getGroups()) {
            if (group.hasTitle()) {
                if (expandableListViewGroupPosition == groupPosition) {
                    return true;
                }
                expandableListViewGroupPosition++;
            }

            if (groupPosition < expandableListViewGroupPosition + group.getOptions().size()) {
                return (groupPosition - expandableListViewGroupPosition == 0) && !group.hasTitle();
            }

            expandableListViewGroupPosition += group.getOptions().size();
        }

        return false;
    }

    private boolean requiresOptionDivider(int groupPosition, boolean isExpanded) {
        if (groupPosition < 0) { return false; }

        int expandableListViewGroupPosition = 0;
        for (MenuGroup group : m_model.getGroups()) {
            if (group.hasTitle()) {
                if (expandableListViewGroupPosition == groupPosition) {
                    return !group.getOptions().isEmpty();
                }
                expandableListViewGroupPosition++;
            }

            if (groupPosition < expandableListViewGroupPosition + group.getOptions().size()) {
                return (groupPosition - expandableListViewGroupPosition != group.getOptions().size() - 1) && !isExpanded;
            }

            expandableListViewGroupPosition += group.getOptions().size();
        }

        return false;
    }
}
