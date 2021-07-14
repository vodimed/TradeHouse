package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.common.extensions.database.AdapterTemplate;
import com.expertek.tradehouse.documents.Lines;
import com.expertek.tradehouse.documents.entity.document;

import java.util.List;

public class InventoryEditActivity extends Activity {
    private final Lines lines = MainApplication.dbd().lines();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_edit_activity);
    }

    /**
     * ListView data Adapter: list of Inventory entries
     */
    private static class InventoryAdapter extends AdapterTemplate<document> {
        public InventoryAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(true);
        }

        @SafeVarargs
        public InventoryAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setHasStableIds(true);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            final TextView text1 = holder.getView().findViewById(android.R.id.text1);
            text1.setText(getItem(position).DocName);
        }

        @Override
        public document getItem(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof List<?>) {
                return (document) ((List<?>) dataset).get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            if (position < 0) return INVALID_ROW_ID;
            final document item = getItem(position);
            return item.DocName.hashCode() * 31 + item.DocType.hashCode();
        }
    }
}