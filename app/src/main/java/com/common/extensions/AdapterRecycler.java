package com.common.extensions;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for RecyclerView and ListView classes.
 * All ViewHolder controls must use ItemListener interface
 * (pass events through) for listening events of mouse clicks
 */
public abstract class AdapterRecycler<Item>
        extends RecyclerView.Adapter<AdapterRecycler.ViewHolder>
        implements AdapterInterface<Item>
{
    public static final long NO_ID = RecyclerView.NO_ID;
    private final AdapterTemplate<Item> template;

    public AdapterRecycler(Context context, @NonNull @LayoutRes int... layout) {
        this(ViewHolder.class, context, layout);
    }

    @SafeVarargs
    public AdapterRecycler(Context context, @NonNull Class<? extends View>... layer) {
        this(ViewHolder.class, context, layer);
    }

    protected AdapterRecycler(Class<? extends ViewHolder> holder,
                              Context context, @NonNull@LayoutRes int... layout)
    {
        super();
        this.template = new AdapterActual(holder, context, layout);
        this.template.registerDataSetObserver(observer); // Dataset notification
    }

    @SafeVarargs
    protected AdapterRecycler(Class<? extends ViewHolder> holder,
                              Context context, @NonNull Class<? extends View>... layer)
    {
        super();
        this.template = new AdapterActual(holder, context, layer);
        this.template.registerDataSetObserver(observer); // Dataset notification
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager current = recyclerView.getLayoutManager();
        if (current == null || !LinearLayoutManager.class.equals(current.getClass()))
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
    }

    private class AdapterActual extends AdapterTemplate<Item> {
        public AdapterActual(Context context, @NonNull int... layout) {
            super(context, layout);
        }

        protected AdapterActual(Class<? extends Holder> holder,
                                Context context, @NonNull int... layout)
        {
            super(holder, context, layout);
        }

        protected AdapterActual(Class<? extends Holder> holder,
                                Context context, @NonNull Class<? extends View>... layer)
        {
            super(holder, context, layer);
        }

        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return AdapterRecycler.this.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            AdapterRecycler.this.onBindViewHolder(holder, position);
        }

        @Override
        public Item getItem(int position) {
            return AdapterRecycler.this.getItem(position);
        }
    }

    public @Nullable Cursor dataset() {
        return template.dataset();
    }

    @SuppressWarnings("unchecked") // just only exception: when type of receiving variable != this
    public <C extends AdapterRecycler<Item>> C from(@Nullable Cursor dataset) {
        template.from(dataset);
        return (C) this;
    }

    @NonNull
    @Override // not Holder because Java type-conversion restrictions
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return (ViewHolder) template.createViewHolder(parent, viewType);
    }

    @Override // twin method because Java type-conversion restrictions
    public final void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        onBindViewHolder((Holder) holder, position);
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        template.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        template.unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return template.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return template.getView(position, convertView, parent);
    }

    @Override
    public int getViewTypeCount() {
        return template.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return template.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return template.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return template.isEnabled(position);
    }

    /**
     * Implementation of Dataset notification kernel
     */
    private final DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            notifyDataSetChanged();
        }
    };

    /**
     * ViewHolder template. You should inherit your ViewHolder from this class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements AdapterInterface.Holder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public View getView() {
            return itemView;
        }
    }
}
