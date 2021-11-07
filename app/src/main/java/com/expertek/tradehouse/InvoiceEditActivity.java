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
import com.common.extensions.database.CurrencyFormatter;
import com.common.extensions.database.DateConverter;
import com.common.extensions.database.PagingList;
import com.common.extensions.exchange.ServiceConnector;
import com.common.extensions.exchange.ServiceInterface;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.document;
import com.expertek.tradehouse.documents.entity.line;
import com.expertek.tradehouse.tradehouse.TradeHouseService;
import com.expertek.tradehouse.tradehouse.Проводка;

import java.util.List;

public class InvoiceEditActivity extends Activity {
    public static final int REQUEST_ADD_DOCUMENT = 1;
    public static final int REQUEST_EDIT_DOCUMENT = 2;
    public static final int REQUEST_DELETE_DOCUMENT = 3;
    private DBDocuments dbd = Application.documents.db();
    protected PagingList<line> lines = null;
    protected LineAdapter adapterLine = null;
    protected document document = null;
    protected final long firstLineId = dbd.lines().getNextId();
    private int position = AdapterInterface.INVALID_POSITION;
    private TextView editSummary = null;
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
        lines = new PagingList<line>(dbd.lines().loadByDocument(document.DocName));

        // Register Service
        tradehouse.registerService(false);

        final EditText editNumber = findViewById(R.id.editNumber);
        editNumber.setText(document.DocName);

        final TextView labelDate = findViewById(R.id.labelDate);
        labelDate.setText(DateConverter.format(document.StartDate));

        editSummary = findViewById(R.id.editSummary);
        editSummary.setText(CurrencyFormatter.format(document.FactSum));

        adapterLine = new LineAdapter(this, R.layout.invoice_position);
        adapterLine.setDataSet(lines);
        adapterLine.setOnItemSelectionListener(onLineSelection);

        final ListView listLine = findViewById(R.id.listLine);
        adapterLine.setChoiceMode(listLine, ListView.CHOICE_MODE_SINGLE);
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

        onLineSelection.onNothingSelected(null);
    }

    @Override
    protected void onDestroy() {
        tradehouse.unregisterService();
        super.onDestroy();
    }

    private final View.OnClickListener onClickAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buttonAdd.equals(v)) {
                actionAdd(AdapterInterface.INVALID_POSITION);
            } else if (buttonSave.equals(v)) {
                actionSave(position);
            } else if (buttonSend.equals(v)) {
                actionSend(position);
            } else if (position == AdapterInterface.INVALID_POSITION) {
                // Do Nothing
            } else if (buttonEdit.equals(v)) {
                actionEdit(position);
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
                position = lines.size() - 1;
                break;
            case InvoiceActivity.REQUEST_EDIT_POSITION:
                final line oldline = lines.get(position);
                document.FactSum -= oldline.FactQnty * oldline.Price;
                lines.set(position, line);
                break;
        }

        document.FactSum += line.FactQnty * line.Price;
        editSummary.setText(CurrencyFormatter.format(document.FactSum));
    }

    protected void actionAdd(int position) {
        final line line = new line();
        line.DocName = document.DocName;
        line.LineID = (int) firstLineId + lines.size();
        line.Pos = lines.size() + 1;

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
            public void renew(line objects) {
                dbd.lines().insert(objects);
            }

            @Override
            public void delete(line objects) {
                dbd.lines().delete(objects);
            }
        });

        final Intent intent = new Intent();
        intent.putExtra(document.class.getName(), document);

        setResult(RESULT_OK, intent);
        finish();
    }

    protected void actionSend(int position) {
        final document export = document;
        if (!export.Complete) return;
        final Bundle params = new Bundle();
        params.putSerializable(document.class.getName(), export);
        tradehouse.enqueue(new ServiceInterface.JobInfo(1, Проводка.class, tradehouse.receiver()), params);
    }

    private final AdapterInterface.OnItemSelectionListener onLineSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            InvoiceEditActivity.this.position = position;
            buttonEdit.setEnabled(true);
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
            InvoiceEditActivity.this.position = AdapterInterface.INVALID_POSITION;
            buttonEdit.setEnabled(false);
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
            final View owner = holder.getView();
            final line line = getItem(position);

            final TextView textPos = owner.findViewById(R.id.textPos);
            final TextView textGoodsName = owner.findViewById(R.id.textGoodsName);
            final TextView textUnitBC = owner.findViewById(R.id.textUnitBC);
            final TextView textPrice = owner.findViewById(R.id.textPrice);
            final TextView textFactQnty = owner.findViewById(R.id.textFactQnty);
            final TextView textDocQnty = owner.findViewById(R.id.textDocQnty);

            textPos.setText(String.valueOf(line.Pos));
            textGoodsName.setText(line.GoodsName);
            textUnitBC.setText(line.UnitBC);
            textPrice.setText(CurrencyFormatter.format(line.Price));
            textFactQnty.setText(CurrencyFormatter.format(line.FactQnty));
            textDocQnty.setText(CurrencyFormatter.format(line.DocQnty));
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
            final line item = getItem(position);
            if (item == null) return INVALID_ROW_ID;
            return getItem(position).LineID;
        }
    }

    private final ServiceConnector tradehouse = new ServiceConnector(this, TradeHouseService.class) {
        @Override
        public void onJobResult(@NonNull ServiceInterface.JobInfo work, Bundle result) {
            final document export = (document) result.getSerializable(document.class.getName());
            document = export;
            actionSave(position);
        }

        @Override
        public void onJobException(@NonNull ServiceInterface.JobInfo work, @NonNull Throwable e) {
            Dialogue.Error(InvoiceEditActivity.this, e);
        }
    };
}