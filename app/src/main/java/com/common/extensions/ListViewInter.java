package com.common.extensions;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;

/**
 * Extension of ListView, allowing unmasked handling onItemClick events.
 */
public class ListViewInter extends ListView implements AdapterInterface.Viewer {
    private AdapterInterface.OnItemSelectionListener onItemSelectionListener = null;

    public ListViewInter(Context context) {
        super(context);
    }

    public ListViewInter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewInter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ListViewInter(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setOnItemSelectionListener(@Nullable AdapterInterface.OnItemSelectionListener listener) {
        onItemSelectionListener = listener;
    }

    // We are unable to override layoutChildren(), because it is called
    // at least twice: with the old and then with the new item choices
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (onItemSelectionListener != null) {
            final SparseBooleanArray selection = getCheckedItemPositions();
            if (selection == null || selection.indexOfValue(true) < 0)
                onItemSelectionListener.onNothingSelected(this);
        }
    }

    // Documented feature: "Manage touch events in a ViewGroup"
    // https://developer.android.com/training/gestures/viewgroup
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = super.onTouchEvent(ev);
        final View child = getTouchEventView(this, ev);
        if (child != this) result |= child.dispatchTouchEvent(ev);
        return result;
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        if (isSoundEffectsEnabled())
            playSoundEffect(SoundEffectConstants.CLICK);
        if (onItemSelectionListener != null)
            onItemSelectionListener.onItemSelection(this, view, position, id);
        return super.performItemClick(view, position, id);
    }

    // Find leaf View in the Adapter's ViewGroup
    protected static View getTouchEventView(ViewGroup parent, MotionEvent event) {
        final int coordinateX = (int) event.getX();
        final int coordinateY = (int) event.getY();

        for (int i = 0; i < parent.getChildCount(); i++) {
            final View child = parent.getChildAt(i);

            if (child.getLeft() <= coordinateX && coordinateX <= child.getRight() &&
                child.getTop() <= coordinateY && coordinateY <= child.getBottom())
            {
                if (child instanceof ViewGroup) {
                    return getTouchEventView((ViewGroup) child, event);
                } else {
                    if (child.isSoundEffectsEnabled())
                        child.setSoundEffectsEnabled(false);
                    return child;
                }
            }
        }
        return parent;
    }
}
