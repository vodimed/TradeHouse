package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.common.extensions.database.AdapterInterface;
import com.common.extensions.database.AdapterTemplate;
import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.documents.entity.document;
import com.expertek.tradehouse.documents.entity.line;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class InvoiceEditActivity extends Activity {
    public final static int REQUEST_ADD_DOCUMENT = 1;
    public final static int REQUEST_EDIT_DOCUMENT = 2;
    private static final DateFormat date = SimpleDateFormat.getInstance(); // SimpleDateFormat("dd.MM.yyyy HH:mm")
    private PagingList<line> lines = null;
    protected document document = null;
    protected LineAdapter adapterLine = null;
    private ListView listLine = null;
    private int position = AdapterInterface.INVALID_POSITION;
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
        lines = new PagingList<line>(MainApplication.dbd().lines().loadByDocument(document.DocName));

        final EditText editNumber = findViewById(R.id.editNumber);
        editNumber.setText(document.DocName);

        final TextView labelDate = findViewById(R.id.labelDate);
        labelDate.setText(date.format(document.StartDate));

        adapterLine = new LineAdapter(this, android.R.layout.simple_list_item_single_choice);
        adapterLine.setDataSet(lines);

        listLine = findViewById(R.id.listLine);
        listLine.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listLine.setSelector(android.R.drawable.list_selector_background);
        listLine.setAdapter(adapterLine);

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
                actionAdd(AdapterInterface.INVALID_POSITION);
            } else if (position == AdapterInterface.INVALID_POSITION) {
                // Do Nothing
            } else if (buttonEdit.equals(v)) {
                actionEdit(position);
            } else if (buttonSave.equals(v)) {
                actionSave(position);
            } else if (buttonSend.equals(v)) {
                actionSend(position);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        final line line = (line) data.getSerializableExtra(line.class.getName());

        switch (requestCode) {
            case InvoiceActivity.REQUEST_ADD_POSITION:
                lines.add(line);
                position = listLine.getCount() - 1;
                break;
            case InvoiceActivity.REQUEST_EDIT_POSITION:
                lines.set(position, line);
                break;
        }
    }

    protected void actionAdd(int position) {
        final line line = new line();
        line.DocName = document.DocName;

        final Intent intent = new Intent(InvoiceEditActivity.this, InvoiceActivity.class);
        intent.putExtra(line.class.getName(), line);
        startActivityForResult(intent, InvoiceActivity.REQUEST_ADD_POSITION);
    }

    protected void actionEdit(int position) {
        final Intent intent = new Intent(InvoiceEditActivity.this, InvoiceActivity.class);
        intent.putExtra(line.class.getName(), adapterLine.getItem(position));
        startActivityForResult(intent, InvoiceActivity.REQUEST_EDIT_POSITION);
    }

    protected void actionSave(int position) {
        lines.commit(new PagingList.Commit<line>() {
            @Override
            public void replace(line... objects) {
                MainApplication.dbd().lines().insert(objects);
            }

            @Override
            public void delete(line... objects) {
                MainApplication.dbd().lines().delete(objects);
            }
        });
    }

    protected void actionSend(int position) {
    }

    private final AdapterInterface.OnItemSelectionListener onDocumentSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            InvoiceEditActivity.this.position = position;
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            InvoiceEditActivity.this.position = AdapterInterface.INVALID_POSITION;
        }
    };

    /**
     * ListView data Adapter: list of Invoice entries
     */
    protected static class LineAdapter extends AdapterTemplate<line> {
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
            if (position < 0 || position >= getCount()) return INVALID_ROW_ID;
            return getItem(position).LineID;
        }
    }
}