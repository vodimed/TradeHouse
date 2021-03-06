package com.common.extensions.database;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SpinnerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * If you use RecyclerView, implement these interfaces in
 * RecyclerView.Adapter and RecyclerView.ViewHoldes inheriters
 */
public interface AdapterInterface<Item> extends ListAdapter, SpinnerAdapter {
    int INVALID_POSITION = AdapterView.INVALID_POSITION;
    long INVALID_ROW_ID = AdapterView.INVALID_ROW_ID;

    Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);
    void onBindViewHolder(@NonNull Holder holder, int position);
    int getItemCount();
    @Override Item getItem(int position);
    long getItemId(int position);
    void setChoiceMode(ViewGroup parent, int choiceMode);
    void setItemChecked(ViewGroup parent, int position, boolean value);
    void clearChoices(ViewGroup parent);

    interface Holder {
        View getView();
    }

    interface Handler {
        void onItemClick(ViewGroup parent, View layout, View view);
        void onDataSetChanged(ViewGroup parent);
    }

    interface Viewer {
        void setOnItemSelectionListener(@Nullable OnItemSelectionListener listener);
    }

    interface Observable<T> {
        void registerObserver(T observer);
        void unregisterObserver(T observer);
        void unregisterAll();
    }

    // Based on AdapterView.OnItemSelectedListener()
    interface OnItemSelectionListener {
        void onItemSelected(ViewGroup parent, View view, int position, long id);
        void onNothingSelected(ViewGroup parent);
    }
}
