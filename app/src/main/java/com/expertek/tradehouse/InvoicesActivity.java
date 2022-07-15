package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.expertek.tradehouse.components.MainSettings;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.Document;
import com.expertek.tradehouse.documents.entity.Line;
import com.expertek.tradehouse.tradehouse.TradeHouseService;
import com.expertek.tradehouse.tradehouse.TradeHouseTask;
import com.expertek.tradehouse.tradehouse.Документы;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class InvoicesActivity extends Activity {
    public static final int INVENTORIES = 1; // R.strings.document_filters
    public static final int INVOICES = 3; // R.strings.document_filters
    private final DBDocuments dbd = Application.documents.db();
    private PagingList<Document> documents = null;
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

        // Retrieve Activity parameters
        final int filter = getIntent().getIntExtra("document_filters", 0);

        // Register Service
        tradehouse.registerService(false);

        editSummary = findViewById(R.id.editSummary);

        //adapterType = new DocTypeAdapter(this, android.R.layout.simple_list_item_single_choice);
        //adapterType = new DocTypeAdapter(this, android.R.layout.simple_spinner_dropdown_item);
        adapterType = new DocTypeAdapter(this, android.R.layout.simple_list_item_activated_1);
        adapterType.setOnItemSelectionListener(onTypeSelection);

        final Spinner spinSelector = findViewById(R.id.spinSelector);
        spinSelector.setAdapter(adapterType);

        adapterDocument = new DocumentAdapter(this, R.layout.document);
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
        spinSelector.setSelection(filter);
        onTypeSelection.onItemSelected(spinSelector, null, filter, 0);
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
                actionCreate();
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
        final Document document = (Document) data.getSerializableExtra(Document.class.getName());

        switch (requestCode) {
            case DocumentActivity.REQUEST_ADD_DOCUMENT:
                if (!dbd.documents().hasDuplicate(document.DocName)) {
                    documents.add(document);
                    onDocumentSelection.setPosition(documents.size() - 1);
                } else if (onDocumentSelection.setPosition(documents.indexOf(document)) < 0) {
                    Dialogue.Duplicate(InvoicesActivity.this, document, null);
                    return; // not in selection
                }
                break;
            case DocumentActivity.REQUEST_EDIT_DOCUMENT:
                documents.set(onDocumentSelection.getPosition(), document);
                break;
            case DocumentActivity.REQUEST_DELETE_DOCUMENT:
                documents.remove(onDocumentSelection.getPosition());
                break;
        }

        try {
            documents.commit(new PagingList.Commit<Document>() {
                @Override
                public void renew(Document[] objects) {
                    dbd.documents().insert(objects);
                }

                @Override
                public void delete(Document[] objects) {
                    dbd.documents().delete(objects);
                }
            });

            switch (requestCode) {
                case DocumentActivity.REQUEST_ADD_DOCUMENT:
                    actionEdit(onDocumentSelection.getPosition());
                    break;
                case DocumentActivity.REQUEST_DELETE_DOCUMENT:
                    onDocumentSelection.setPosition(AdapterInterface.INVALID_POSITION);
                case DocumentActivity.REQUEST_EDIT_DOCUMENT:
                    refreshActivityControls();
            }
        } catch (Exception e) {
            documents.rollback();
            Dialogue.Error(this, e);
        }
    }

    protected void actionCreate() {
        assert adapterDocument.getDataSet() != null;

        final Document document = new Document();
        document.DocName = document.getNextId(dbd.documents().getMaxId());
        document.DocType = (filtertype != null ? TextUtils.join(",", filtertype) : null);
        document.StartDate = Calendar.getInstance().getTime();
        document.ObjectType = MainSettings.TradeHouseObjType;
        document.ObjectID = MainSettings.TradeHouseObjCode;
        document.UserID = MainSettings.TradeHouseUserId;
        document.UserName = MainSettings.TradeHouseUserName;
        document.Status = "НОВ";
        //TODO:Complete
        // document.Complete = true;
        document.Complete = false;

        final Intent intent = new Intent(InvoicesActivity.this, CreationActivity.class);
        intent.putExtra(Document.class.getName(), document);
        startActivityForResult(intent, DocumentActivity.REQUEST_ADD_DOCUMENT);
    }

    protected void actionEdit(int position) {
        final Intent intent = new Intent(InvoicesActivity.this, DocumentActivity.class);
        intent.putExtra(Document.class.getName(), adapterDocument.getItem(position));
        startActivityForResult(intent, DocumentActivity.REQUEST_EDIT_DOCUMENT);
    }

    protected void actionDelete(int position) {
        Dialogue.Delete(this, documents.get(position), onDeleteClick);
    }

    protected void actionSend(int position) {
        //TODO:Complete
//        final Document export = documents.get(position);
//        if (!export.isComplete()) return;
//        final Bundle params = new Bundle();
//        params.putSerializable(Document.class.getName(), export);
//        tradehouse.enqueue(new ServiceInterface.JobInfo(1, Проводка.class, tradehouse.receiver()), params);
        buttonSend.setEnabled(false);
        tradehouse.enqueue(new ServiceInterface.JobInfo(3, Документы.class, tradehouse.receiver()), null);
    }

    private final DialogInterface.OnClickListener onDeleteClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which != DialogInterface.BUTTON_POSITIVE) return;
            final Intent intent = new Intent();
            intent.putExtra(Line.class.getName(), documents.get(onDocumentSelection.getPosition()));
            onActivityResult(DocumentActivity.REQUEST_DELETE_DOCUMENT, RESULT_OK, intent);
        }
    };

    private void refreshActivityControls() {
        final String selectedKey = TextUtils.join(",", filtertype);
        editSummary.setText(Formatter.Currency.format(dbd.documents().sumAllDocs(filtertype)));
        buttonCreate.setEnabled(selectedKey.equals("*") || selectedKey.endsWith("WB"));
    }

    private final AdapterInterface.OnItemSelectionListener onTypeSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            final String selectedKey = adapterType.getKey(position);
            filtertype = selectedKey.split(",");
            documents = new PagingList<Document>(dbd.documents().load(filtertype));
            adapterDocument.setDataSet(documents);
            onDocumentSelection.onNothingSelected(parent);
            buttonCreate.setVisibility(position == INVENTORIES ? View.GONE : View.VISIBLE);
            refreshActivityControls();
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            filtertype = null;
            onDocumentSelection.onNothingSelected(parent);
            buttonCreate.setVisibility(View.VISIBLE);
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
            //TODO: https://stackoverflow.com/questions/48253761/how-do-i-clear-listview-selection
            //TODO: https://stackoverflow.com/questions/31413571/listview-how-to-clear-selection
            //         setNextSelectedPositionInt(position);
            //        requestLayout();
            //        invalidate();
            return getPosition();
        }

        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            buttonEdit.setEnabled(true);
            buttonDelete.setEnabled(true);
            //TODO:Complete
            // buttonSend.setEnabled(documents.get(position).isComplete());
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            buttonEdit.setEnabled(false);
            buttonDelete.setEnabled(false);
            //TODO:Complete
            // buttonSend.setEnabled(false);
        }
    }
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
    protected static class DocumentAdapter extends AdapterTemplate<Document> {
        private static final HashMap<String, Character> shortype = fillDocTypes();
        private static final char[] buffer = new char[2];

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
                    getResources().getStringArray(R.array.invoice_types);
            final HashMap<String, Character> result =
                    new HashMap<String, Character>(document_types.length);
            for (String doctype : document_types) {
                final String[] keyval = doctype.split("\\|");
                result.put(keyval[0], keyval[2].charAt(0));
            }
            return result;
        }

        private String shortstatus(String status) {
            if ((status == null) || status.length() <= 2) return status;
            buffer[0] = status.charAt(0);
            buffer[1] = status.charAt(status.length() - 1);
            return new String(buffer);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            final View owner = holder.getView();
            final Document document = getItem(position);

            final TextView textDocName = owner.findViewById(R.id.textDocName);
            final TextView textDocType = owner.findViewById(R.id.textDocType);
            final TextView textStatus = owner.findViewById(R.id.textStatus);
            final TextView textFactSum = owner.findViewById(R.id.textFactSum);
            final TextView textStartDate = owner.findViewById(R.id.textStartDate);

            textDocName.setText(document.DocName);
            textDocType.setText(shortype.get(document.DocType).toString());
            textDocType.setTypeface(null, (document.Complete ? Typeface.BOLD : Typeface.NORMAL));
            textStatus.setText(shortstatus(document.Status));
            textFactSum.setText(Formatter.Currency.format(document.FactSum));
            textStartDate.setText(Formatter.Date.format(document.StartDate));
        }

        @Override
        public Document getItem(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof List<?>) {
                return (Document) ((List<?>) dataset).get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            if (position < 0 || position >= getCount()) return INVALID_ROW_ID;
            final Document item = getItem(position);
            if (item == null) return INVALID_ROW_ID;
            return item.DocName.hashCode() * 31 + item.DocType.hashCode();
        }
    }

    //TODO
    protected static class DocumentAdapter1 extends BaseAdapter {
        private final Document[] array = new Document[30];
        private final LayoutInflater inflater;
        private final int layout;

        public DocumentAdapter1(Context context, @NonNull int layout) {
            super();
            this.inflater = LayoutInflater.from(context);
            this.layout = layout;
            for (int i = 0; i < array.length; i++) {
                array[i] = new Document();
                array[i].DocName = String.valueOf(i);
                array[i].DocType = "IntPurchWB";
                array[i].FactSum = 0.0;
                array[i].StartDate = Calendar.getInstance().getTime();
                array[i].Complete = false;
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
        public Document getItem(int position) {
            return array[position];
        }

        @Override
        public long getItemId(int position) {
            if (position < 0 || position >= getCount()) return AdapterInterface.INVALID_ROW_ID;
            final Document item = getItem(position);
            if (item == null) return AdapterInterface.INVALID_ROW_ID;
            return item.DocName.hashCode() * 31 + item.DocType.hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = inflater.inflate(layout, parent, false);

            final View owner = convertView;
            final Document document = getItem(position);

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
            buttonSend.setEnabled(true);
            // TODO:Complete
//            final Intent intent = new Intent();
//            intent.putExtras(result);
//            final Document export = (Document) result.getSerializable(Document.class.getName());
//            onDocumentSelection.setPosition(documents.indexOf(export));
//            onActivityResult(DocumentActivity.REQUEST_EDIT_DOCUMENT, RESULT_OK, intent);
            final File documents = Application.app().getDatabasePath(MainSettings.Documents_db);
            Application.replace_documents_db_file(TradeHouseTask.temporary(documents).getName(),
                    (Class<? extends DBDocuments>) result.getSerializable(documents.getName()));
            InvoicesActivity.this.recreate();
        }

        @Override
        public void onJobException(@NonNull ServiceInterface.JobInfo work, @NonNull Throwable e) {
            buttonSend.setEnabled(true);
            Logger.w(e);
        }
    };
}
