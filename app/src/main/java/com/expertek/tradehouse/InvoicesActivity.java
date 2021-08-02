package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.common.extensions.database.AdapterInterface;
import com.common.extensions.database.AdapterTemplate;
import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.documents.Documents;
import com.expertek.tradehouse.documents.entity.document;

import java.util.Calendar;
import java.util.List;

public class InvoicesActivity extends Activity {
    private final Documents documents = MainApplication.dbd().documents();
    private DocTypeAdapter adapterType = null;
    private DocumentAdapter adapterDocument = null;
    protected ListView listInvoices = null;
    protected Button buttonCreate = null;
    private Button buttonEdit = null;
    private Button buttonDelete = null;
    private Button buttonSend = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoices_activity);

        adapterType = new DocTypeAdapter(this, android.R.layout.simple_list_item_single_choice);

        final Spinner spinSelector = findViewById(R.id.spinSelector);
        adapterType.setOnItemSelectionListener(onTypeSelection);
        spinSelector.setAdapter(adapterType);

        adapterDocument = new DocumentAdapter(this, android.R.layout.simple_list_item_single_choice);
        adapterDocument.setDataSet(new PagingList<document>(documents.loadByDocType(adapterType.getKey(0))));

        listInvoices = findViewById(R.id.listInvoices);
        listInvoices.setAdapter(adapterDocument);

        buttonCreate = findViewById(R.id.buttonCreate);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonSend = findViewById(R.id.buttonSend);

        buttonCreate.setOnClickListener(onClickAction);
        buttonEdit.setOnClickListener(onClickAction);
        buttonDelete.setOnClickListener(onClickAction);
        buttonSend.setOnClickListener(onClickAction);
    }

    private final View.OnClickListener onClickAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buttonCreate.equals(v)) {
                actionCreate();
            } else if (buttonEdit.equals(v)) {
                actionEdit();
            } else if (buttonDelete.equals(v)) {
                actionDelete();
            } else if (buttonSend.equals(v)) {
                actionSend();
            }
        }
    };

    protected void actionCreate() {
        assert adapterDocument.getDataSet() != null;

        final document document = new document();
        document.DocName = documents.getMaxId(); //TODO: increment, init may be null
        document.StartDate = Calendar.getInstance().getTime();

        final Intent intent = new Intent(InvoicesActivity.this, InvoiceCreateActivity.class);
        intent.putExtra(document.class.getName(), document);
        startActivity(intent);
    }

    protected void actionEdit() {
        final document document = (document) listInvoices.getSelectedItem();

        if (document != null) {
            final Intent intent = new Intent(InvoicesActivity.this, InvoiceEditActivity.class);
            intent.putExtra(document.class.getName(), document);
            startActivity(intent);
        }
    }

    protected void actionDelete() {
        final document document = (document) listInvoices.getSelectedItem();

        if (document != null) {
            //TODO
        }
    }

    protected void actionSend() {
        final document document = (document) listInvoices.getSelectedItem();

        if (document != null) {
            //TODO
        }
    }

    private final AdapterInterface.OnItemSelectionListener onTypeSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            final String key = adapterType.getKey(position);
            adapterDocument.setDataSet(new PagingList<document>(documents.loadByDocType(key)));
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            // Never to do
        }
    };

    /**
     * Spinner data Adapter: list of DocTypes
     */
    private static class DocTypeAdapter extends AdapterTemplate<String> {
        public DocTypeAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setDataSet(context.getResources().getStringArray(R.array.doc_types));
            setHasStableIds(true);
        }

        @SafeVarargs
        public DocTypeAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setDataSet(context.getResources().getStringArray(R.array.doc_types));
            setHasStableIds(true);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            final TextView text1 = holder.getView().findViewById(android.R.id.text1);
            text1.setText(getItem(position));
        }

        @Override
        public String getItem(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof String[]) {
                final String item = ((String[]) dataset)[position];
                return item.split("\\|")[1];
            } else {
                return null;
            }
        }

        public String getKey(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof String[]) {
                final String item = ((String[]) dataset)[position];
                return item.split("\\|")[0];
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position; // constant list
        }
    }

    /**
     * ListView data Adapter: list of documents by DocType
     */
    private static class DocumentAdapter extends AdapterTemplate<document> {
        public DocumentAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(true);
        }

        @SafeVarargs
        public DocumentAdapter(Context context, @NonNull Class<? extends View>... layer) {
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
