package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObservable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.common.extensions.database.AdapterTemplate;
import com.common.extensions.exchange.ServiceConnector;
import com.common.extensions.exchange.ServiceInterface;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.tradehouse.TradeHouseService;
import com.expertek.tradehouse.tradehouse.TradeHouseTask;
import com.expertek.tradehouse.tradehouse.Документы;
import com.expertek.tradehouse.tradehouse.Настройки;
import com.expertek.tradehouse.tradehouse.Словари;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DictionariesActivity extends Activity {
    private ResponseDataSet datasetResponse = new ResponseDataSet();
    private ResponseAdapter adapterResponse = null;
    private Button buttonSend = null;
    private Button buttonClose = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionaries_activity);

        tradehouse.registerService(false);

        adapterResponse = new ResponseAdapter(this, TextView.class);
        adapterResponse.setDataSet(datasetResponse);

        final ListView listResponse = findViewById(R.id.listResponse);
        listResponse.setChoiceMode(ListView.CHOICE_MODE_NONE);
        listResponse.setSelector(android.R.drawable.list_selector_background);
        listResponse.setAdapter(adapterResponse);

        buttonSend = findViewById(R.id.buttonSend);
        buttonClose = findViewById(R.id.buttonClose);

        buttonSend.setOnClickListener(onClickAction);
        buttonClose.setOnClickListener(onClickAction);

        // TODO: check if exchange is already active
        buttonSend.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        tradehouse.unregisterService();
        super.onDestroy();
    }

    private final View.OnClickListener onClickAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buttonSend.equals(v)) {
                actionSend();
            } else if (buttonClose.equals(v)) {
                actionClose();
            }
        }
    };

    protected void actionSend() {
        tradehouse.enqueue(new ServiceInterface.JobInfo(1, Настройки.class, tradehouse.receiver()), null);
        tradehouse.enqueue(new ServiceInterface.JobInfo(2, Словари.class, tradehouse.receiver()), null);
        tradehouse.enqueue(new ServiceInterface.JobInfo(3, Документы.class, tradehouse.receiver()), null);
        buttonSend.setEnabled(false);
    }

    @SuppressWarnings("unchecked")
    protected void actionReceive(@NonNull ServiceInterface.JobInfo work, Bundle result) {
        switch (work.getJobId()) {
            case 2:
                final File dictionaries = MainApplication.app().getDatabasePath(MainSettings.Dictionaries_db);
                MainApplication.replace_dictionaries_db_file(TradeHouseTask.temporary(dictionaries).getName(),
                        (Class<? extends DbDictionaries>) result.getSerializable(dictionaries.getName()));
                break;
            case 3:
                final File documents = MainApplication.app().getDatabasePath(MainSettings.Documents_db);
                MainApplication.replace_documents_db_file(TradeHouseTask.temporary(documents).getName(),
                        (Class<? extends DBDocuments>) result.getSerializable(documents.getName()));
                break;
        }
    }

    protected void actionClose() {
        finish();
    }

    /**
     * Service response data Adapter: list of messages
     */
    private static class ResponseAdapter extends AdapterTemplate<String> {
        public ResponseAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(true);
        }

        @SafeVarargs
        public ResponseAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setHasStableIds(true);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            final TextView text1 = (TextView) holder.getView();
            text1.setText(getItem(position));
        }

        @Override
        public String getItem(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof ResponseDataSet) {
                return ((ResponseDataSet) dataset).get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position; // constant list
        }

        @Override
        public int getCount() {
            final Object dataset = getDataSet();
            if (dataset instanceof ResponseDataSet) {
                return ((ResponseDataSet) dataset).size();
            } else {
                return 0;
            }
        }
    }

    /**
     * Service response dataset: list of messages
     */
    private static class ResponseDataSet extends DataSetObservable {
        private final ArrayList<String> content = new ArrayList<String>();
        
        public String get(int index) {
            return content.get(index);
        }

        public int size() {
            return content.size();
        }

        public void clear() {
            content.clear();
            notifyInvalidated();
        }

        public void append(String value) {
            content.add(value);
            notifyInvalidated();
        }

        public void appendAll(List<String> values) {
            content.addAll(values);
            notifyInvalidated();
        }
    }

    private final ServiceConnector tradehouse = new ServiceConnector(this, TradeHouseService.class) {
        @Override
        public void onJobResult(@NonNull ServiceInterface.JobInfo work, Bundle result) {
            datasetResponse.append(result.toString());
            actionReceive(work, result);
        }

        @Override
        public void onJobException(@NonNull ServiceInterface.JobInfo work, @NonNull Throwable e) {
            datasetResponse.append(String.valueOf(work.getJobId()) + ": " + e.toString());
        }
    };
}
