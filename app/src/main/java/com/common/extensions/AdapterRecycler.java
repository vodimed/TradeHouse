package com.common.extensions;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class AdapterRecycler<Item>
        extends RecyclerView.Adapter<AdapterRecycler.ViewHolder>
        implements AdapterInterface<Item>
{
    private final AdapterTemplate<Item> template;

    public AdapterRecycler(Context context, @NonNull @LayoutRes int... resource) {
        this.template = new AdapterTemplate<Item>(ViewHolder.class, context, resource) {
            @Override
            public void onBindViewHolder(@NonNull Holder holder, int position) {
                AdapterRecycler.this.onBindViewHolder(holder, position);
            }

            @Override
            public int getItemCount() {
                return AdapterRecycler.this.getItemCount();
            }

            @Override
            public Item getItem(int position) {
                return AdapterRecycler.this.getItem(position);
            }

            @Override
            public long getItemId(int position) {
                return AdapterRecycler.this.getItemId(position);
            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return (ViewHolder) template.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        this.onBindViewHolder((ViewHolder) holder, position);
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
        return getItemCount();
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
