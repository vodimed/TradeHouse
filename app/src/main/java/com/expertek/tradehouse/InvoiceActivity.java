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
    private Button buttonOk = null;
    private Button buttonCancel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_activity);

        // Retrieve Activity parameters
        line = (line) getIntent().getSerializableExtra(line.class.getName());

        final TextView labelName = findViewById(R.id.labelName);
        labelName.setText(line.GoodsName);

        final EditText editPrice = findViewById(R.id.editPrice);
        editPrice.setText(String.valueOf(line.Price));

        final EditText editAmountDoc = findViewById(R.id.editAmountDoc);
        editAmountDoc.setText(String.valueOf(line.DocQnty));

        final EditText editAmountFact = findViewById(R.id.editAmountFact);
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
        final EditText editPrice = findViewById(R.id.editPrice);
        line.Price = Double.parseDouble(editPrice.getText().toString());

        final EditText editAmountDoc = findViewById(R.id.editAmountDoc);
        line.DocQnty = Double.parseDouble(editAmountDoc.getText().toString());

        final EditText editAmountFact = findViewById(R.id.editAmountFact);
        line.FactQnty = Double.parseDouble(editAmountFact.getText().toString());

        finish();
    }

    protected void actionCancel() {
        finish();
    }
}