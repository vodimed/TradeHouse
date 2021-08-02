package com.common.extensions.database;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.LongSparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AbsListView;
import android.widget.AdapterView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for ListView class (when RecycledView library is not connected).
 * To correct handling onItemClick events items must implement Checkable interface.
 * To suppress second onClick sound (echo), set ListView.setSoundEffectsEnabled(false).
 */
public abstract class AdapterTemplate<Item>
        implements AdapterInterface<Item>, AdapterInterface.Viewer
{
    private static final int CHECK_POSITION_SEARCH_DISTANCE = 20;
    private final EventHandler handler = new EventHandler();
    private final Listener delegate = new Listener(handler);
    private final Notifier notifier = new Notifier(handler);
    private final Constructor<? extends Holder> creator;
    private final Constructor<? extends View>[] instance;
    private final LayoutInflater inflater;
    private final int[] layout;
    private Object dataset = null;
    private boolean stableIds = false;
    /**
     * The list allows up to one choice, may be unchecked
     */
    public static final int CHOICE_MODE_ALONE = -1;

    public AdapterTemplate(Context context, @NonNull @LayoutRes int... layout) {
        this(ViewHolder.class, context, layout);
    }

    @SafeVarargs
    public AdapterTemplate(Context context, @NonNull Class<? extends View>... layer) {
        this(ViewHolder.class, context, layer);
    }

    protected AdapterTemplate(Class<? extends Holder> holder,
                           Context context, @NonNull@LayoutRes int... layout)
    {
        this.creator = getHolderCreator(holder);
        this.instance = null;
        this.inflater = LayoutInflater.from(context);
        this.layout = layout;
    }

    @SafeVarargs
    protected AdapterTemplate(Class<? extends Holder> holder,
                              Context context, @NonNull Class<? extends View>... layer)
    {
        this.creator = getHolderCreator(holder);
        this.instance = getViewCreators(layer);
        this.inflater = null;
        this.layout = null;
    }

    private Constructor<? extends Holder> getHolderCreator(Class<? extends Holder> holder) {
        try {
            return holder.getDeclaredConstructor(View.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid Holder: " + holder.getName());
        }
    }

    private Constructor<? extends View>[] getViewCreators(Class<? extends View>[] views) {
        @SuppressWarnings("unchecked") // it is even possible (save) to use Constructor<?>
        final Constructor<? extends View>[] result = new Constructor[views.length];

        for (int i = 0; i < views.length; i++) {
            try {
                result[i] = views[i].getDeclaredConstructor(Context.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Invalid View: " + views[i].getName());
            }
        }
        return result;
    }

    public @Nullable Object getDataSet() {
        return dataset;
    }

    public void setDataSet(@Nullable Object dataset) {
        if (dataset != this.dataset) {
            if (this.dataset instanceof AdapterInterface.Observable) {
                @SuppressWarnings("unchecked")
                final Observable<DataSetObserver> observable = (Observable<DataSetObserver>) this.dataset;
                observable.unregisterObserver(notifier.acceptor);
            } else if (this.dataset instanceof Cursor) {
                final Cursor cursor = (Cursor) this.dataset;
                cursor.unregisterContentObserver(notifier.content);
                cursor.unregisterDataSetObserver(notifier.acceptor);
            }

            if (dataset instanceof AdapterInterface.Observable) {
                @SuppressWarnings("unchecked")
                final Observable<DataSetObserver> observable = (Observable<DataSetObserver>) dataset;
                observable.registerObserver(notifier.acceptor);
            } else if (dataset instanceof Cursor) {
                final Cursor cursor = (Cursor) dataset;
                cursor.registerDataSetObserver(notifier.acceptor);
                cursor.registerContentObserver(notifier.content);
            }

            this.dataset = dataset;
            notifier.notifyChanged();
        }
    }

    @Override // returning ViewHolder would be invalid typecast for AdapterRecycler
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createViewHolder(parent, viewType);
    }

    protected final Holder createViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We have to protect View.AcessibilityDelegate from AbsListView.clearScrapForRebind(),
        // which drops acessibility of non-transient layout (if this layout consists of one View).
        // View.setHasTransientState(true) is overcomplicated way, so we just restore Acessibility
        // on stage of binding ViewHolder (onBindViewHolder) in getView() method of this class.
        final View view = acessible(createView(parent, viewType), true);
        // view.setHasTransientState(true);
        try {
            final Holder result = creator.newInstance(view);
            view.setTag(result);
            return result;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Invalid Constructor: " + creator.getName());
    }

    private View createView(@NonNull ViewGroup parent, int viewType) {
        if (inflater != null) {
            return inflater.inflate(layout[viewType], parent, false);
        } else try {
            return instance[viewType].newInstance(parent.getContext());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Invalid Constructor: " + instance[viewType].getName());
    }

    protected View acessible(View view, boolean indeepth) {
        if (!(view instanceof ViewGroup)) {
            view.setAccessibilityDelegate(delegate);
        } else if (indeepth) {
            final ViewGroup parent = (ViewGroup) view;
            for (int i = parent.getChildCount() - 1; i >= 0; i--) {
                acessible(parent.getChildAt(i), indeepth);
            }
        }
        return view;
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    protected void registerParentView(@NonNull ViewGroup parent) {
        notifier.registerParentView(parent);
    }

    protected void unregisterParentView(@NonNull ViewGroup parent) {
        notifier.unregisterParentView(parent);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        notifier.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        notifier.unregisterObserver(observer);
    }

    @Override
    public int getCount() {
        if (dataset instanceof Cursor) {
            return ((Cursor) dataset).getCount();
        } else if (dataset instanceof List) {
            return ((List<?>) dataset).size();
        } else if (dataset instanceof Object[]) {
            return ((Object[]) dataset).length;
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return INVALID_ROW_ID;
    }

    @Override
    public final boolean hasStableIds() {
        return stableIds; // Method is final in AdapterRecycler, so we should comply
    }

    public void setHasStableIds(boolean hasStableIds) {
        if (!notifier.isEmpty()) throw new IllegalStateException("Adapter has registered observers");
        stableIds = hasStableIds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView != null) {
            holder = (Holder) convertView.getTag();
        } else {
            holder = onCreateViewHolder(parent, getItemViewType(position));
            convertView = holder.getView();
        }
        onBindViewHolder(holder, position);
        return acessible(convertView, false); // instead of setHasTransientState(true)
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getItemViewType(int position) {
        return (position % getViewTypeCount());
    }

    @Override
    public int getViewTypeCount() {
        if (inflater != null) {
            return layout.length;
        } else {
            return instance.length;
        }
    }

    @Override
    public boolean isEmpty() {
        return (getCount() == 0);
    }

    @Override
    public void setOnItemSelectionListener(@Nullable OnItemSelectionListener listener) {
        handler.onItemSelectionListener = listener;
    }

    @Override
    public void setChoiceMode(ViewGroup parent, int choiceMode) {
        if (parent instanceof AbsListView) {
            final AbsListView abslist = (AbsListView) parent;
            abslist.setChoiceMode(choiceMode);
        } else {
            final Choicer choicer = getChoicerInternal(parent);
            if (choicer != null) {
                choicer.setChoiceMode(choiceMode);
            } else if (choiceMode != AbsListView.CHOICE_MODE_NONE) {
                parent.setTag(new Choicer(choiceMode));
            }
        }
    }

    // Unable to return Choicer for AbsListView
    @Nullable protected Choicer getChoicerInternal(ViewGroup parent) {
        if (parent.getTag() instanceof Choicer) {
            return (Choicer) parent.getTag();
        } else {
            return null;
        }
    }

    /**
     * Adapres's Event Handler Callback methods
     */
    protected int getChoiceMode(ViewGroup parent) {
        if (parent instanceof AbsListView) {
            final AbsListView abslist = (AbsListView) parent;
            return abslist.getChoiceMode();
        } else {
            final Choicer choicer = getChoicerInternal(parent);
            return (choicer != null ? choicer.getChoiceMode() : AbsListView.CHOICE_MODE_NONE);
        }
    }

    protected int getPositionForView(ViewGroup parent, View layout) {
        if (parent instanceof AdapterView) {
            final AdapterView<?> advlist = (AdapterView<?>) parent;
            return advlist.getPositionForView(layout);
        } else {
            return INVALID_POSITION;
        }
    }

    protected long getItemIdForView(ViewGroup parent, View layout) {
        if (parent instanceof AdapterView) {
            final AdapterView<?> advlist = (AdapterView<?>) parent;
            return advlist.getItemIdAtPosition(advlist.getPositionForView(layout));
        } else {
            return INVALID_ROW_ID;
        }
    }

    // Analyse currently selected positions of the ViewGroup
    protected boolean isAnyChecked(ViewGroup parent) {
        final SparseBooleanArray selection;
        if (parent instanceof AbsListView) {
            final AbsListView abslist = (AbsListView) parent;
            selection = abslist.getCheckedItemPositions();
        } else {
            final Choicer choicer = getChoicerInternal(parent);
            selection = (choicer != null ? choicer.getCheckedItemPositions() : null);
        }

        if (selection != null) {
            return (selection.indexOfValue(true) >= 0);
        } else {
            return false;
        }
    }

    // Result: True = processed (OnClick event), False = otherwise (unClick, whatever...)
    protected boolean performItemClick(ViewGroup parent, View layout, View view, int position) {
        boolean checked = true;
        if (parent instanceof AbsListView) {
            final AbsListView abslist = (AbsListView) parent;
            if (getChoiceMode(parent) != AbsListView.CHOICE_MODE_SINGLE)
                checked = !abslist.isItemChecked(position);
            abslist.setItemChecked(position, checked);
        } else {
            final Choicer choicer = getChoicerInternal(parent);
            if (choicer == null) return true; // CHOICE_MODE_NONE
            if (getChoiceMode(parent) != AbsListView.CHOICE_MODE_SINGLE)
                checked = !choicer.isItemChecked(position);
            final long id = getItemIdForView(parent, layout);
            if (choicer != null) choicer.setItemChecked(position, id, checked);
        }
        return checked;
    }

    // Synchronize Choicer with changed Dataset
    protected void performDataSetChanged(ViewGroup parent) {
        final Choicer choicer = getChoicerInternal(parent);
        if (choicer == null) return; // CHOICE_MODE_NONE

        final SparseBooleanArray selection = choicer.getCheckedItemPositions();
        final LongSparseArray<Integer> hintpos = choicer.getCheckedItemIds();

        if (hintpos.size() <= 0) {
            if (hasStableIds()) {
                choicer.clearSelectionMarkers();
            } else {
                final int count = getItemCount();
                for (int i = selection.size() - 1; i >= 0; i--) {
                    if (selection.valueAt(i) && (selection.keyAt(i) >= count)) {
                        selection.delete(selection.keyAt(i));
                    }
                }
            }
        } else {
            int delta = 0;
            for (int i = hintpos.size() - 1; i >= 0; i--) {
                final int position = hintpos.valueAt(i);
                final long id = hintpos.keyAt(i);
                final int reposition = findPositionById(id, position, delta);
                delta = reposition - position;

                if (reposition != position) {
                    selection.delete(position);
                    if (reposition != INVALID_POSITION) {
                        hintpos.setValueAt(i, reposition);
                        selection.put(reposition, true);
                    } else {
                        hintpos.removeAt(i);
                    }
                }
            }
        }
    }

    // Expanded search from the epicenter (pos = position)
    private int findPositionById(long id, int position, int delta) {
        final int size = getItemCount();
        position += delta;

        for (int i = 0; i < CHECK_POSITION_SEARCH_DISTANCE * 2; i++) {
            final int pos = position + (((i % 2) << 1) - 1) * (i + 1) / 2;
            if ((0 <= pos) && (pos < size) && (getItemId(pos) == id)) return pos;
        }
        return INVALID_POSITION;
    }

    /**
     * ViewHolder template. But you are not forced to use it (interface is enough)
     */
    public static class ViewHolder implements AdapterInterface.Holder {
        public final View itemView;

        public ViewHolder(@NonNull View itemView) {
            this.itemView = itemView;
        }

        @Override
        public View getView() {
            return itemView;
        }
    }

    /**
     * Feedback events from ViewHolder Views
     */
    private static class Listener extends View.AccessibilityDelegate {
        private final Handler handler;

        private Listener(Handler handler) {
            this.handler = handler;
        }

        // Retrieve ListView Layout of this View
        private View getLayout(View view) {
            while (!(view.getTag() instanceof Holder))
                view = (View) view.getParent();
            return view;
        }

        @Override
        public void sendAccessibilityEvent(View host, int eventType) {
            super.sendAccessibilityEvent(host, eventType);
            final View layout = getLayout(host);

            switch (eventType) {
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    handler.onItemClick((ViewGroup) layout.getParent(), layout, host);
                    break;
            }
        }
    }

    /**
     * Argegator of various events for Adapter
     */
    private class EventHandler implements Handler {
        public AdapterInterface.OnItemSelectionListener onItemSelectionListener = null;

        @Override
        public void onItemClick(ViewGroup parent, View layout, View view) {
            final int position = AdapterTemplate.this.getPositionForView(parent, layout);
            if (AdapterTemplate.this.performItemClick(parent, layout, view, position)) {
                processSelection(parent, layout, position);
            } else {
                processRejection(parent, layout);
            }
        }

        @Override
        public void onDataSetChanged(ViewGroup parent) {
            AdapterTemplate.this.performDataSetChanged(parent);
            processRejection(parent, null);
        }

        private void processSelection(ViewGroup parent, View layout, int position) {
            if (onItemSelectionListener != null) {
                if (parent.isSoundEffectsEnabled()) parent.playSoundEffect(SoundEffectConstants.CLICK);
                final long id = AdapterTemplate.this.getItemIdForView(parent, layout);
                onItemSelectionListener.onItemSelected(parent, layout, position, id);
            }
        }

        private void processRejection(ViewGroup parent, View layout) {
            if ((onItemSelectionListener != null) && (layout != null)) {
                if (parent.isSoundEffectsEnabled()) parent.playSoundEffect(SoundEffectConstants.CLICK);
            }
            if ((onItemSelectionListener != null) && !AdapterTemplate.this.isAnyChecked(parent)) {
                onItemSelectionListener.onNothingSelected(parent);
            }
        }
    }

    /**
     * Implementation of Dataset notification kernel
     */
    private static class Notifier extends DataSetObservable {
        private final ArrayList<ViewGroup> mParents = new ArrayList<ViewGroup>();
        private final Handler handler;

        private Notifier(Handler handler) {
            super();
            this.handler = handler;
        }

        public boolean isEmpty() {
            return mParents.isEmpty() && mObservers.isEmpty();
        }

        public void registerParentView(@NonNull ViewGroup parent) {
            if (mParents.contains(parent)) return;
            mParents.add(parent);
        }

        public void unregisterParentView(@NonNull ViewGroup parent) {
            mParents.remove(parent);
        }

        @Override
        public void unregisterAll() {
            mParents.clear();
            super.unregisterAll();
        }

        @Override
        public void notifyChanged() {
            synchronized(mParents) {
                super.notifyChanged();
                for (ViewGroup parent : mParents) {
                    handler.onDataSetChanged(parent);
                }
            }
        }

        @Override
        public void notifyInvalidated() {
            synchronized(mParents) {
                super.notifyInvalidated();
            }
        }

        public final DataSetObserver acceptor = new DataSetObserver() {
            @Override
            public void onChanged() {
                notifyChanged();
            }

            @Override
            public void onInvalidated() {
                notifyInvalidated();
            }
        };

        public final ContentObserver content = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange) {
                notifyChanged();
            }
        };
    }

    /**
     * Checkable logic implementation
     */
    protected static class Choicer {
        private final static boolean fastop = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
        private final SparseBooleanArray selection = new SparseBooleanArray();
        private final LongSparseArray<Integer> hintpos = new LongSparseArray<Integer>();
        private int choiceMode;

        public Choicer(int choiceMode) {
            this.choiceMode = choiceMode;
        }

        public void setChoiceMode(int choiceMode) {
            if (this.choiceMode != choiceMode) clearSelectionMarkers();
            this.choiceMode = choiceMode;
        }

        public int getChoiceMode() {
            return choiceMode;
        }

        public boolean isItemChecked(int position) {
            return selection.get(position);
        }

        public void setItemChecked(int position, long id, boolean value) {
            boolean checked = isItemChecked(position);
            switch (choiceMode) {
                case AbsListView.CHOICE_MODE_NONE:
                case AbsListView.CHOICE_MODE_MULTIPLE_MODAL:
                    break;
                case AbsListView.CHOICE_MODE_SINGLE:
                    if (checked) break;
                case AdapterTemplate.CHOICE_MODE_ALONE:
                    clearSelectionMarkers();
                case AbsListView.CHOICE_MODE_MULTIPLE:
                    if (!checked) {
                        selection.put(position, true);
                        if (id != INVALID_ROW_ID) hintpos.put(id, position);
                    } else {
                        selection.delete(position);
                        if (id != INVALID_ROW_ID) hintpos.delete(id);
                    }
                    break;
            }
        }

        public SparseBooleanArray getCheckedItemPositions() {
            return selection;
        }

        public LongSparseArray<Integer> getCheckedItemIds() {
            return hintpos;
        }

        public void clearSelectionMarkers() {
            selection.clear();
            hintpos.clear();
        }
    }
}
