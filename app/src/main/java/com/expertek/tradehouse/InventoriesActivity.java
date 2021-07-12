package com.expertek.tradehouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
        final Intent intent = new Intent(InventoriesActivity.this, InventoryEditActivity.class);
        startActivity(intent);
    }

    @Override
    protected void actionDelete() {

    }

    @Override
    protected void actionSend() {

    }
}
