package com.expertek.tradehouse;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.line;
import com.honeywell.aidc.BarcodeReadEvent;

import java.util.Locale;

public class BarcodeActivity extends Activity {
    private final DBDocuments dbd = Application.documents.db();
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
    public void onPause() {
        super.onPause();
        detector.Pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        detector.Resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detector.Finalize();
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
        public void onBarcodeDetect(BarcodeReadEvent event) {
            editBarcode.setText(event.getBarcodeData());

            final PagingList<line> lines = new PagingList<line>(
                    dbd.lines().findBarCode(event.getBarcodeData()));

            if (lines.size() > 0) {
                editName.setText(lines.get(0).GoodsName);
                buttonPrice.setText(String.format(Locale.getDefault(),
                        "%.2f", lines.get(0).Price));
                buttonPrice.setTextSize(48);
            } else {
                editName.setText("");
                buttonPrice.setText(R.string.zero_value);
                buttonPrice.setTextSize(72);
            }
        }
    };
}
