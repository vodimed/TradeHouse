package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.common.extensions.database.AdapterInterface;
import com.common.extensions.database.AdapterTemplate;
import com.common.extensions.database.Formatter;
import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.components.BarcodeMarker;
import com.expertek.tradehouse.components.BarcodeProcessor;
import com.expertek.tradehouse.components.BarcodeScanner;
import com.expertek.tradehouse.components.Dialogue;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.entity.Barcode;

import java.text.ParseException;
import java.util.List;

public class PositionActivity extends Activity {
    public static final int REQUEST_ADD_POSITION = 1;
    public static final int REQUEST_EDIT_POSITION = 2;
    private BarcodeProcessor processor = null;
    private AutoCompleteTextView editName = null;
    protected EditText editPrice = null;
    private EditText editAmountDoc = null;
    private EditText editAmountFact = null;
    private Button buttonOk = null;
    private Button buttonCancel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.position_activity);

        // Retrieve Activity parameters
        processor = getIntent().getParcelableExtra(BarcodeProcessor.class.getName());

        //setTitle(R.string._msg_scan_item);

        // Initialize Barcode Reader
        scanner.Initialize(this);

        editName = findViewById(R.id.editName);
        editName.setText(processor.line.GoodsName);
        editName.setAdapter(new BarcodeAdapter(this, R.layout.barcode));
        editName.setOnFocusChangeListener(autoCompleteHandler);
        editName.setOnItemClickListener(autoCompleteHandler);
        editName.setOnClickListener(autoCompleteHandler);
        autoCompleteHandler.initialize(processor, editName);

        findViewById(R.id.layoutPrice).setVisibility(!processor.document.isInventory() ? View.VISIBLE : View.GONE);
        editPrice = findViewById(R.id.buttonPrice);
        editPrice.setText(processor.line.Price != 0 ? Formatter.Currency.format(processor.line.Price) : null);

        editAmountDoc = findViewById(R.id.editAmountDoc);
        editAmountDoc.setText(processor.line.DocQnty != 0 ? Formatter.Number.format(processor.line.DocQnty) : null);
        editAmountDoc.setEnabled(!processor.document.isInventory());

        editAmountFact = findViewById(R.id.editAmountFact);
        editAmountFact.setText(processor.line.FactQnty != 0 ? Formatter.Number.format(processor.line.FactQnty) : null);

        buttonOk = findViewById(R.id.buttonOk);
        buttonCancel = findViewById(R.id.buttonCancel);

        buttonOk.setOnClickListener(onClickAction);
        buttonCancel.setOnClickListener(onClickAction);
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
        super.onDestroy();
    }

    private void updateLine() {
        editName.setText(processor.line.GoodsName);
        editPrice.setText(Formatter.Currency.format(processor.line.Price));
        editAmountDoc.setText(processor.line.DocQnty != 0 ? Formatter.Number.format(processor.line.DocQnty) : null);
        editAmountFact.setText(processor.line.FactQnty != 0 ? Formatter.Number.format(processor.line.FactQnty) : null);
    }

    private final BarcodeScanner scanner = new BarcodeScanner() {
        @Override
        protected void onBarcodeDetect(String scanned) {
            final BarcodeMarker marker = new BarcodeMarker(scanned);
            if (processor.add(PositionActivity.this, marker) != BarcodeProcessor.ERROR_VALUE) {
                editName.setEnabled(!processor.isLineMarked());
                updateLine();
            }
        }
    };

    private final View.OnClickListener onClickAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buttonOk.equals(v)) {
                actionOk();
            } else if (buttonCancel.equals(v)) {
                actionCancel();
            }
        }
    };

    protected void actionOk() {
        try {
            processor.line.Price = Formatter.Currency.parse(editPrice.getText().toString());
            processor.line.DocQnty = Formatter.Number.parse(editAmountDoc.getText().toString());
            processor.line.FactQnty = Formatter.Number.parse(editAmountFact.getText().toString());
        } catch (ParseException e) {
            Dialogue.Error(this, R.string.msg_data_incorrect);
            return;
        }

        if ((editName.getText().length() <= 0) || (processor.line.GoodsID <= 0)) {
            Dialogue.Error(this, R.string.barcode_prompt);
            return;
        } else if (processor.line.FactQnty > processor.line.DocQnty) {
            Dialogue.Error(this, R.string.msg_quantity_mismatch);
            return;
        } else if ((processor.line.FactQnty < processor.line.DocQnty) && (processor.parentmark != null)) {
            Dialogue.Error(this, R.string.msg_pack_only);
            return;
        }

        final Intent intent = new Intent();
        intent.putExtra(BarcodeProcessor.class.getName(), processor);

        setResult(RESULT_OK, intent);
        finish();
    }

    protected void actionCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * ListView data Adapter: list of Barcodes
     */
    protected static class BarcodeAdapter extends AdapterTemplate<Barcode> implements Filterable {
        private final DbDictionaries dbc = Application.dictionaries.db();
        private final BarcodeFilter filter = new BarcodeFilter();

        public BarcodeAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(false);
        }

        @SafeVarargs
        public BarcodeAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setHasStableIds(false);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterInterface.Holder holder, int position) {
            final View owner = holder.getView();
            final Barcode barcode = getItem(position);

            final TextView textBC = owner.findViewById(R.id.textBC);
            final TextView textName = owner.findViewById(R.id.textName);

            textBC.setText(barcode.BC);
            textName.setText(dbc.goods().getName(barcode.GoodsID));
        }

        @Override
        public Barcode getItem(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof List<?>) {
                return (Barcode) ((List<?>) dataset).get(position);
            } else {
                return null;
            }
        }

        @Override
        public Filter getFilter() {
            return filter;
        }

        private class BarcodeFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults results = new FilterResults();
                if (constraint != null) {
                    final PagingList<Barcode> barinfo = new PagingList<Barcode>(dbc.barcodes().load(constraint.toString()));
                    results.count = barinfo.size();
                    results.values = barinfo;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                BarcodeAdapter.this.setDataSet(results.values);
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((Barcode) resultValue).BC;
            }
        }
    }

    private static class AutoCompleteHandler implements View.OnClickListener, View.OnFocusChangeListener, AdapterView.OnItemClickListener {
        private final PositionActivity context;
        private BarcodeProcessor processor = null;
        private View v = null;

        public AutoCompleteHandler(PositionActivity context) {
            this.context = context;
        }

        public void initialize(BarcodeProcessor processor, View v) {
            this.processor = processor;
            this.v = v;
            raise();
        }

        private void raise() {
            v.setFocusable(processor.line.GoodsID <= 0);
            v.setEnabled(!processor.isLineMarked());
        }

        @Override
        public void onClick(View v) {
            v.setFocusableInTouchMode(true);
            v.requestFocus();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            v.clearFocus();
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                ((TextView) v).setText(processor.line.BC);
            } else {
                final BarcodeMarker marker = new BarcodeMarker(((TextView) v).getText().toString());

                if (marker.gtin.equals(processor.line.BC)) {
                    ((TextView) v).setText(processor.line.GoodsName);
                    raise();
                } else if (processor.updateLine(context, marker)) {
                    context.updateLine();
                    raise();
                }
            }
        }
    }
    private final AutoCompleteHandler autoCompleteHandler = new AutoCompleteHandler(this);
}