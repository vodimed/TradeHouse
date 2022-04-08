package com.expertek.tradehouse;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.expertek.tradehouse.dictionaries.DbDictionaries;

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

        // Initiialize Barcode Reader
        detector.Initialize(this);

        editBarcode = findViewById(R.id.editBarcode);
        editName = findViewById(R.id.editName);
        buttonPrice = findViewById(R.id.buttonPrice);
        buttonPrice.setOnTouchListener(onPriceTouch);
    }

    @Override
    public void onResume() {
        super.onResume();
        detector.Resume();
    }

    @Override
    public void onPause() {
        detector.Pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        detector.Finalize();
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
            detector.Enabled(switch_on);
            // Do not "eat" events
            return false;
        }
    };

    private final BarcodeDetector detector = new BarcodeDetector() {
        @Override
        public void onBarcodeDetect(String scanned) {
            editBarcode.setText(scanned);
            final BarcodeResolver.Position position = BarcodeResolver.search(scanned);

            if (position != null) {
                editName.setText(position.good.Name);
                buttonPrice.setText(String.format(Locale.getDefault(), "%.2f", position.barcode.PriceBC));
                buttonPrice.setTextSize(getResources().getDimension(R.dimen.barc_normal));
            } else {
                editName.setText("");
                buttonPrice.setText(R.string.zero_value);
                buttonPrice.setTextSize(getResources().getDimension(R.dimen.barc_large));
            }
        }
    };
}
