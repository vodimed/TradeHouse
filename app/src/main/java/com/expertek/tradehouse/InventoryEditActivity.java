package com.expertek.tradehouse;

import android.content.Intent;

import com.expertek.tradehouse.documents.entity.line;

public class InventoryEditActivity extends InvoiceEditActivity {
    @Override
    protected void actionAdd(int position) {
        final line line = new line();
        line.DocName = document.DocName;

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
}