package com.expertek.tradehouse;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import com.honeywell.aidc.AidcException;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;

import java.util.HashMap;
import java.util.Map;

public class BarcodeActivity extends Activity implements BarcodeReader.BarcodeListener {
    private AidcManager aidcManager = null;
    private BarcodeReader barcodeReader = null;
    private EditText editBarcode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_actvity);

        editBarcode = findViewById(R.id.editBarcode);

        // Initiialize Barcode Reader
        AidcManager.create(this, manager);
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
                reader.setProperty(BarcodeReader.PROPERTY_TRIGGER_SCAN_MODE,
                        BarcodeReader.TRIGGER_SCAN_MODE_CONTINUOUS);

                final Map<String, Object> properties = new HashMap<String, Object>();

                properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
                properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
                properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, false);
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

    @Override
    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {
        editBarcode.setText(barcodeReadEvent.getBarcodeData());
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
    }
}
