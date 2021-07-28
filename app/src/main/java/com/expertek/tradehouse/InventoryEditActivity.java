package com.expertek.tradehouse;

import android.content.Intent;

import com.expertek.tradehouse.documents.entity.line;

public class InventoryEditActivity extends InvoiceEditActivity {
    @Override
    protected void actionAdd() {
        final line line = new line();
        line.DocName = document.DocName;

        final Intent intent = new Intent(InventoryEditActivity.this, InventoryActivity.class);
        intent.putExtra(line.class.getName(), line);
        startActivity(intent);
    }

    @Override
    protected void actionEdit() {
        final line line = (line) listInvoice.getSelectedItem();

        if (line != null) {
            final Intent intent = new Intent(InventoryEditActivity.this, InventoryActivity.class);
            intent.putExtra(line.class.getName(), line);
            startActivity(intent);
        }
    }
}