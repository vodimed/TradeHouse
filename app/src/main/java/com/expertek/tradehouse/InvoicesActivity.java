package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.common.extensions.exchange.ServiceConnector;
import com.common.extensions.exchange.ServiceInterface;
import com.expertek.tradehouse.documents.entity.document;
import com.expertek.tradehouse.documents.entity.line;
import com.expertek.tradehouse.tradehouse.TradeHouseService;
import com.expertek.tradehouse.tradehouse.Документы;

import java.util.Calendar;
import java.util.List;

public class InvoicesActivity extends Activity {
    private PagingList<document> documents = null;
    private DocTypeAdapter adapterType = null;
    protected DocumentAdapter adapterDocument = null;
    private ListView listDocument = null;
    private int position = AdapterInterface.INVALID_POSITION;
    protected Button buttonCreate = null;
    private Button buttonEdit = null;
    private Button buttonDelete = null;
    private Button buttonSend = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoices_activity);

        tradehouse.registerService(false);

        adapterType = new DocTypeAdapter(this, android.R.layout.simple_list_item_single_choice);
        adapterType.setOnItemSelectionListener(onTypeSelection);

        final Spinner spinSelector = findViewById(R.id.spinSelector);
        spinSelector.setAdapter(adapterType);

        documents = new PagingList<document>(MainApplication.dbd().documents().loadByDocType(adapterType.getKey(0)));

        adapterDocument = new DocumentAdapter(this, android.R.layout.simple_list_item_single_choice);
        adapterDocument.setDataSet(documents);
        adapterDocument.setOnItemSelectionListener(onDocumentSelection);

        listDocument = findViewById(R.id.listDocument);
        listDocument.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listDocument.setSelector(android.R.drawable.list_selector_background);
        listDocument.setAdapter(adapterDocument);

        buttonCreate = findViewById(R.id.buttonCreate);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonSend = findViewById(R.id.buttonSend);

        buttonCreate.setOnClickListener(onClickAction);
        buttonEdit.setOnClickListener(onClickAction);
        buttonDelete.setOnClickListener(onClickAction);
        buttonSend.setOnClickListener(onClickAction);
    }

    @Override
    protected void onDestroy() {
        tradehouse.unregisterService();
        super.onDestroy();
    }

    private final View.OnClickListener onClickAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buttonCreate.equals(v)) {
                actionCreate(AdapterInterface.INVALID_POSITION);
            } else if (position == AdapterInterface.INVALID_POSITION) {
                // Do Nothing
            } else if (buttonEdit.equals(v)) {
                actionEdit(position);
            } else if (buttonDelete.equals(v)) {
                actionDelete(position);
            } else if (buttonSend.equals(v)) {
                actionSend(position);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        final document document = (document) data.getSerializableExtra(document.class.getName());

        switch (requestCode) {
            case InvoiceEditActivity.REQUEST_ADD_DOCUMENT:
                documents.add(document);
                position = listDocument.getCount() - 1;
                actionEdit(position);
                break;
            case InvoiceEditActivity.REQUEST_EDIT_DOCUMENT:
                // documents.set(position, document); TODO
                break;
            case InvoiceEditActivity.REQUEST_DELETE_DOCUMENT:
                documents.remove(position);
                break;
        }

        documents.commit(new PagingList.Commit<document>() {
            @Override
            public void replace(document objects) {
                MainApplication.dbd().documents().insert(objects);
            }

            @Override
            public void delete(document objects) {
                MainApplication.dbd().documents().delete(objects);
            }
        });
    }

    protected void actionCreate(int position) {
        assert adapterDocument.getDataSet() != null;

        final document document = new document();
        document.DocName = MainApplication.dbd().documents().getMaxId();
        document.StartDate = Calendar.getInstance().getTime();

        final Intent intent = new Intent(InvoicesActivity.this, InvoiceCreateActivity.class);
        intent.putExtra(document.class.getName(), document);
        startActivityForResult(intent, InvoiceEditActivity.REQUEST_ADD_DOCUMENT);
    }

    protected void actionEdit(int position) {
        final Intent intent = new Intent(InvoicesActivity.this, InvoiceEditActivity.class);
        intent.putExtra(document.class.getName(), adapterDocument.getItem(position));
        startActivityForResult(intent, InvoiceEditActivity.REQUEST_EDIT_DOCUMENT);
    }

    protected void actionDelete(int position) {
        final Intent intent = new Intent();
        intent.putExtra(line.class.getName(), adapterDocument.getItem(position));
        onActivityResult(InvoiceEditActivity.REQUEST_DELETE_DOCUMENT, RESULT_OK, intent);
    }

    protected void actionSend(int position) {
        //TODO: final document document = adapterDocument.getItem(position);
        tradehouse.enqueue(new ServiceInterface.JobInfo(1, Документы.class, tradehouse.receiver()), null);
    }

    private final AdapterInterface.OnItemSelectionListener onTypeSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            final String key = adapterType.getKey(position);
            documents = new PagingList<document>(MainApplication.dbd().documents().loadByDocType(key));
            adapterDocument.setDataSet(documents);
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            // Never to do
        }
    };

    private final AdapterInterface.OnItemSelectionListener onDocumentSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            InvoicesActivity.this.position = position;
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            InvoicesActivity.this.position = AdapterInterface.INVALID_POSITION;
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
    protected static class DocumentAdapter extends AdapterTemplate<document> {
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
            if (position < 0 || position >= getCount()) return INVALID_ROW_ID;
            final document item = getItem(position);
            return item.DocName.hashCode() * 31 + item.DocType.hashCode();
        }
    }

    private final ServiceConnector tradehouse = new ServiceConnector(this, TradeHouseService.class) {
        @Override
        public void onJobResult(@NonNull ServiceInterface.JobInfo work, Bundle result) {
            Log.d("RESULT", result.toString());
        }

        @Override
        public void onJobException(@NonNull ServiceInterface.JobInfo work, @NonNull Throwable e) {
            Log.d("EXCEPTION", e.toString());
        }
    };
}
