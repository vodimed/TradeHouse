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
    protected void actionCreate() {
        // Inapplicable
    }

    @Override
    protected void actionEdit() {
        final document document = (document) listInvoices.getSelectedItem();

        if (document != null) {
            final Intent intent = new Intent(InventoriesActivity.this, InventoryEditActivity.class);
            intent.putExtra(document.class.getName(), document);
            startActivity(intent);
        }
    }
}
