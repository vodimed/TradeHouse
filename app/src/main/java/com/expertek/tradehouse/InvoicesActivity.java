package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.common.extensions.database.AdapterInterface;
import com.common.extensions.database.AdapterTemplate;
import com.common.extensions.database.Formatter;
import com.common.extensions.database.PagingList;
import com.common.extensions.exchange.ServiceConnector;
import com.common.extensions.exchange.ServiceInterface;
import com.expertek.tradehouse.components.Dialogue;
import com.expertek.tradehouse.components.Logger;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.document;
import com.expertek.tradehouse.documents.entity.line;
import com.expertek.tradehouse.tradehouse.TradeHouseService;
import com.expertek.tradehouse.tradehouse.Проводка;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class InvoicesActivity extends Activity {
    private final DBDocuments dbd = Application.documents.db();
    private PagingList<document> documents = null;
    private DocTypeAdapter adapterType = null;
    protected DocumentAdapter adapterDocument = null;
    ///*TODO*/protected DocumentAdapter1 adapterDocument = null;
    private String[] filtertype = null;
    private ListView listDocument = null;
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

        //adapterType = new DocTypeAdapter(this, android.R.layout.simple_list_item_single_choice);
        //adapterType = new DocTypeAdapter(this, android.R.layout.simple_spinner_dropdown_item);
        adapterType = new DocTypeAdapter(this, android.R.layout.simple_list_item_activated_1);
        adapterType.setOnItemSelectionListener(onTypeSelection);

        final Spinner spinSelector = findViewById(R.id.spinSelector);
        spinSelector.setAdapter(adapterType);

        adapterDocument = new DocumentAdapter(this, R.layout.invoice_document);
        ///*TODO*/adapterDocument = new DocumentAdapter1(this, R.layout.invoice_document);
        adapterDocument.setOnItemSelectionListener(onDocumentSelection);

        listDocument = findViewById(R.id.listDocument);
        adapterDocument.setChoiceMode(listDocument, ListView.CHOICE_MODE_SINGLE);
        ///*TODO*/listDocument.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
            final int position = onDocumentSelection.getPosition();
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
                if (!dbd.documents().hasDuplicate(document.DocName)) {
                    documents.add(document);
                    onDocumentSelection.setPosition(documents.size() - 1);
                } else if (onDocumentSelection.setPosition(documents.indexOf(document)) < 0) {
                    Dialogue.Duplicate(InvoicesActivity.this, document, null);
                    return; // not in selection
                }
                break;
            case InvoiceEditActivity.REQUEST_EDIT_DOCUMENT:
                documents.set(onDocumentSelection.getPosition(), document);
                break;
            case InvoiceEditActivity.REQUEST_DELETE_DOCUMENT:
                documents.remove(onDocumentSelection.getPosition());
                break;
        }

        try {
            documents.commit(new PagingList.Commit<document>() {
                @Override
                public void renew(document[] objects) {
                    dbd.documents().insert(objects);
                }

                @Override
                public void delete(document[] objects) {
                    dbd.documents().delete(objects);
                }
            });

            switch (requestCode) {
                case InvoiceEditActivity.REQUEST_ADD_DOCUMENT:
                    actionEdit(onDocumentSelection.getPosition());
                    break;
                case InvoiceEditActivity.REQUEST_DELETE_DOCUMENT:
                    onDocumentSelection.setPosition(AdapterInterface.INVALID_POSITION);
                case InvoiceEditActivity.REQUEST_EDIT_DOCUMENT:
                    refreshActivityControls();
            }
        } catch (Exception e) {
            documents.rollback();
            Dialogue.Error(this, e);
        }
    }

    protected void actionCreate(int position) {
        assert adapterDocument.getDataSet() != null;

        final document document = new document();
        document.DocName = document.getNextId(dbd.documents().getMaxId());
        document.DocType = (filtertype != null ? TextUtils.join(",", filtertype) : null);
        document.StartDate = Calendar.getInstance().getTime();
        document.Complete = true;

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
        final document export = documents.get(position);
        if (!export.Complete) return;
        final Bundle params = new Bundle();
        params.putSerializable(document.class.getName(), export);
        tradehouse.enqueue(new ServiceInterface.JobInfo(1, Проводка.class, tradehouse.receiver()), params);
    }

    private final DialogInterface.OnClickListener onDeleteClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which != DialogInterface.BUTTON_POSITIVE) return;
            final Intent intent = new Intent();
            intent.putExtra(line.class.getName(), documents.get(onDocumentSelection.getPosition()));
            onActivityResult(InvoiceEditActivity.REQUEST_DELETE_DOCUMENT, RESULT_OK, intent);
        }
    };

    private void refreshActivityControls() {
        final String selectedKey = TextUtils.join(",", filtertype);
        editSummary.setText(Formatter.Currency.format(dbd.documents().sumAllDocs(filtertype)));
        buttonCreate.setEnabled(selectedKey.equals("*") || selectedKey.contains("WB"));
    }

    private final AdapterInterface.OnItemSelectionListener onTypeSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            final String selectedKey = adapterType.getKey(position);
            filtertype = selectedKey.split(",");
            documents = new PagingList<document>(dbd.documents().loadByDocType(filtertype));
            adapterDocument.setDataSet(documents);
            onDocumentSelection.onNothingSelected(parent);
            refreshActivityControls();
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            filtertype = null;
            onDocumentSelection.onNothingSelected(parent);
            buttonCreate.setEnabled(false);
        }
    };

    private class ItemSelectionListener implements AdapterInterface.OnItemSelectionListener {
        public int getPosition() {
            return listDocument.getCheckedItemPosition();
        }

        public int setPosition(int position) {
            listDocument.setItemChecked(position, true);
            listDocument.requestFocusFromTouch();
            listDocument.clearFocus();
            listDocument.setSelection(position);
            return getPosition();
        }

        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            buttonEdit.setEnabled(true);
            buttonDelete.setEnabled(true);
            buttonSend.setEnabled(documents.get(position).Complete);
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            buttonEdit.setEnabled(false);
            buttonDelete.setEnabled(false);
            buttonSend.setEnabled(false);
        }
    };
    private final ItemSelectionListener onDocumentSelection = new ItemSelectionListener();

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
        private static final HashMap<String, Character> shortype = fillDocTypes();

        public DocumentAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(true);
        }

        @SafeVarargs
        public DocumentAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setHasStableIds(true);
        }

        private static HashMap<String, Character> fillDocTypes() {
            final String[] document_types = Application.app().
                    getResources().getStringArray(R.array.document_types);
            final HashMap<String, Character> result =
                    new HashMap<String, Character>(document_types.length);
            for (String doctype : document_types) {
                final String[] keyval = doctype.split("\\|");
                result.put(keyval[0], keyval[2].charAt(0));
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
            textDocType.setText(shortype.get(document.DocType).toString());
            textStatus.setText(document.Status);
            textFactSum.setText(Formatter.Currency.format(document.FactSum));
            textStartDate.setText(Formatter.Date.format(document.StartDate));
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

    protected static class DocumentAdapter1 extends BaseAdapter {
        private final document[] array = new document[30];
        private final LayoutInflater inflater;
        private final int layout;

        public DocumentAdapter1(Context context, @NonNull int layout) {
            super();
            this.inflater = LayoutInflater.from(context);
            this.layout = layout;
            for (int i = 0; i < array.length; i++) {
                array[i] = new document();
                array[i].DocName = String.valueOf(i);
                array[i].DocType = "IntPurchWB";
                array[i].FactSum = 0.0;
                array[i].StartDate = Calendar.getInstance().getTime();
                array[i].Complete = true;
            }
        }

        public void setDataSet(Object object) {
        }

        public Object getDataSet() {
            return array;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return array.length;
        }

        @Override
        public document getItem(int position) {
            return array[position];
        }

        @Override
        public long getItemId(int position) {
            if (position < 0 || position >= getCount()) return AdapterInterface.INVALID_ROW_ID;
            final document item = getItem(position);
            if (item == null) return AdapterInterface.INVALID_ROW_ID;
            return item.DocName.hashCode() * 31 + item.DocType.hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = inflater.inflate(layout, parent, false);

            final View owner = convertView;
            final document document = (document) getItem(position);

            final TextView textDocName = owner.findViewById(R.id.textDocName);
            final TextView textDocType = owner.findViewById(R.id.textDocType);
            final TextView textStatus = owner.findViewById(R.id.textStatus);
            final TextView textFactSum = owner.findViewById(R.id.textFactSum);
            final TextView textStartDate = owner.findViewById(R.id.textStartDate);

            textDocName.setText(document.DocName);
            textDocType.setText("WB");
            textStatus.setText(document.Status);
            textFactSum.setText(Formatter.Currency.format(document.FactSum));
            textStartDate.setText(Formatter.Date.format(document.StartDate));

            return convertView;
        }
    }

    private final ServiceConnector tradehouse = new ServiceConnector(this, TradeHouseService.class) {
        @Override
        public void onJobResult(@NonNull ServiceInterface.JobInfo work, Bundle result) {
            final Intent intent = new Intent();
            intent.putExtras(result);
            final document export = (document) result.getSerializable(document.class.getName());
            onDocumentSelection.setPosition(documents.indexOf(export));
            onActivityResult(InvoiceEditActivity.REQUEST_EDIT_DOCUMENT, RESULT_OK, intent);
        }

        @Override
        public void onJobException(@NonNull ServiceInterface.JobInfo work, @NonNull Throwable e) {
            Logger.w(e);
        }
    };
}
