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
import com.expertek.tradehouse.dictionaries.Clients;
import com.expertek.tradehouse.dictionaries.entity.client;

import java.util.List;

public class InvoiceCreateActivity extends Activity {
    private final Clients clients = MainApplication.dbc().clients();
    private InvoiceTypesAdapter adaptertypes = null;
    private ClientsAdapter adapter = null;
    private String inventorytype = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_create_activity);

        adaptertypes = new InvoiceTypesAdapter(this, android.R.layout.simple_list_item_single_choice);

        final Spinner spinSelector = findViewById(R.id.spinSelector);
        adaptertypes.setOnItemSelectionListener(onTypeSelection);
        spinSelector.setAdapter(adaptertypes);

        adapter = new ClientsAdapter(this, android.R.layout.simple_list_item_single_choice);
        adapter.setDataSet(new PagingList<client>(clients.getAll()));

        final ListView listInvoices = findViewById(R.id.listInvoices);
        listInvoices.setAdapter(adapter);
    }

    private final AdapterInterface.OnItemSelectionListener onTypeSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            inventorytype = adaptertypes.getKey(position);
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            // Never to do
        }
    };

    /**
     * Spinner data Adapter: list of Invoice Types
     */
    private static class InvoiceTypesAdapter extends AdapterTemplate<String> {
        public InvoiceTypesAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setDataSet(context.getResources().getStringArray(R.array.invoice_types));
            setHasStableIds(true);
        }

        @SafeVarargs
        public InvoiceTypesAdapter(Context context, @NonNull Class<? extends View>... layer) {
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
     * Spinner data Adapter: list of Contragents
     */
    private static class ClientsAdapter extends AdapterTemplate<client> {
        public ClientsAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(true);
        }

        @SafeVarargs
        public ClientsAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setHasStableIds(true);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            final TextView text1 = holder.getView().findViewById(android.R.id.text1);
            text1.setText(getItem(position).Name);
        }

        @Override
        public client getItem(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof List<?>) {
                return (client) ((List<?>) dataset).get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            client item = getItem(position);
            return item.cli_code;
        }
    }
}