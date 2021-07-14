package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.common.extensions.database.AdapterTemplate;
import com.expertek.tradehouse.documents.Lines;
import com.expertek.tradehouse.documents.entity.document;

import java.util.List;

public class InvoiceEditActivity extends Activity {
    private final Lines lines = MainApplication.dbd().lines();
    private Button buttonAdd = null;
    private Button buttonEdit = null;
    private Button buttonSave = null;
    private Button buttonSend = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_edit_activity);

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonSave = findViewById(R.id.buttonSave);
        buttonSend = findViewById(R.id.buttonSend);

        buttonAdd.setOnClickListener(onClickAction);
        buttonEdit.setOnClickListener(onClickAction);
        buttonSave.setOnClickListener(onClickAction);
        buttonSend.setOnClickListener(onClickAction);
    }

    private final View.OnClickListener onClickAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buttonAdd.equals(v)) {
                actionAdd();
            } else if (buttonEdit.equals(v)) {
                actionEdit();
            } else if (buttonSave.equals(v)) {
                actionSave();
            } else if (buttonSend.equals(v)) {
                actionSend();
            }
        }
    };

    protected void actionAdd() {
        final Intent intent = new Intent(InvoiceEditActivity.this, InvoiceActivity.class);
        startActivity(intent);
    }

    protected void actionEdit() {
        final Intent intent = new Intent(InvoiceEditActivity.this, InvoiceActivity.class);
        startActivity(intent);
    }

    protected void actionSave() {

    }

    protected void actionSend() {

    }

    /**
     * ListView data Adapter: list of Invoice entries
     */
    private static class InvoiceAdapter extends AdapterTemplate<document> {
        public InvoiceAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(true);
        }

        @SafeVarargs
        public InvoiceAdapter(Context context, @NonNull Class<? extends View>... layer) {
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