package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    private Button buttonInvoices = null;
    private Button buttonInventories = null;
    private Button buttonPricecontrol = null;
    private Button buttonDictionaries = null;
    private Button buttonSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // TODO: ListView: addHeaderView();
        // TODO: RecyclerView: addItemDecoration()

        buttonInvoices = findViewById(R.id.buttonInvoices);
        buttonInventories = findViewById(R.id.buttonInventories);
        buttonPricecontrol = findViewById(R.id.buttonPricecontrol);
        buttonDictionaries = findViewById(R.id.buttonDictionaries);
        buttonSettings = findViewById(R.id.buttonSettings);

        buttonInvoices.setOnClickListener(onClickMenuItem);
        buttonInventories.setOnClickListener(onClickMenuItem);
        buttonPricecontrol.setOnClickListener(onClickMenuItem);
        buttonDictionaries.setOnClickListener(onClickMenuItem);
        buttonSettings.setOnClickListener(onClickMenuItem);
    }

    private final View.OnClickListener onClickMenuItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Intent intent;

            if (buttonInvoices.equals(v)) {
                intent = new Intent(MainActivity.this, SelectionActivity.class);
                intent.putExtra("document_filters", SelectionActivity.INVOICES);
            } else if (buttonInventories.equals(v)) {
                intent = new Intent(MainActivity.this, SelectionActivity.class);
                intent.putExtra("document_filters", SelectionActivity.INVENTORIES);
            } else if (buttonPricecontrol.equals(v)) {
                intent = new Intent(MainActivity.this, BarcodeActivity.class);
            } else if (buttonDictionaries.equals(v)) {
                intent = new Intent(MainActivity.this, DictionariesActivity.class);
            } else if (buttonSettings.equals(v)) {
                intent = new Intent(MainActivity.this, SettingsActivity.class);
            } else {
                intent = null;
            }

            startActivity(intent);
        }
    };
}
