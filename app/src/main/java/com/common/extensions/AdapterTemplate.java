package com.common.extensions;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class AdapterTemplate<Item> implements AdapterInterface<Item> {
    private final ArraySet<DataSetObserver> observers = new ArraySet<DataSetObserver>(1);
    private final Constructor<? extends Holder> creator;
    private final LayoutInflater inflater;
    private final int[] layout;

    public AdapterTemplate(Class<? extends Holder> holder,
                           Context context, @NonNull@LayoutRes int... resource)
    {
        try {
            this.creator = holder.getDeclaredConstructor(View.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid holder: " + holder.getName());
        }
        this.inflater = LayoutInflater.from(context);
        this.layout = resource;
    }

    public AdapterTemplate(Context context, @NonNull@LayoutRes int... resource) {
        this(ViewHolder.class, context, resource);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = inflater.inflate(layout[viewType], parent, false);
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
        throw new IllegalArgumentException("Invalid constructor: " + creator.getName());
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
        observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        observers.remove(observer);
    }

    @Override
    public int getCount() {
        return getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
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
        return (position % layout.length);
    }

    @Override
    public int getViewTypeCount() {
        return layout.length;
    }

    @Override
    public boolean isEmpty() {
        return (getCount() == 0);
    }

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
}
