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
import com.expertek.tradehouse.components.BarcodeMarker;
import com.expertek.tradehouse.components.BarcodeProcessor;
import com.expertek.tradehouse.components.BarcodeScanner;
import com.expertek.tradehouse.components.Dialogue;
import com.expertek.tradehouse.components.Logger;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.Document;
import com.expertek.tradehouse.documents.entity.Line;
import com.expertek.tradehouse.documents.entity.Markline;
import com.expertek.tradehouse.tradehouse.TradeHouseService;
import com.expertek.tradehouse.tradehouse.Проводка;

import java.util.List;

public class DocumentActivity extends Activity {
    public static final int REQUEST_ADD_DOCUMENT = 1;
    public static final int REQUEST_EDIT_DOCUMENT = 2;
    public static final int REQUEST_DELETE_DOCUMENT = 3;
    private final DBDocuments dbd = Application.documents.db();
    private BarcodeProcessor processor = null;
    protected Document document = null;
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
        document = (Document) getIntent().getSerializableExtra(Document.class.getName());
        final PagingList<Line> lines = new PagingList<Line>(dbd.lines().load(document.DocName));
        processor = new BarcodeProcessor(document, lines);

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

        buttonAdd.setEnabled(!document.isReadonly());
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
            final int position = processor.add(DocumentActivity.this, new BarcodeMarker(scanned));
            if (position == BarcodeProcessor.SINGLETON_LIST) {
                actionAdd();
            } else if (position != BarcodeProcessor.ERROR_VALUE) {
                listLine.setItemChecked(position, true);
                actionEdit();
            }
        }
    };

    private final View.OnClickListener onClickAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buttonAdd.equals(v)) {
                processor.selectLine(AdapterInterface.INVALID_POSITION, null);
                actionAdd();
            } else if (buttonEdit.equals(v)) {
                final int position = listLine.getCheckedItemPosition();
                if (position != AdapterInterface.INVALID_POSITION) {
                    processor.selectLine(position, null);
                    actionEdit();
                }
            } else if (buttonSave.equals(v)) {
                actionSave();
            } else if (buttonSend.equals(v)) {
                actionSend();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        processor.apply(data.getParcelableExtra(BarcodeProcessor.class.getName()));
        final int position = processor.acceptLine();
        editSummary.setText(Formatter.Currency.format(document.FactSum));

        if (position != listLine.getCheckedItemPosition()) {
            listLine.requestFocusFromTouch();
            listLine.clearFocus();
            listLine.setSelection(position);
            onLineSelection.onItemSelected(null, null, position, AdapterInterface.INVALID_ROW_ID);
        }
    }

    protected void actionAdd() {
        final Intent intent = new Intent(DocumentActivity.this, PositionActivity.class);
        intent.putExtra(BarcodeProcessor.class.getName(), processor);
        startActivityForResult(intent, PositionActivity.REQUEST_ADD_POSITION);
    }

    protected void actionEdit() {
        final Intent intent = new Intent(DocumentActivity.this, PositionActivity.class);
        intent.putExtra(BarcodeProcessor.class.getName(), processor);
        startActivityForResult(intent, PositionActivity.REQUEST_EDIT_POSITION);
    }

    protected void actionSave() {
        try {
            ((PagingList<Line>) processor.lines).commit(new PagingList.Commit<Line>() {
                @Override
                public void renew(Line[] objects) {
                    dbd.lines().insert(objects);
                }

                @Override
                public void delete(Line[] objects) {
                    dbd.lines().delete(objects);
                }
            });

            if (processor.marklines != null) {
                ((PagingList<Markline>) processor.marklines).commit(new PagingList.Commit<Markline>() {
                    @Override
                    public void renew(Markline[] objects) {
                        dbd.marklines().insert(objects);
                    }

                    @Override
                    public void delete(Markline[] objects) {
                        dbd.marklines().delete(objects);
                    }
                });
            }
        } catch (Exception e) {
            Dialogue.Error(this, e);
            return;
        }

        final Intent intent = new Intent();
        intent.putExtra(Document.class.getName(), document);

        setResult(RESULT_OK, intent);
        finish();
    }

    protected void actionSend() {
        final Document export = document;
        if (!export.isComplete()) return;
        final Bundle params = new Bundle();
        params.putSerializable(Document.class.getName(), export);
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
    protected static class LineAdapter extends AdapterTemplate<Line> {
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
            final Line line = getItem(position);

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
        public Line getItem(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof List<?>) {
                return (Line) ((List<?>) dataset).get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            if (position < 0 || position >= getCount()) return INVALID_ROW_ID;
            final Line item = getItem(position);
            if (item == null) return INVALID_ROW_ID;
            return getItem(position).LineID;
        }
    }

    private final ServiceConnector tradehouse = new ServiceConnector(this, TradeHouseService.class) {
        @Override
        public void onJobResult(@NonNull ServiceInterface.JobInfo work, Bundle result) {
            assert (result != null);
            document = (Document) result.getSerializable(Document.class.getName());
            actionSave();
        }

        @Override
        public void onJobException(@NonNull ServiceInterface.JobInfo work, @NonNull Throwable e) {
            Logger.w(e);
        }
    };
}