package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.common.extensions.database.AdapterTemplate;
import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.entity.client;
import com.expertek.tradehouse.documents.entity.document;

import java.util.Arrays;
import java.util.List;

public class CreationActivity extends Activity {
    private final DbDictionaries dbc = Application.dictionaries.db();
    private document document = null;
    private InvoiceTypeAdapter adapterType = null;
    private ClientAdapter adapterClient = null;
    private Button buttonCreate = null;
    private Button buttonCancel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_activity);

        // Retrieve Activity parameters
        document = (document) getIntent().getSerializableExtra(document.class.getName());

        final EditText editNumber = findViewById(R.id.editNumber);
        editNumber.setText(document.DocName);

        adapterType = new InvoiceTypeAdapter(this, android.R.layout.simple_list_item_activated_1);
        adapterType.setDataSet(InvoiceTypeAdapter.createDataSet(this, document.DocType));

        final Spinner spinType = findViewById(R.id.spinType);
        spinType.setAdapter(adapterType);

        adapterClient = new ClientAdapter(this, android.R.layout.simple_list_item_activated_1);
        adapterClient.setDataSet(new PagingList<client>(dbc.clients().load()));

        final Spinner spinContragent = findViewById(R.id.spinContragent);
        spinContragent.setAdapter(adapterClient);

        buttonCreate = findViewById(R.id.buttonCreate);
        buttonCancel = findViewById(R.id.buttonCancel);

        buttonCreate.setOnClickListener(onClickAction);
        buttonCancel.setOnClickListener(onClickAction);
    }

    private final View.OnClickListener onClickAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buttonCreate.equals(v)) {
                actionSave();
            } else if (buttonCancel.equals(v)) {
                actionCancel();
            }
        }
    };

    protected void actionSave() {
        final EditText editNumber = findViewById(R.id.editNumber);
        final String text = editNumber.getText().toString();
        if (text.length() > 0) document.DocName = text;

        final Spinner spinType = findViewById(R.id.spinType);
        document.DocType = adapterType.getKey(spinType.getSelectedItemPosition());

        final Spinner spinContragent = findViewById(R.id.spinContragent);
        final client client = (client) spinContragent.getSelectedItem();
        document.ClientType = client.cli_type;
        document.ClientID = client.cli_code;

        final Intent intent = new Intent();
        intent.putExtra(document.class.getName(), document);

        setResult(RESULT_OK, intent);
        finish();
    }

    protected void actionCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

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
                if (code.endsWith("WB") && (filter.equals("*") || filter.contains(code))) {
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