package com.expertek.tradehouse;

import android.content.Intent;
import android.os.Bundle;

import com.common.extensions.Dialogue;
import com.expertek.tradehouse.documents.entity.line;
import com.honeywell.aidc.BarcodeReadEvent;

public class InventoryEditActivity extends InvoiceEditActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initiialize Barcode Reader
        detector.Initialize(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        detector.Pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        detector.Resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.Finalize();
    }

    @Override
    protected void actionAdd(int position) {
        final line line = new line();
        line.DocName = document.DocName;
        line.LineID = (int) firstLineId + lines.size();
        line.Pos = lines.size() + 1;

        final Intent intent = new Intent(InventoryEditActivity.this, InventoryActivity.class);
        intent.putExtra(line.class.getName(), line);
        startActivityForResult(intent, InventoryActivity.REQUEST_ADD_POSITION);
    }

    @Override
    protected void actionEdit(int position) {
        final Intent intent = new Intent(InventoryEditActivity.this, InventoryActivity.class);
        intent.putExtra(line.class.getName(), adapterLine.getItem(position));
        startActivityForResult(intent, InventoryActivity.REQUEST_EDIT_POSITION);
    }

    private final BarcodeDetector detector = new BarcodeDetector() {
        @Override
        protected void onBarcodeDetect(BarcodeReadEvent event) {
            Dialogue.Question(InventoryEditActivity.this, R.string.add, null);
        }
    };
}