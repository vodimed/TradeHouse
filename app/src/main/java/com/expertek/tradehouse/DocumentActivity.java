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
import com.common.extensions.database.Formatter;
import com.common.extensions.database.PagingList;
import com.common.extensions.exchange.ServiceConnector;
import com.common.extensions.exchange.ServiceInterface;
import com.expertek.tradehouse.components.BarcodeScanner;
import com.expertek.tradehouse.components.Dialogue;
import com.expertek.tradehouse.components.Logger;
import com.expertek.tradehouse.components.Marker;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.document;
import com.expertek.tradehouse.documents.entity.line;
import com.expertek.tradehouse.tradehouse.TradeHouseService;
import com.expertek.tradehouse.tradehouse.Проводка;

import java.util.List;

public class DocumentActivity extends Activity {
    public static final int REQUEST_ADD_DOCUMENT = 1;
    public static final int REQUEST_EDIT_DOCUMENT = 2;
    public static final int REQUEST_DELETE_DOCUMENT = 3;
    private final DBDocuments dbd = Application.documents.db();
    protected final long firstLineId = dbd.lines().getNextId();
    protected PagingList<line> lines = null;
    protected document document = null;
    private ListView listLine = null;
    private TextView editSummary = null;
    private Button buttonAdd = null;
    private Button buttonEdit = null;
    private Button buttonSave = null;
    private Button buttonSend = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_activity);

        // Retrieve Activity parameters
        document = (document) getIntent().getSerializableExtra(document.class.getName());
        lines = new PagingList<line>(dbd.lines().loadByDocument(document.DocName));

        // Register Service
        tradehouse.registerService(false);

        // Initialize Barcode Reader
        scanner.Initialize(this);

        final EditText editNumber = findViewById(R.id.editNumber);
        editNumber.setText(document.DocName);

        final TextView labelDate = findViewById(R.id.labelDate);
        labelDate.setText(Formatter.Date.format(document.StartDate));

        editSummary = findViewById(R.id.editSummary);
        editSummary.setText(Formatter.Currency.format(document.FactSum));

        final LineAdapter adapterLine = new LineAdapter(this, R.layout.invoice_position);
        adapterLine.setDataSet(lines);
        adapterLine.setOnItemSelectionListener(onLineSelection);

        listLine = findViewById(R.id.listLine);
        adapterLine.setChoiceMode(listLine, ListView.CHOICE_MODE_SINGLE);
        listLine.setAdapter(adapterLine);

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonSave = findViewById(R.id.buttonSave);
        buttonSend = findViewById(R.id.buttonSend);

        buttonAdd.setOnClickListener(onClickAction);
        buttonEdit.setOnClickListener(onClickAction);
        buttonSave.setOnClickListener(onClickAction);
        buttonSend.setOnClickListener(onClickAction);

        buttonAdd.setEnabled(document.isEditable());
        onLineSelection.onNothingSelected(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanner.Resume();
    }

    @Override
    protected void onPause() {
        scanner.Pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        scanner.Finalize();
        tradehouse.unregisterService();
        super.onDestroy();
    }

    private final BarcodeScanner scanner = new BarcodeScanner() {
        @Override
        protected void onBarcodeDetect(String scanned) {
            final Marker marker = new Marker(scanned);
            if (marker.isWellformed()) {
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).BC.equals(marker.gtin)) {
                        actionEdit(i);
                        return;
                    }
                }
                actionAdd(marker);
            } else {
                Dialogue.Error(DocumentActivity.this, R.string.barcode_prompt);
            }
        }
    };

    private final View.OnClickListener onClickAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buttonAdd.equals(v)) {
                actionAdd(null);
            } else if (buttonSave.equals(v)) {
                actionSave();
            } else if (buttonSend.equals(v)) {
                actionSend();
            } else if (buttonEdit.equals(v)) {
                final int position = listLine.getCheckedItemPosition();
                if (position != AdapterInterface.INVALID_POSITION) actionEdit(position);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        final line line = (line) data.getSerializableExtra(line.class.getName());

        switch (requestCode) {
            case PositionActivity.REQUEST_ADD_POSITION:
                lines.add(line);
                listLine.requestFocusFromTouch();
                listLine.clearFocus();
                listLine.setSelection(lines.size() - 1);
                break;
            case PositionActivity.REQUEST_EDIT_POSITION:
                final int position = listLine.getCheckedItemPosition();
                final line oldline = lines.get(position);
                document.FactSum -= oldline.FactQnty * oldline.Price;
                lines.set(position, line);
                break;
        }

        document.FactSum += line.FactQnty * line.Price;
        editSummary.setText(Formatter.Currency.format(document.FactSum));
    }

    private void actionAdd(Marker marker) {
        final line line = new line();
        line.DocName = document.DocName;
        line.LineID = (int) firstLineId + lines.size();
        line.Pos = lines.size() + 1;
        actionAdd(line, marker);
    }

    protected void actionAdd(line line, Marker marker) {
        final Intent intent = new Intent(DocumentActivity.this, PositionActivity.class);
        intent.putExtra("Inv", document.DocType.startsWith("Inv"));
        intent.putExtra(line.class.getName(), line);
        intent.putExtra(Marker.class.getName(), marker);
        startActivityForResult(intent, PositionActivity.REQUEST_ADD_POSITION);
    }

    protected void actionEdit(int position) {
        final Intent intent = new Intent(DocumentActivity.this, PositionActivity.class);
        intent.putExtra("Inv", document.DocType.startsWith("Inv"));
        intent.putExtra(line.class.getName(), lines.get(position));
        startActivityForResult(intent, PositionActivity.REQUEST_EDIT_POSITION);
    }

    protected void actionSave() {
        try {
            lines.commit(new PagingList.Commit<line>() {
                @Override
                public void renew(line[] objects) {
                    dbd.lines().insert(objects);
                }

                @Override
                public void delete(line[] objects) {
                    dbd.lines().delete(objects);
                }
            });
        } catch (Exception e) {
            Dialogue.Error(this, e);
            return;
        }

        final Intent intent = new Intent();
        intent.putExtra(document.class.getName(), document);

        setResult(RESULT_OK, intent);
        finish();
    }

    protected void actionSend() {
        final document export = document;
        if (!export.isComplete()) return;
        final Bundle params = new Bundle();
        params.putSerializable(document.class.getName(), export);
        tradehouse.enqueue(new ServiceInterface.JobInfo(1, Проводка.class, tradehouse.receiver()), params);
    }

    private final AdapterInterface.OnItemSelectionListener onLineSelection =
            new AdapterInterface.OnItemSelectionListener()
    {
        @Override
        public void onItemSelected(ViewGroup parent, View view, int position, long id) {
            buttonEdit.setEnabled(true);
        }

        @Override
        public void onNothingSelected(ViewGroup parent) {
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
            textPrice.setText(Formatter.Currency.format(line.Price));
            textFactQnty.setText(Formatter.Number.format(line.FactQnty));
            textDocQnty.setText(Formatter.Number.format(line.DocQnty));
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
            document = (document) result.getSerializable(document.class.getName());
            actionSave();
        }

        @Override
        public void onJobException(@NonNull ServiceInterface.JobInfo work, @NonNull Throwable e) {
            Logger.w(e);
        }
    };
}