package com.expertek.tradehouse;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Checkable;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class CheckableLayout extends LinearLayout implements Checkable {
    private boolean checked = false;

    public CheckableLayout(Context context) {
        super(context);
    }

    public CheckableLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        refreshDrawableState();
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }
}
