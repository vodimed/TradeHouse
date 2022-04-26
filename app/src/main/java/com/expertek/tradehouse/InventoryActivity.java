package com.expertek.tradehouse;

import android.os.Bundle;
import android.view.View;

public class InventoryActivity extends InvoiceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.layoutPrice).setVisibility(View.GONE);
        findViewById(R.id.editAmountDoc).setEnabled(false);
    }
}