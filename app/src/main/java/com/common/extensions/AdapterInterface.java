package com.common.extensions;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * If you use RecyclerView, implement these interfaces in
 * RecyclerView.Adapter and RecyclerView.ViewHoldes inheriters
 */
public interface AdapterInterface<Item> extends ListAdapter {
    Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);
    void onBindViewHolder(@NonNull Holder holder, int position);
    int getItemCount();
    @Override Item getItem(int position);
    long getItemId(int position);

    interface Holder {
        View getView();
    }

    interface Viewer {
        void setOnItemSelectionListener(@Nullable OnItemSelectionListener listener);
        void setSoundEffectsEnabled(boolean soundEffectsEnabled);
    }

    // Based on AdapterView.OnItemSelectedListener()
    interface OnItemSelectionListener {
        void onItemSelection(ViewGroup parent, View view, int position, long id);
        void onNothingSelected(ViewGroup parent);
    }
}
