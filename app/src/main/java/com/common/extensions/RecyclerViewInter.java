package com.common.extensions;

import android.content.Context;
import android.util.AttributeSet;
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Checkable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Extension of RecyclerView, allowing unmasked handling onItemClick events.
*/
public class RecyclerViewInter extends RecyclerView implements AdapterInterface.Viewer {
    private static final int CHECK_POSITION_SEARCH_DISTANCE = 20;
    private AdapterInterface.OnItemSelectionListener onItemSelectionListener = null;
    private final LongSparseArray<Boolean> selection = new LongSparseArray<Boolean>();
    private final LongSparseArray<Integer> hintpos = new LongSparseArray<Integer>();
    private int choiceMode = AbsListView.CHOICE_MODE_NONE;

    public RecyclerViewInter(@NonNull Context context) {
        super(context);
    }

    public RecyclerViewInter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewInter(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Object getSelectedItem() {
        final Adapter<?> adapter = getAdapter();
        if (!(adapter instanceof AdapterInterface)) return null;

        long position = selection.indexOfValue(Boolean.TRUE);
        if (adapter.hasStableIds()) position = hintpos.get(position, NO_POSITION);
        if (position == NO_POSITION) return null;

        return ((AdapterInterface<?>) adapter).getItem((int) position);
    }

    public void setChoiceMode(int choiceMode) {
        clearSelectionMarkers();
        this.choiceMode = choiceMode;
    }

    public int getChoiceMode() {
        return choiceMode;
    }

    @Override
    public void setOnItemSelectionListener(@Nullable  AdapterInterface.OnItemSelectionListener listener) {
        onItemSelectionListener = listener;
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        clearSelectionMarkers();
        super.setAdapter(adapter);
    }

    // See also: OnChildAttachStateChangeListener
    @Override
    public void onChildAttachedToWindow(@NonNull View child) {
        super.onChildAttachedToWindow(child);

        if (child instanceof Checkable) {
            final int position = getChildAdapterPosition(child);
            final Checkable view = (Checkable) child;

            if (!getAdapter().hasStableIds()) {
                view.setChecked(selection.get(position, Boolean.FALSE));
            } else {
                final long id = getChildItemId(child);
                final int hint = hintpos.get(id, NO_POSITION);
                if (Math.abs(position - hint) < CHECK_POSITION_SEARCH_DISTANCE)
                    view.setChecked(selection.get(id, Boolean.FALSE));
            }
        }
    }

    // We are unable to override layoutChildren(), because it is called
    // at least twice: with the old and then with the new item choices
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        final Adapter<?> adapter = getAdapter();
        final int size = adapter.getItemCount();
        final boolean stable = adapter.hasStableIds();

        // Check if Dataset was changed
        for (int i = 0; i < selection.size(); i++) {
            if (selection.valueAt(i)) {
                final long position = selection.keyAt(i);
                final boolean found = (!stable ? position < size : findPositionById(position) != NO_POSITION);
                if (!found) selection.put(position, Boolean.FALSE);
            }
        }

        if (selection == null || selection.indexOfValue(Boolean.TRUE) < 0) {
            clearSelectionMarkers();
            if (onItemSelectionListener != null)
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
        final View child = ListViewInter.getTouchEventView(this, ev);

        /*
        if (child != this) {
            final int position = getChildAdapterPosition(child);
            if (position != NO_POSITION) {
                final long id = getChildItemId(child);

                if (child instanceof Checkable)
                    performCheckable((Checkable) child, position, id);

                performItemClick(child, position, id);
            }
        }
        if (child != this) result |= child.dispatchTouchEvent(ev);
        */

        if (child != this) result |= child.dispatchTouchEvent(ev);
        return result;
    }

    // We simulate this method, because unable to intercept anything
    // after calling child.dispatchTouchEvent() in onTouchEvent()
    private boolean performItemClick(View view, int position, long id) {
        if (isSoundEffectsEnabled())
            playSoundEffect(SoundEffectConstants.CLICK);
        if (onItemSelectionListener != null)
            onItemSelectionListener.onItemSelection(this, view, position, id);
        return true;
    }

    // Control the states of Checkable elements
    private void performCheckable(Checkable view, long position, long id) {
        final boolean stable = getAdapter().hasStableIds();
        final int hint = (int) position;

        if (stable)
            position = (int) id;
        switch (choiceMode) {
            case AbsListView.CHOICE_MODE_SINGLE:
                if (selection.get(position, Boolean.FALSE)) break;
                final LayoutManager layout = getLayoutManager();
                if (layout != null) toggleLayout(layout);
                clearSelectionMarkers();
            case AbsListView.CHOICE_MODE_MULTIPLE:
                final boolean checked = !view.isChecked();
                selection.put(position, checked ? Boolean.TRUE : Boolean.FALSE);
                if (checked && stable) hintpos.put(position, hint);
                view.setChecked(checked);
                break;
        }
    }

    private void clearSelectionMarkers() {
        selection.clear();
        hintpos.clear();
    }

    private int findPositionById(long id) {
        final int hint = hintpos.get(id, NO_POSITION);
        final Adapter<?> adapter = getAdapter();
        final int size = adapter.getItemCount();

        if (hint != NO_POSITION) {
            for (int i = 0; i < CHECK_POSITION_SEARCH_DISTANCE * 2; i++) {
                final int pos = hint + (((i % 2) << 1) - 1) * (i + 1) / 2;
                if (pos < 0 || pos >= size) continue;
                if (adapter.getItemId(pos) == id) return pos;
            }
            hintpos.put(id, NO_POSITION);
        }
        return NO_POSITION;
    }

    private static void toggleLayout(LayoutManager layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View other = layout.getChildAt(i);

            if (other instanceof Checkable) {
                final Checkable item = (Checkable) other;
                if (item.isChecked()) item.setChecked(false);
            }
        }
    }
}
