package com.expertek.tradehouse;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.expertek.tradehouse.documents.entity.line;

public class InvoiceActivity extends Activity {
    private line line = null;
    private TextView labelName = null;
    protected EditText editPrice = null;
    private EditText editAmountDoc = null;
    private EditText editAmountFact = null;
    private Button buttonOk = null;
    private Button buttonCancel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_activity);

        // Retrieve Activity parameters
        line = (line) getIntent().getSerializableExtra(line.class.getName());

        labelName = findViewById(R.id.labelName);
        labelName.setText(line.GoodsName);

        editPrice = findViewById(R.id.editPrice);
        editPrice.setText(String.valueOf(line.Price));

        editAmountDoc = findViewById(R.id.editAmountDoc);
        editAmountDoc.setText(String.valueOf(line.DocQnty));

        editAmountFact = findViewById(R.id.editAmountFact);
        editAmountFact.setText(String.valueOf(line.FactQnty));

        buttonOk = findViewById(R.id.buttonOk);
        buttonCancel = findViewById(R.id.buttonCancel);

        buttonOk.setOnClickListener(onClickAction);
        buttonCancel.setOnClickListener(onClickAction);
    }

    private final View.OnClickListener onClickAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buttonOk.equals(v)) {
                actionOk();
            } else if (buttonCancel.equals(v)) {
                actionCancel();
            }
        }
    };

    protected void actionOk() {
        line.Price = Double.parseDouble(editPrice.getText().toString());
        line.DocQnty = Double.parseDouble(editAmountDoc.getText().toString());
        line.FactQnty = Double.parseDouble(editAmountFact.getText().toString());
        finish();
    }

    protected void actionCancel() {
        finish();
    }
}