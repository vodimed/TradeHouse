package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.common.extensions.database.AdapterInterface;
import com.common.extensions.database.AdapterTemplate;
import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.documents.Documents;
import com.expertek.tradehouse.documents.entity.document;

import java.util.List;

public class InvoicesActivity extends Activity {
    private final Documents documents = MainApplication.dbd().documents();
    private DocTypesAdapter adaptertypes = null;
    private DocumentsAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoices_activity);

        adaptertypes = new DocTypesAdapter(this, android.R.layout.simple_list_item_single_choice);

        final Spinner spinSelector = findViewById(R.id.spinSelector);
        adaptertypes.setOnItemSelectionListener(onTypeSelection);
        spinSelector.setAdapter(adaptertypes);

        adapter = new DocumentsAdapter(this, android.R.layout.simple_list_item_single_choice);
        adapter.setDataSet(new PagingList<document>(documents.loadByDocType(adaptertypes.getKey(0))));

        final ListView listInvoices = findViewById(R.id.listInvoices);
        listInvoices.setAdapter(adapter);
    }

    private final AdapterInterface.OnItemSelectionListener onTypeSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            final String key = adaptertypes.getKey(position);
            adapter.setDataSet(new PagingList<document>(documents.loadByDocType(key)));
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            // Never to do
        }
    };

    /**
     * Spinner data Adapter: list of DocTypes
     */
    private static class DocTypesAdapter extends AdapterTemplate<String> {
        public DocTypesAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setDataSet(context.getResources().getStringArray(R.array.invoice_types));
            setHasStableIds(true);
        }

        @SafeVarargs
        public DocTypesAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setDataSet(context.getResources().getStringArray(R.array.invoice_types));
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
    private static class DocumentsAdapter extends AdapterTemplate<document> {
        public DocumentsAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(true);
        }

        @SafeVarargs
        public DocumentsAdapter(Context context, @NonNull Class<? extends View>... layer) {
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
            document item = getItem(position);
            return item.DocName.hashCode() * 31 + item.DocType.hashCode();
        }
    }
}
