package com.expertek.tradehouse;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.expertek.tradehouse.dictionaries.entity.Objects;
import com.expertek.tradehouse.exchange.ServiceConnector;
import com.expertek.tradehouse.exchange.ServiceInterface;
import com.expertek.tradehouse.exchange.ServiceReceiver;
import com.expertek.tradehouse.tradehouse.Dictionaries;
import com.expertek.tradehouse.tradehouse.Documents;
import com.expertek.tradehouse.tradehouse.TradeHouseService;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.UnsupportedPropertyException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements BarcodeReader.BarcodeListener {
    private AidcManager mAidcManager = null;
    private BarcodeReader mBarcodeReader = null;
    private ListView barcodeList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve activity graphical controls
        barcodeList = findViewById(R.id.listViewBarcodeData);

        // Get new AidcManager
        AidcManager.create(this.getApplicationContext(), new CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                mAidcManager = aidcManager;
                if (createBarcodeReaderConnection()) {
                    mBarcodeReader.addBarcodeListener(MainActivity.this);
                }
            }
        });

        tradehouse.registerService(false);

        //TODO: database
        Objects ob = new Objects();
        ob.Name = "bbb";
        ob.obj_code = 2;
        ob.obj_type = 2;
        ob.uid = 2;
        MainApplication.dbc().tObjects().insertAll(ob);
        List<Objects> ls = MainApplication.dbc().tObjects().getAll();
        int sz = ls.size();

        //MainApplication.dbct().isOpen()
    }

    private boolean createBarcodeReaderConnection() {
        try {
            mBarcodeReader = mAidcManager.createBarcodeReader();
            mBarcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                    BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);

            // Optional: scanner continuous mode
            mBarcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_SCAN_MODE,
                    BarcodeReader.TRIGGER_SCAN_MODE_CONTINUOUS);

            final Map<String, Object> properties = new HashMap<String, Object>();
            // Set Symbologies On/Off
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
            // Set Max Code 39 barcode length
            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
            // Turn on center decoding
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
            // Enable bad read response
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true);
            // Sets time period for decoder timeout in any mode
            properties.put(BarcodeReader.PROPERTY_DECODER_TIMEOUT, 400);

            // Apply the settings
            mBarcodeReader.setProperties(properties);
            // Activate scanner
            mBarcodeReader.claim();
            return true;
        } catch (InvalidScannerNameException e) {
                e.printStackTrace();
                Toast.makeText(this, "Invalid Scanner Name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (UnsupportedPropertyException e) {
            e.printStackTrace();
            Toast.makeText(this, "Control mode not set.", Toast.LENGTH_SHORT).show();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
            Toast.makeText(this, "Scanner unavailable.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent event) {
        // update UI to reflect the data
        final List<String> list = new ArrayList<String>();
        list.add("Barcode data: " + event.getBarcodeData());
        list.add("Character Set: " + event.getCharset());
        list.add("Code ID: " + event.getCodeId());
        list.add("AIM ID: " + event.getAimId());
        list.add("Timestamp: " + event.getTimestamp());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                barcodeList.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_list_item_1, list));
            }
        });
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBarcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            mBarcodeReader.release();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBarcodeReader != null) try {
            mBarcodeReader.claim();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
            Toast.makeText(this, "Scanner unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBarcodeReader != null) {
            // unregister barcode event listener
            mBarcodeReader.removeBarcodeListener(this);
            mBarcodeReader.close();
        }
        if (mAidcManager != null) {
            mAidcManager.close();
        }
    }

    private final ServiceConnector tradehouse = new ServiceConnector(this, TradeHouseService.class) {
        @Override
        public void onServiceResult(@NonNull JobInfo work, Bundle result) {
            Log.d("RESULT", result.toString());
        }

        @Override
        public void onServiceException(@NonNull JobInfo work, @NonNull Throwable e) {
            Log.d("EXCEPTION", e.toString());
        }
    };

    private final ServiceReceiver scheduler = new ServiceReceiver(new ServiceInterface.Receiver() {
        @Override
        public void onServiceResult(@NonNull ServiceInterface.JobInfo work, @Nullable Bundle result) {
            Log.d("SCHEDULER RESULT", "onReceiveResult");
        }

        @Override
        public void onServiceException(@NonNull ServiceInterface.JobInfo work, @NonNull Throwable e) {
            Log.d("SCHEDULER EXCEPTION", "onReceiveResult");
        }
    });

    public void doBindService(View view) {

        /*
        final JobScheduler mScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        final ComponentName service = new ComponentName(this, TradeHouseService.class);
        for (int jobID = 1; jobID < 0; jobID++) {

            final JobInfo.Builder jobInfo = new JobInfo.Builder(jobID, service).setOverrideDeadline(0);
            final Bundle params = new Bundle();

            final Intent intent = new ServiceInterface.JobInfo(jobID, Settings.class, scheduler.receiver()).asIntent(this, TradeHouseService.class);
            intent.replaceExtras(params);

            jobInfo.setClipData(new ClipData("", new String[0], new ClipData.Item(intent)), 0);
            int result = mScheduler.enqueue(jobInfo.build(), new JobWorkItem(new Intent()));
            //Log.d("JOB", "plan(" + jobID + ") = " + result);
        }
        final List<JobInfo> jobList = mScheduler.getAllPendingJobs();
        //mScheduler.cancelAll();
        final List<JobInfo> jobListClear = mScheduler.getAllPendingJobs();
        Log.d("JOB", "planned = " + jobList.size() + " & " + jobListClear.size());
        */

        tradehouse.enqueue(new ServiceInterface.JobInfo(1, Dictionaries.class, tradehouse.receiver()), null);
    }

    public void doUnbindService(View view) {
        //tradehouse.unregisterService();
        tradehouse.enqueue(new ServiceInterface.JobInfo(2, Documents.class, tradehouse.receiver()), null);
    }
}
