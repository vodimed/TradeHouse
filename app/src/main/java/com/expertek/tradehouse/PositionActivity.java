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
import com.expertek.tradehouse.components.BarcodeScanner;
import com.expertek.tradehouse.components.Dialogue;
import com.expertek.tradehouse.components.Marker;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.entity.barcode;
import com.expertek.tradehouse.dictionaries.entity.good;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.line;
import com.expertek.tradehouse.documents.entity.markline;

import java.text.ParseException;
import java.util.List;

public class PositionActivity extends Activity {
    public static final int REQUEST_ADD_POSITION = 1;
    public static final int REQUEST_EDIT_POSITION = 2;
    private final DBDocuments dbd = Application.documents.db();
    private final DbDictionaries dbc = Application.dictionaries.db();
    protected final long firstLineId = dbd.marklines().getNextId();
    private PagingList<markline> marklines = null;
    private line line = null;
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
        final boolean inv = getIntent().getBooleanExtra("Inv", false);
        line = (line) getIntent().getSerializableExtra(line.class.getName());
        marklines = new PagingList<markline>(dbd.marklines().loadByDocument(line.DocName));
        appendMarkline((Marker) getIntent().getSerializableExtra(Marker.class.getName()));

        // Initialize Barcode Reader
        scanner.Initialize(this);

        editName = findViewById(R.id.editName);
        editName.setText(line.GoodsName);
        editName.setAdapter(new BarcodeAdapter(this, R.layout.barcode_lookup));
        editName.setOnFocusChangeListener(onEnterBarcode);
        editName.setOnItemClickListener(onClickBarcode);

        findViewById(R.id.layoutPrice).setVisibility(!inv ? View.VISIBLE : View.GONE);
        editPrice = findViewById(R.id.buttonPrice);
        editPrice.setText(line.Price != 0 ? Formatter.Currency.format(line.Price) : null);

        editAmountDoc = findViewById(R.id.editAmountDoc);
        editAmountDoc.setText(line.DocQnty != 0 ? Formatter.Number.format(line.DocQnty) : null);
        editAmountDoc.setEnabled(!inv);

        editAmountFact = findViewById(R.id.editAmountFact);
        editAmountFact.setText(line.FactQnty != 0 ? Formatter.Number.format(line.FactQnty) : null);

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

    private void updateLine(barcode barcode, double weight) {
        if (barcode == null) return;

        if (line.BC.isEmpty()) {
            final good good = dbc.goods().get(barcode.GoodsID);
            line.GoodsName = (good != null ? good.Name : null);
            line.GoodsID = barcode.GoodsID;
            line.BC = barcode.BC;
            line.UnitBC = barcode.UnitBC;
            line.Price = barcode.PriceBC;
        } else if (line.BC.equals(barcode.BC)) {
            line.DocQnty += weight;
            line.FactQnty += weight;
        }

        editPrice.setText(Formatter.Currency.format(line.Price));
        editAmountDoc.setText(line.DocQnty != 0 ? Formatter.Number.format(line.DocQnty) : null);
        editAmountFact.setText(line.FactQnty != 0 ? Formatter.Number.format(line.FactQnty) : null);
    }

    private void appendMarkline(Marker marker) {
        if (marker == null) return;

        final barcode barcode = dbc.barcodes().get(marker.gtin);

        if (barcode != null) {
            updateLine(barcode, marker.weight);
        } else {
            Dialogue.Error(PositionActivity.this, R.string.barcode_prompt);
        }

        editName.setText(line.GoodsName);
        //editName.setEnabled(false);
    }

    private final BarcodeScanner scanner = new BarcodeScanner() {
        @Override
        protected void onBarcodeDetect(String scanned) {
            final Marker marker = new Marker(scanned);
            if (marker.isWellformed()) {
                appendMarkline(marker);
            } else {
                Dialogue.Error(PositionActivity.this, R.string.barcode_prompt);
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
            line.Price = Formatter.Currency.parse(editPrice.getText().toString());
            line.DocQnty = Formatter.Number.parse(editAmountDoc.getText().toString());
            line.FactQnty = Formatter.Number.parse(editAmountFact.getText().toString());
        } catch (ParseException e) {
            Dialogue.Error(this, e);
            return;
        }

        final Intent intent = new Intent();
        intent.putExtra(line.class.getName(), line);

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
    protected static class BarcodeAdapter extends AdapterTemplate<barcode> implements Filterable {
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
            final barcode barcode = getItem(position);

            final TextView textBC = owner.findViewById(R.id.textBC);
            final TextView textName = owner.findViewById(R.id.textName);

            textBC.setText(barcode.BC);
            final good good = dbc.goods().get(barcode.GoodsID);
            textName.setText(good != null ? good.Name : null);
        }

        @Override
        public barcode getItem(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof List<?>) {
                return (barcode) ((List<?>) dataset).get(position);
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
                    final PagingList<barcode> barinfo = new PagingList<barcode>(dbc.barcodes().load(constraint.toString()));
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
                return ((barcode) resultValue).BC;
            }
        }
    }

    private final View.OnFocusChangeListener onEnterBarcode = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            final TextView view = (TextView) v;
            if (hasFocus) {
                view.setText(line.BC);
            } else {
                final String scanned = view.getText().toString();
                if (!scanned.equals(line.BC)) updateLine(dbc.barcodes().get(scanned), 0);
                view.setText(line.GoodsName);
            }
        }
    };

    private final AdapterView.OnItemClickListener onClickBarcode = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            updateLine((barcode) parent.getItemAtPosition(position), 0);
            editName.setFocusable(false);
            editName.setFocusableInTouchMode(true);
        }
    };
}