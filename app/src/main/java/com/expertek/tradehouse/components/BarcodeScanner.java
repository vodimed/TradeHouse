package com.expertek.tradehouse.components;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.view.SoundEffectConstants;

import com.honeywell.aidc.AidcException;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;

import java.util.HashMap;
import java.util.Map;

public abstract class BarcodeScanner implements BarcodeReader.BarcodeListener {
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private AidcManager aidcManager = null;
    private AudioManager audioManager = null;
    private BarcodeReader barcodeReader = null;
    private BarcodeReadEvent barcodeEvent = null;
    private BarcodeFailureEvent failureEvent = null;

    public void Initialize(Context context) {
        AidcManager.create(context, manager);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void Pause() {
        if (barcodeReader != null) {
            barcodeReader.release();
        }
    }

    public void Resume() {
        if (barcodeReader != null) try {
            barcodeReader.claim();
        } catch (AidcException e) {
            Logger.w(e);
        }
    }

    public void Finalize() {
        if (barcodeReader != null) {
            barcodeReader.removeBarcodeListener(this);
            barcodeReader.close();
            barcodeReader = null;
        }

        if (aidcManager != null) {
            aidcManager.close();
            aidcManager = null;
        }
    }

    public void Enabled(boolean state) {
        try {
            barcodeReader.aim(state);
            barcodeReader.light(state);
            barcodeReader.decode(state);
        } catch (AidcException e) {
            Logger.e(e);
        }
    }

    public void mockBarcodeDetect(String scanned) {
        onBarcodeDetect(scanned);
    }

    protected abstract void onBarcodeDetect(String scanned);

    protected void onBarcodeFailure() {
        audioManager.playSoundEffect(SoundEffectConstants.CLICK);
    }

    @Override
    public final void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {
        barcodeEvent = barcodeReadEvent;
        runOnUiThread(callback);
    }

    @Override
    public final void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
        failureEvent = barcodeFailureEvent;
        runOnUiThread(failure);
    }

    public final void runOnUiThread(Runnable action) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            handler.post(action);
        } else {
            action.run();
        }
    }

    private final Runnable callback = new Runnable() {
        @Override
        public void run() {
            onBarcodeDetect(barcodeEvent.getBarcodeData());
        }
    };

    private final Runnable failure = new Runnable() {
        @Override
        public void run() {
            onBarcodeFailure();
        }
    };

    private final AidcManager.CreatedCallback manager = new AidcManager.CreatedCallback() {
        @Override
        public void onCreated(AidcManager aidcManager) {
            BarcodeScanner.this.aidcManager = aidcManager;
            BarcodeScanner.this.barcodeReader = connectReader(aidcManager);
            if (barcodeReader != null) barcodeReader.addBarcodeListener(BarcodeScanner.this);
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
                Logger.e(e);
                return null;
            }
        }
    };
}
