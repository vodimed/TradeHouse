package com.expertek.tradehouse;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.expertek.tradehouse.components.BarcodeScanner;
import com.expertek.tradehouse.components.Marker;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.entity.barcode;

import java.util.Locale;

public class BarcodeActivity extends Activity {
    private final DbDictionaries dbc = Application.dictionaries.db();
    private EditText editBarcode = null;
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

    private final BarcodeScanner scanner = new BarcodeScanner() {
        @Override
        public void onBarcodeDetect(String scanned) {
            final Marker marker = new Marker(scanned);
            editBarcode.setText(marker.gtin);

            final barcode barcode = dbc.barcodes().get(marker.gtin);
            if (barcode != null) {
                editName.setText(dbc.goods().get(barcode.GoodsID).Name);
                buttonPrice.setText(String.format(Locale.getDefault(), "%.2f", barcode.PriceBC));
                buttonPrice.setTextSize(getResources().getDimension(R.dimen.barc_normal));
            } else {
                editName.setText("");
                buttonPrice.setText(R.string.zero_value);
                buttonPrice.setTextSize(getResources().getDimension(R.dimen.barc_large));
            }
        }
    };
}
