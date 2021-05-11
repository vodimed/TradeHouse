package com.common.extensions;

import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.Observable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Adapter for ListView class (when RecycledView library is not connected).
 * To correct handling onItemClick events items must implement Checkable interface
 */
public abstract class AdapterTemplate<Item>
        implements AdapterInterface<Item>
{
    public static final long NO_ID = -1;
    private final Notifier notifier = new Notifier();
    private final Constructor<? extends Holder> creator;
    private final Constructor<? extends View>[] instance;
    private final LayoutInflater inflater;
    private final int[] layout;
    private Cursor dataset = null;
    private boolean stableIds = false;

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

    public @Nullable Cursor dataset() {
        return this.dataset;
    }

    @SuppressWarnings("unchecked") // just only exception: when type of receiving variable != this
    public <C extends AdapterTemplate<Item>> C from(@Nullable Cursor dataset) {
        if (this.dataset != dataset) {
            if (this.dataset != null) this.dataset.unregisterDataSetObserver(notifier.observer);
            this.dataset = dataset;
            if (this.dataset != null) this.dataset.registerDataSetObserver(notifier.observer);
            notifier.observer.onChanged();
        }
        return (C) this;
    }

    @Override // returning ViewHolder would be invalid typecast for AdapterRecycler
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createViewHolder(parent, viewType);
    }

    protected final Holder createViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = createView(parent, viewType);
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

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        this.notifier.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.notifier.unregisterObserver(observer);
    }

    @Override
    public int getCount() {
        if (dataset == null) return 0;
        return dataset.getCount();
    }

    @Override
    public long getItemId(int position) {
        return NO_ID;
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
        return convertView;
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

    /**
     * Implementation of Dataset notification kernel
     */
    private static class Notifier extends Observable<DataSetObserver> {
        public boolean isEmpty() {
            return mObservers.isEmpty();
        }

        public final DataSetObserver observer = new DataSetObserver() {
            public void onChanged() {
                for (DataSetObserver observer : mObservers) observer.onChanged();
            }

            public void onInvalidated() {
                for (DataSetObserver observer : mObservers) observer.onInvalidated();
            }
        };
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
     * Simple Dataset template to avoid stupid code in user Application
     */
    public static abstract class SimpleCursor extends AbstractCursor {
        @Override
        public String[] getColumnNames() {
            return new String[0];
        }

        @Override
        public String getString(int column) {
            return null;
        }

        @Override
        public short getShort(int column) {
            return 0;
        }

        @Override
        public int getInt(int column) {
            return 0;
        }

        @Override
        public long getLong(int column) {
            return 0;
        }

        @Override
        public float getFloat(int column) {
            return 0;
        }

        @Override
        public double getDouble(int column) {
            return 0;
        }

        @Override
        public boolean isNull(int column) {
            return false;
        }
    }
}
