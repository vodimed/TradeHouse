package com.expertek.tradehouse;

import android.os.Bundle;
import android.view.View;

public class InventoryActivity extends InvoiceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editPrice.setVisibility(View.GONE);
    }
}