package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.common.extensions.database.CurrencyFormatter;
import com.common.extensions.database.DateConverter;
import com.common.extensions.database.PagingList;
import com.common.extensions.exchange.ServiceConnector;
import com.common.extensions.exchange.ServiceInterface;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.document;
import com.expertek.tradehouse.documents.entity.line;
import com.expertek.tradehouse.tradehouse.TradeHouseService;
import com.expertek.tradehouse.tradehouse.Документы;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class InvoicesActivity extends Activity {
    private DBDocuments dbd = Application.documents.db();
    private PagingList<document> documents = null;
    private DocTypeAdapter adapterType = null;
    protected DocumentAdapter adapterDocument = null;
    private String[] filtertype = null;
    private int position = AdapterInterface.INVALID_POSITION;
    private TextView editSummary = null;
    protected Button buttonCreate = null;
    private Button buttonEdit = null;
    private Button buttonDelete = null;
    private Button buttonSend = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoices_activity);

        // Register Service
        tradehouse.registerService(false);

        editSummary = findViewById(R.id.editSummary);

        adapterType = new DocTypeAdapter(this, android.R.layout.simple_list_item_single_choice);
        adapterType.setOnItemSelectionListener(onTypeSelection);

        final Spinner spinSelector = findViewById(R.id.spinSelector);
        spinSelector.setAdapter(adapterType);

        adapterDocument = new DocumentAdapter(this, R.layout.invoice_document);
        adapterDocument.setOnItemSelectionListener(onDocumentSelection);

        final ListView listDocument = findViewById(R.id.listDocument);
        adapterDocument.setChoiceMode(listDocument, ListView.CHOICE_MODE_SINGLE);
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

        // Redraw states of Activity components
        onTypeSelection.onItemSelected(spinSelector, null, 0, 0);
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
        document document = (document) data.getSerializableExtra(document.class.getName());

        switch (requestCode) {
            case InvoiceEditActivity.REQUEST_ADD_DOCUMENT:
                if (!dbd.documents().duplicate(document)) {
                    documents.add(document);
                    position = documents.size() - 1;
                } else {
                    position = documents.indexOf(document);
                    if (position < 0) {
                        Dialogue.Duplicate(InvoicesActivity.this, document, null);
                        onDocumentSelection.onNothingSelected(null);
                        return; // not in selection
                    }
                    document = documents.get(position);
                }
                actionEdit(position);
                break;
            case InvoiceEditActivity.REQUEST_EDIT_DOCUMENT:
                documents.set(position, document);
                break;
            case InvoiceEditActivity.REQUEST_DELETE_DOCUMENT:
                documents.remove(position);
                onDocumentSelection.onNothingSelected(null);
                break;
        }

        documents.commit(new PagingList.Commit<document>() {
            @Override
            public void renew(document objects) {
                dbd.documents().insert(objects);
            }

            @Override
            public void delete(document objects) {
                dbd.documents().delete(objects);
            }
        });

        refreshActivityControls();
    }

    protected void actionCreate(int position) {
        assert adapterDocument.getDataSet() != null;

        final document document = new document();
        document.DocName = dbd.documents().getNextId();
        document.DocType = (filtertype != null ? TextUtils.join(",", filtertype) : null);
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
        Dialogue.Delete(this, documents.get(position), onDeleteClick);
    }

    protected void actionSend(int position) {
        //TODO: final document document = adapterDocument.getItem(position);
        tradehouse.enqueue(new ServiceInterface.JobInfo(1, Документы.class, tradehouse.receiver()), null);
    }

    private final DialogInterface.OnClickListener onDeleteClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which != DialogInterface.BUTTON_POSITIVE) return;
            final Intent intent = new Intent();
            intent.putExtra(line.class.getName(), adapterDocument.getItem(position));
            onActivityResult(InvoiceEditActivity.REQUEST_DELETE_DOCUMENT, RESULT_OK, intent);
        }
    };

    private void refreshActivityControls() {
        final String selectedKey = TextUtils.join(",", filtertype);
        editSummary.setText(CurrencyFormatter.format(dbd.documents().sumAllDocs(filtertype)));
        buttonCreate.setEnabled(selectedKey.length() <= 0 || selectedKey.contains("WB"));
    }

    private final AdapterInterface.OnItemSelectionListener onTypeSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            final String selectedKey = adapterType.getKey(position);
            filtertype = selectedKey.split(",");
            documents = new PagingList<document>(dbd.documents().getDocType(filtertype));
            adapterDocument.setDataSet(documents);
            refreshActivityControls();
            onDocumentSelection.onNothingSelected(parent);
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            filtertype = null;
            buttonCreate.setEnabled(false);
            onDocumentSelection.onNothingSelected(parent);
        }
    };

    private final AdapterInterface.OnItemSelectionListener onDocumentSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            InvoicesActivity.this.position = position;
            buttonEdit.setEnabled(true);
            buttonDelete.setEnabled(true);
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            InvoicesActivity.this.position = AdapterInterface.INVALID_POSITION;
            buttonEdit.setEnabled(false);
            buttonDelete.setEnabled(false);
        }
    };

    /**
     * Spinner data Adapter: list of DocTypes
     */
    private static class DocTypeAdapter extends AdapterTemplate<String> {
        public DocTypeAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setDataSet(context.getResources().getStringArray(R.array.document_filters));
            setHasStableIds(true);
        }

        @SafeVarargs
        public DocTypeAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setDataSet(context.getResources().getStringArray(R.array.document_filters));
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
        private static final HashMap<String, String> shortype = fillDocTypes();

        public DocumentAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(true);
        }

        @SafeVarargs
        public DocumentAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setHasStableIds(true);
        }

        private static HashMap<String, String> fillDocTypes() {
            final String[] document_types = Application.app().
                    getResources().getStringArray(R.array.document_types);
            final HashMap<String, String> result =
                    new HashMap<String, String>(document_types.length);
            final char[] buffer = new char[5];

            for (String doctype : document_types) {
                final String[] keyval = doctype.split("\\|");

                for (int i = 0, j = 0; i < buffer.length; i++) {
                    buffer[i] = keyval[1].charAt(j);
                    j = keyval[1].indexOf(' ', j);
                    if (++j <= 0) break;
                }
                result.put(keyval[0], new String(buffer));
            }
            return result;
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            final View owner = holder.getView();
            final document document = getItem(position);

            final TextView textDocName = owner.findViewById(R.id.textDocName);
            final TextView textDocType = owner.findViewById(R.id.textDocType);
            final TextView textStatus = owner.findViewById(R.id.textStatus);
            final TextView textFactSum = owner.findViewById(R.id.textFactSum);
            final TextView textStartDate = owner.findViewById(R.id.textStartDate);

            textDocName.setText(document.DocName);
            textDocType.setText(shortype.get(document.DocType));
            textStatus.setText(document.Status);
            textFactSum.setText(CurrencyFormatter.format(document.FactSum));
            textStartDate.setText(DateConverter.format(document.StartDate));
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
            if (item == null) return INVALID_ROW_ID;
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
