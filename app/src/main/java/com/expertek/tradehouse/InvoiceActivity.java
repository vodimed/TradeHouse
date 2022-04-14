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

import com.common.extensions.Dialogue;
import com.common.extensions.database.AdapterInterface;
import com.common.extensions.database.AdapterTemplate;
import com.common.extensions.database.Formatter;
import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.entity.BarInfo;
import com.expertek.tradehouse.dictionaries.entity.barcode;
import com.expertek.tradehouse.dictionaries.entity.good;
import com.expertek.tradehouse.documents.entity.line;

import java.text.ParseException;
import java.util.List;

public class InvoiceActivity extends Activity {
    public static final int REQUEST_ADD_POSITION = 1;
    public static final int REQUEST_EDIT_POSITION = 2;
    private final DbDictionaries dbc = Application.dictionaries.db();
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
        setContentView(R.layout.invoice_activity);

        // Retrieve Activity parameters
        line = (line) getIntent().getSerializableExtra(line.class.getName());

        // Initiialize Barcode Reader
        detector.Initialize(this);

        editName = findViewById(R.id.editName);
        editName.setText(line.GoodsName);
        editName.setAdapter(new BarcodeAdapter(this, R.layout.barcode_lookup));
        editName.setOnItemClickListener(onClickBarcode);

        editPrice = findViewById(R.id.buttonPrice);
        if (line.Price != 0) editPrice.setText(Formatter.Currency.format(line.Price));

        editAmountDoc = findViewById(R.id.editAmountDoc);
        if (line.DocQnty != 0) editAmountDoc.setText(Formatter.Number.format(line.DocQnty));

        editAmountFact = findViewById(R.id.editAmountFact);
        if (line.FactQnty != 0) editAmountFact.setText(Formatter.Number.format(line.FactQnty));

        buttonOk = findViewById(R.id.buttonOk);
        buttonCancel = findViewById(R.id.buttonCancel);

        buttonOk.setOnClickListener(onClickAction);
        buttonCancel.setOnClickListener(onClickAction);
    }

    @Override
    protected void onResume() {
        super.onResume();
        detector.Resume();
    }

    @Override
    protected void onPause() {
        detector.Pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        detector.Finalize();
        super.onDestroy();
    }

    private void applyBarcode(barcode barcode, String name) {
        line.applyBC(barcode, name);
        editName.setText(line.GoodsName);
        editPrice.setText(Formatter.Currency.format(line.Price));
    }

    private final BarcodeDetector detector = new BarcodeDetector() {
        @Override
        protected void onBarcodeDetect(String scanned) {
            final barcode barcode = dbc.barcodes().get(scanned);
            if (barcode == null) {
                Dialogue.Error(InvoiceActivity.this, R.string.barcode_prompt);
                return;
            }
            final good good = dbc.goods().get(barcode.GoodsID);
            applyBarcode(barcode, good.Name);
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

    private final AdapterView.OnItemClickListener onClickBarcode = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final BarInfo barinfo = (BarInfo) parent.getItemAtPosition(position);
            applyBarcode(barinfo, barinfo.Name);
        }
    };

    /**
     * ListView data Adapter: list of Barcodes
     */
    protected static class BarcodeAdapter extends AdapterTemplate<BarInfo> implements Filterable {
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
            final BarInfo barinfo = getItem(position);

            final TextView textBC = owner.findViewById(R.id.textBC);
            final TextView textName = owner.findViewById(R.id.textName);

            textBC.setText(barinfo.BC);
            textName.setText(barinfo.Name);
        }

        @Override
        public BarInfo getItem(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof List<?>) {
                return (BarInfo) ((List<?>) dataset).get(position);
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
                    final PagingList<BarInfo> barinfo = new PagingList<BarInfo>(dbc.barcodes().loadInfo(constraint.toString()));
                    results.count = barinfo.size();
                    results.values = barinfo;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                BarcodeAdapter.this.setDataSet(results.values);
            }
        }
    }
}