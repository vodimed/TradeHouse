package com.expertek.tradehouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.expertek.tradehouse.documents.entity.document;

public class InventoriesActivity extends InvoicesActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buttonCreate.setVisibility(View.GONE);
    }

    @Override
    protected void actionCreate(int position) {
        // Inapplicable
    }

    @Override
    protected void actionEdit(int position) {
        final Intent intent = new Intent(InventoriesActivity.this, InventoryEditActivity.class);
        intent.putExtra(document.class.getName(), adapterDocument.getItem(position));
        startActivityForResult(intent, InventoryEditActivity.REQUEST_EDIT_DOCUMENT);
    }
}
