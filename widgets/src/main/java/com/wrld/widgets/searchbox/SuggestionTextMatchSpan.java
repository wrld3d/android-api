package com.wrld.widgets.searchbox;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.widget.TextView;

public class SuggestionTextMatchSpan extends CharacterStyle {
    private final int m_colour;

    private Typeface m_highlightTypeface;

    public SuggestionTextMatchSpan(int colour) {
        super();
        m_colour = colour;

        m_highlightTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setColor(m_colour);
        tp.setTypeface(m_highlightTypeface);
    }

    public void format(TextView textView, String string, String highlight) {
        CharSequence text = createSpannable(string, highlight);
        textView.setText(text);
    }

    public CharSequence createSpannable(String string, String highlight) {
        int higlightStart = string.toLowerCase().indexOf(highlight.toLowerCase());
        int higlightEnd = higlightStart + highlight.length();

        if(higlightStart < 0){
            return string;
        }

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
        applyStyleToSpannable(spannableStringBuilder, higlightStart, higlightEnd);
        return spannableStringBuilder;
    }

    private SpannableStringBuilder applyStyleToSpannable(SpannableStringBuilder source, int start, int end) {
        source.setSpan(CharacterStyle.wrap(this), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return source;
    }
}
