package com.expertek.tradehouse;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.entity.line;
import com.honeywell.aidc.AidcException;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BarcodeActivity extends Activity implements BarcodeReader.BarcodeListener {
    private final DBDocuments dbd = Application.documents.db();
    private AidcManager aidcManager = null;
    private BarcodeReader barcodeReader = null;
    private String editBartext = null;
    private EditText editBarcode = null;
    private EditText editName = null;
    private Button buttonPrice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_actvity);

        // Initiialize Barcode Reader
        AidcManager.create(this, manager);

        editBarcode = findViewById(R.id.editBarcode);
        editName = findViewById(R.id.editName);
        buttonPrice = findViewById(R.id.buttonPrice);
        buttonPrice.setOnTouchListener(onPriceTouch);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeReader != null) {
            barcodeReader.release();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeReader != null) try {
            barcodeReader.claim();
        } catch (AidcException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (barcodeReader != null) {
            barcodeReader.removeBarcodeListener(this);
            barcodeReader.close();
        }

        if (aidcManager != null) {
            aidcManager.close();
        }
    }

    private final View.OnTouchListener onPriceTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
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
            try {
                barcodeReader.aim(switch_on);
                barcodeReader.light(switch_on);
                barcodeReader.decode(switch_on);
            } catch (AidcException e) {
                e.printStackTrace();
            }
            // Do not "eat" events
            return false;
        }
    };

    private final AidcManager.CreatedCallback manager = new AidcManager.CreatedCallback() {
        @Override
        public void onCreated(AidcManager aidcManager) {
            BarcodeActivity.this.aidcManager = aidcManager;
            BarcodeActivity.this.barcodeReader = connectReader(aidcManager);
            if (barcodeReader != null) barcodeReader.addBarcodeListener(BarcodeActivity.this);
        }

        private BarcodeReader connectReader(AidcManager aidcManager) {
            try {
                final BarcodeReader reader = aidcManager.createBarcodeReader();
                reader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                        BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);

                // Optional: scanner continuous mode
                //reader.setProperty(BarcodeReader.PROPERTY_TRIGGER_SCAN_MODE,
                //        BarcodeReader.TRIGGER_SCAN_MODE_CONTINUOUS);

                final Map<String, Object> properties = new HashMap<String, Object>();

                properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
                //properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, false);
                properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
                properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
                properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
                properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false);
                properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
                properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
                properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_DECODER_TIMEOUT, 400);

                // Apply the settings
                reader.setProperties(properties);

                // Activate scanner
                reader.claim();
                return reader;
            } catch (AidcException e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    private final Runnable barcodesetter = new Runnable() {
        @Override
        public void run() {
            editBarcode.setText(editBartext);

            final PagingList<line> lines = new PagingList<line>(
                    dbd.lines().findBarCode(editBartext));

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

    private final Runnable barcodeerror = new Runnable() {
        @Override
        public void run() {
            buttonPrice.playSoundEffect(SoundEffectConstants.CLICK);
        }
    };

    @Override
    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {
        editBartext = barcodeReadEvent.getBarcodeData();
        runOnUiThread(barcodesetter);
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        runOnUiThread(barcodeerror);
    }
}
