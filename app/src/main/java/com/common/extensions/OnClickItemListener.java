package com.common.extensions;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Checkable;

import com.expertek.tradehouse.components.Logger;

import java.lang.reflect.Method;

/**
 * Alternative for AdapterTemplate & AdapterRecycler usage
 * (without capability of onNothingSelected() notification).
 * You should use this class for implementation OnClick()
 * listeners of all event receiving ViewHolder's elements
 */
public abstract class OnClickItemListener implements View.OnClickListener {
    private Method getChildAdapterPosition = null;
    private Method getChildItemId = null;
    private final int choiceMode;

    public OnClickItemListener() {
        this(AbsListView.CHOICE_MODE_NONE);
    }

    public OnClickItemListener(int choiceModeHint) {
        this.choiceMode = choiceModeHint;
    }

    // Override this method to allow control the checked list
    // AbsListView.CHOICE_MODE_SINGLE or CHOICE_MODE_MULTIPLE
    protected int getChoiceMode(ViewGroup parent) {
        if (parent instanceof AbsListView) {
            return ((AbsListView) parent).getChoiceMode();
        } else {
            return choiceMode;
        }
    }

    @Override
    public final void onClick(View v) {
        final ViewGroup parent = getParentAdapterView(v);
        if (parent == null) return;
        int position = 0;
        long id = 0;

        if (parent instanceof AdapterView) {
            position = ((AdapterView) parent).getPositionForView(v);
            id = ((AdapterView) parent).getItemIdAtPosition(position);
        } else try {
            position = (Integer) getChildAdapterPosition.invoke(parent, v);
            id = (Long) getChildItemId.invoke(parent, v);
        } catch (ReflectiveOperationException e) {
            Logger.e(e);
        }

        if (v instanceof Checkable) {
            final Checkable view = (Checkable) v;
            final boolean abs = (parent instanceof AbsListView);

            switch (getChoiceMode(parent)) {
                case AbsListView.CHOICE_MODE_SINGLE:
                    if (view.isChecked()) break;
                    if (!abs) toggleLayout(parent);
                case AbsListView.CHOICE_MODE_MULTIPLE:
                    view.toggle();
                    if (abs) ((AbsListView) parent).setItemChecked(position, view.isChecked());
                    break;
            }
        }

        onItemClick(parent, v, position, id);
    }

    public abstract void onItemClick(ViewGroup parent, View view, int position, long id);

    private ViewGroup getParentAdapterView(View view) {
        while (view.getParent() instanceof ViewGroup) {
            final ViewGroup parent = (ViewGroup) view.getParent();

            if (parent instanceof AdapterView) {
                return parent;
            } else if (isRecyclerViewDynamic(parent)) {
                return parent;
            }
        }
        return null;
    }

    private boolean isRecyclerViewDynamic(ViewGroup parent) {
        final Class<?> recycler = parent.getClass();
        try {
            getChildAdapterPosition = recycler.getMethod("getChildAdapterPosition", View.class);
            getChildItemId = recycler.getMethod("getChildItemId", View.class);
            return true;
        } catch (NoSuchMethodException e) {
            Logger.e(e);
            return false;
        }
    }

    private static void toggleLayout(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View other = layout.getChildAt(i);

            if (other instanceof Checkable) {
                final Checkable item = (Checkable) other;
                if (item.isChecked()) item.setChecked(false);
            }
        }
    }
}
