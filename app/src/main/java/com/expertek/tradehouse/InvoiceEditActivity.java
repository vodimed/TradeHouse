package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.common.extensions.database.AdapterTemplate;
import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.documents.Lines;
import com.expertek.tradehouse.documents.entity.document;
import com.expertek.tradehouse.documents.entity.line;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class InvoiceEditActivity extends Activity {
    private static final DateFormat date = SimpleDateFormat.getInstance(); // SimpleDateFormat("dd.MM.yyyy HH:mm")
    private final Lines lines = MainApplication.dbd().lines();
    protected document document = null;
    private LineAdapter adapterLine = null;
    protected ListView listInvoice = null;
    private Button buttonAdd = null;
    private Button buttonEdit = null;
    private Button buttonSave = null;
    private Button buttonSend = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_edit_activity);

        // Retrieve Activity parameters
        document = (document) getIntent().getSerializableExtra(document.class.getName());

        final EditText editNumber = findViewById(R.id.editNumber);
        editNumber.setText(document.DocName);

        final TextView labelDate = findViewById(R.id.labelDate);
        labelDate.setText(date.format(document.StartDate));

        adapterLine = new LineAdapter(this, android.R.layout.simple_list_item_single_choice);
        adapterLine.setDataSet(new PagingList<line>(lines.loadByDocument(document.DocName)));

        listInvoice = findViewById(R.id.listInvoice);
        listInvoice.setAdapter(adapterLine);

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
        final line line = new line();
        line.DocName = document.DocName;

        final Intent intent = new Intent(InvoiceEditActivity.this, InvoiceActivity.class);
        intent.putExtra(line.class.getName(), line);
        startActivity(intent);
    }

    protected void actionEdit() {
        final line line = (line) listInvoice.getSelectedItem();

        if (line != null) {
            final Intent intent = new Intent(InvoiceEditActivity.this, InvoiceActivity.class);
            intent.putExtra(line.class.getName(), line);
            startActivity(intent);
        }
    }

    protected void actionSave() {
    }

    protected void actionSend() {
    }

    /**
     * ListView data Adapter: list of Invoice entries
     */
    private static class LineAdapter extends AdapterTemplate<line> {
        public LineAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(true);
        }

        @SafeVarargs
        public LineAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setHasStableIds(true);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            final TextView text1 = holder.getView().findViewById(android.R.id.text1);
            text1.setText(getItem(position).GoodsName);
        }

        @Override
        public line getItem(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof List<?>) {
                return (line) ((List<?>) dataset).get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            if (position < 0) return INVALID_ROW_ID;
            return getItem(position).LineID;
        }
    }
}