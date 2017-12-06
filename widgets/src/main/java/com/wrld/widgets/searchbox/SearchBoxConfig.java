package com.wrld.widgets.searchbox;
import android.graphics.Color;

public class SearchBoxConfig {
    private Color m_searchBoxDefaultColor;
    private Color m_searchBoxFocusColor;
    private Color m_searchBoxHoverColor;
    private Color m_searchBoxActiveColor;
    private Color m_fontColor;

    private int m_fontSize;

    private float m_searchBoxMarginLeft;
    private float m_searchBoxMarginTop;
    private float m_searchBoxResultSetPaddingTop;

    public Color getSearchBoxDefaultColor() {return m_searchBoxDefaultColor;}
    public Color getSearchBoxFocusColor() {return m_searchBoxFocusColor;}
    public Color getSearchBoxHoverColor() {return m_searchBoxHoverColor;}
    public Color getSearchBoxActiveColor() {return m_searchBoxActiveColor;}

    public int getFontSize() {return m_fontSize;}

    public float getSearchBoxMarginLeft() {return m_searchBoxMarginLeft;}
    public float getSearchBoxMarginTop() {return m_searchBoxMarginTop;}
    public float getSearchBoxResultSetPaddingTop() {return m_searchBoxResultSetPaddingTop;}
}
