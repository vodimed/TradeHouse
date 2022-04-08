package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.common.extensions.database.AdapterInterface;
import com.common.extensions.database.AdapterTemplate;
import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.entity.client;
import com.expertek.tradehouse.documents.entity.document;

import java.util.Arrays;
import java.util.List;

public class InvoiceCreateActivity extends Activity {
    private final DbDictionaries dbc = Application.dictionaries.db();
    private document document = null;
    private InvoiceTypeAdapter adapterType = null;
    private ClientAdapter adapterClient = null;
    private Button buttonCreate = null;
    private Button buttonCancel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_create_activity);

        // Retrieve Activity parameters
        document = (document) getIntent().getSerializableExtra(document.class.getName());

        adapterType = new InvoiceTypeAdapter(this, android.R.layout.simple_list_item_activated_1);
        adapterType.setDataSet(InvoiceTypeAdapter.createDataSet(this, document.DocType));
        adapterType.setOnItemSelectionListener(onTypeSelection);

        final EditText editNumber = findViewById(R.id.editNumber);
        editNumber.setText(document.DocName);

        final Spinner spinSelector = findViewById(R.id.spinType);
        spinSelector.setAdapter(adapterType);
        onSpinnerInit(spinSelector, onTypeSelection);

        adapterClient = new ClientAdapter(this, android.R.layout.simple_list_item_activated_1);
        adapterClient.setDataSet(new PagingList<client>(dbc.clients().load()));
        adapterClient.setOnItemSelectionListener(onClientSelection);

        final Spinner spinContragent = findViewById(R.id.spinContragent);
        spinContragent.setAdapter(adapterClient);
        onSpinnerInit(spinContragent, onClientSelection);

        buttonCreate = findViewById(R.id.buttonCreate);
        buttonCancel = findViewById(R.id.buttonCancel);

        buttonCreate.setOnClickListener(onClickAction);
        buttonCancel.setOnClickListener(onClickAction);
    }

    private final View.OnClickListener onClickAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buttonCreate.equals(v)) {
                actionCreate();
            } else if (buttonCancel.equals(v)) {
                actionCancel();
            }
        }
    };

    protected void actionCreate() {
        final EditText editNumber = findViewById(R.id.editNumber);
        final String text = editNumber.getText().toString();
        if (text.length() > 0) document.DocName = text;

        final Intent intent = new Intent();
        intent.putExtra(document.class.getName(), document);

        setResult(RESULT_OK, intent);
        finish();
    }

    protected void actionCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void onSpinnerInit(Spinner spinner, AdapterInterface.OnItemSelectionListener listener) {
        if (listener != null) {
            final int selected = spinner.getSelectedItemPosition();

            if (selected != AdapterInterface.INVALID_POSITION) {
                listener.onItemSelected(spinner, spinner.getSelectedView(), selected, spinner.getSelectedItemId());
            } else {
                listener.onNothingSelected(spinner);
            }
        }
    }

    private final AdapterInterface.OnItemSelectionListener onClientSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            document.ClientID = (int) id;
            document.ClientType = adapterClient.getItem(position).cli_type;
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            // Never to do
        }
    };

    private final AdapterInterface.OnItemSelectionListener onTypeSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            document.DocType = adapterType.getKey(position);
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            // Never to do
        }
    };

    /**
     * Spinner data Adapter: list of Invoice Types
     */
    private static class InvoiceTypeAdapter extends AdapterTemplate<String> {
        public InvoiceTypeAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setDataSet(createDataSet(context, null));
            setHasStableIds(true);
        }

        @SafeVarargs
        public InvoiceTypeAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setDataSet(createDataSet(context, null));
            setHasStableIds(true);
        }

        public static String[] createDataSet(Context context, String filter) {
            final String[] dataset = context.getResources().getStringArray(R.array.document_types);
            if (filter == null) filter = "";
            int count = 0;
            for (int i = 0; i < dataset.length; i++) {
                final String code = dataset[i].split("\\|")[0];
                if (code.endsWith("WB") && (filter.length() <= 0 || filter.contains(code))) {
                    dataset[count++] = dataset[i];
                }
            }
            return Arrays.copyOf(dataset, count);
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
    private static class ClientAdapter extends AdapterTemplate<client> {
        public ClientAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(true);
        }

        @SafeVarargs
        public ClientAdapter(Context context, @NonNull Class<? extends View>... layer) {
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
            if (position < 0 || position >= getCount()) return INVALID_ROW_ID;
            final client item = getItem(position);
            if (item == null) return INVALID_ROW_ID;
            return getItem(position).cli_code;
        }
    }
}