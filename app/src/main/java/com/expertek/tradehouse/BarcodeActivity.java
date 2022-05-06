package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import com.expertek.tradehouse.components.BarcodeScanner;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.entity.Barcode;
import com.expertek.tradehouse.dictionaries.entity.Good;

import java.util.List;

public class BarcodeActivity extends Activity {
    private final DbDictionaries dbc = Application.dictionaries.db();
    private AutoCompleteTextView editBarcode = null;
    private EditText editName = null;
    private Button buttonPrice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_actvity);

        // Initialize Barcode Reader
        scanner.Initialize(this);

        editBarcode = findViewById(R.id.editBarcode);
        editName = findViewById(R.id.editName);
        buttonPrice = findViewById(R.id.buttonPrice);
        buttonPrice.setOnTouchListener(onPriceTouch);

        editBarcode.setAdapter(new BarcodeAdapter(this, R.layout.barcode_lookup));
        editBarcode.setOnEditorActionListener(autoCompleteHandler);
        editBarcode.setOnItemClickListener(autoCompleteHandler);
    }

    @Override
    public void onResume() {
        super.onResume();
        scanner.Resume();
    }

    @Override
    public void onPause() {
        scanner.Pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        scanner.Finalize();
        super.onDestroy();
    }

    private final View.OnTouchListener onPriceTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.performClick();
            boolean switch_on;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    switch_on = true;
                    break;
                case MotionEvent.ACTION_UP:
                    switch_on = false;
                    break;
                default:
                    return false;
            }
            scanner.Enabled(switch_on);
            // Do not "eat" events
            return false;
        }
    };

    private void displayBarcode(Barcode barcode) {
        if (barcode != null) {
            final Good good = dbc.goods().get(barcode.GoodsID);
            editName.setText(good != null ? good.Name : null);
            buttonPrice.setText(Formatter.Currency.format(barcode.PriceBC));
        } else {
            editName.setText("");
            buttonPrice.setText(R.string.zero_value);
        }
    }

    private final BarcodeScanner scanner = new BarcodeScanner() {
        @Override
        public void onBarcodeDetect(String scanned) {
            final BarcodeMarker marker = new BarcodeMarker(scanned);
            editBarcode.setText(marker.gtin);
            displayBarcode(dbc.barcodes().get(marker.gtin));
         }
    };

    /**
     * ListView data Adapter: list of Barcodes
     */
    protected static class BarcodeAdapter extends AdapterTemplate<Barcode> implements Filterable {
        private final DbDictionaries dbc = Application.dictionaries.db();
        private final BarcodeAdapter.BarcodeFilter filter = new BarcodeAdapter.BarcodeFilter();

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
            final Good good = dbc.goods().get(barcode.GoodsID);
            textName.setText(good != null ? good.Name : null);
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

    private static class AutoCompleteHandler implements TextView.OnEditorActionListener, AdapterView.OnItemClickListener {
        private final BarcodeActivity context;
        private final DbDictionaries dbc;

        public AutoCompleteHandler(BarcodeActivity context, DbDictionaries dbc) {
            this.context = context;
            this.dbc = dbc;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            context.displayBarcode((Barcode) parent.getItemAtPosition(position));
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == KeyEvent.KEYCODE_CALL) {
                context.displayBarcode(dbc.barcodes().get(v.getText().toString()));
                return true;
            }
            return false;
        }
    }
    private final AutoCompleteHandler autoCompleteHandler = new AutoCompleteHandler(this, dbc);
}
