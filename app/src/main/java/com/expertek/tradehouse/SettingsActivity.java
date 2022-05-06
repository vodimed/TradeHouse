package com.expertek.tradehouse;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.common.extensions.database.AdapterInterface;
import com.common.extensions.database.AdapterTemplate;
import com.common.extensions.database.PagingList;
import com.expertek.tradehouse.components.Logger;
import com.expertek.tradehouse.components.MainSettings;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.entity.Obj;
import com.expertek.tradehouse.dictionaries.entity.User;
import com.expertek.tradehouse.tradehouse.ConnectionReceiver;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SettingsActivity extends Activity {
    private final DbDictionaries dbc = Application.dictionaries.db();
    private final TabController tabcontrol = new TabController(3);
    private final SeekController seekcontrol = new SeekController();
    private final ConnectionHandler conhandler = new ConnectionHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        tabcontrol.addTab(this, R.id.toggleGeneral, R.id.tabGeneral);
        tabcontrol.addTab(this, R.id.toggleOptions, R.id.tabOptions);
        tabcontrol.addTab(this, R.id.toggleTimeouts, R.id.tabTimeouts);

        seekcontrol.register(this, R.id.seekLines, R.id.textLines, MainSettings.LinesPerPage);
        seekcontrol.register(this, R.id.seekConnectDelay, R.id.textConnectDelay, MainSettings.ConnectionTimeout / 1000);
        seekcontrol.register(this, R.id.seekCheckDelay, R.id.textCheckDelay, MainSettings.CheckTimeout / 1000);
        seekcontrol.register(this, R.id.seekLoadDelay, R.id.textLoadDelay, MainSettings.LoadTimeout / 1000);
        seekcontrol.register(this, R.id.seekSendDelay, R.id.textSendDelay, MainSettings.SendTimeout / 1000);

        final TextView label = findViewById(R.id.labelSerial);
        label.setText(String.format("%s v%s", label.getText(), Application.getVersion()));

        final TextView serial = findViewById(R.id.editSerial);
        serial.setText(MainSettings.SerialNumber);

        final ObjectAdapter objectAdapter = new ObjectAdapter(this, android.R.layout.simple_list_item_activated_1);
        objectAdapter.setDataSet(new PagingList<Obj>(dbc.objects().load()));
        final Spinner spinObject = findViewById(R.id.spinObject);
        spinObject.setAdapter(objectAdapter);
        spinObject.setSelection(objectAdapter.findItem(MainSettings.TradeHouseObjType, MainSettings.TradeHouseObjCode));

        final UserAdapter userAdapter = new UserAdapter(this, android.R.layout.simple_list_item_activated_1);
        userAdapter.setDataSet(new PagingList<User>(dbc.users().load()));
        final Spinner spinUser = findViewById(R.id.spinUser);
        spinUser.setAdapter(userAdapter);
        spinUser.setSelection(userAdapter.findItem(MainSettings.TradeHouseUserId));

        conhandler.editAddress = findViewById(R.id.editAddress);
        conhandler.editAddress.setText(String.format(Locale.getDefault(), "%s:%d",
                MainSettings.TradeHouseAddress, MainSettings.TradeHousePort));
        conhandler.editAddress.addTextChangedListener(conhandler.onAddressChanged);

        conhandler.checkTethering = findViewById(R.id.checkTethering);
        conhandler.checkTethering.setChecked(MainSettings.Tethering);
        conhandler.checkTethering.setOnCheckedChangeListener(conhandler.onTetheringChanged);
        conhandler.onTetheringChanged.onCheckedChanged(null, conhandler.checkTethering.isChecked());

        final Switch offline = findViewById(R.id.checkOffline);
        offline.setChecked(MainSettings.WorkOffline);

        final Switch loadinv = findViewById(R.id.checkLoadInvents);
        loadinv.setChecked(MainSettings.LoadInvents);

        final Switch checkmarks = findViewById(R.id.checkMarks);
        checkmarks.setChecked(MainSettings.CheckMarks);

        final TextView prefixes = findViewById(R.id.editPrefixes);
        prefixes.setText(setToString(MainSettings.BarcodePrefixes));

        final Button buttonConnect = findViewById(R.id.buttonConnect);
        buttonConnect.setOnClickListener(conhandler.onConnectClick);
    }

    @Override
    protected void onResume() {
        super.onResume();
        conhandler.onConnectClick.onResume();
    }

    @Override
    protected void onDestroy() {
        final SeekBar lines = findViewById(R.id.seekLines);
        MainSettings.LinesPerPage = lines.getProgress();

        final SeekBar connect = findViewById(R.id.seekConnectDelay);
        MainSettings.ConnectionTimeout = connect.getProgress() * 1000;

        final SeekBar check = findViewById(R.id.seekCheckDelay);
        MainSettings.CheckTimeout = check.getProgress() * 1000;

        final SeekBar load = findViewById(R.id.seekLoadDelay);
        MainSettings.LoadTimeout = load.getProgress() * 1000;

        final SeekBar send = findViewById(R.id.seekSendDelay);
        MainSettings.SendTimeout = send.getProgress() * 1000;

        final Spinner spinObject = findViewById(R.id.spinObject);
        final Obj object = (Obj) spinObject.getSelectedItem();
        MainSettings.TradeHouseObjType = object.obj_type;
        MainSettings.TradeHouseObjCode = object.obj_code;

        final Spinner spinUser = findViewById(R.id.spinUser);
        final User user = (User) spinUser.getSelectedItem();
        MainSettings.TradeHouseUserId = user.userID;
        MainSettings.TradeHouseUserName = user.userName;

        MainSettings.Tethering = conhandler.checkTethering.isChecked();

        final String[] addr = conhandler.editAddress.getText().toString().split(":", 2);
        if (!conhandler.checkTethering.isChecked()) MainSettings.TradeHouseAddress = addr[0];
        MainSettings.TradeHousePort = getPort(addr);

        final Switch offline = findViewById(R.id.checkOffline);
        MainSettings.WorkOffline = offline.isChecked();

        final Switch loadinv = findViewById(R.id.checkLoadInvents);
        MainSettings.LoadInvents = loadinv.isChecked();

        final Switch checkmarks = findViewById(R.id.checkMarks);
        MainSettings.CheckMarks = checkmarks.isChecked();

        final TextView prefixes = findViewById(R.id.editPrefixes);
        MainSettings.BarcodePrefixes = setFromString(prefixes.getText().toString());

        MainSettings.savePreferences();
        super.onDestroy();
    }

    private static class TabController implements View.OnClickListener {
        private final ToggleButton[] toggle;
        private final LinearLayout[] tab;
        private int count = 0;

        TabController(int size) {
            toggle = new ToggleButton[size];
            tab = new LinearLayout[size];
        }

        public void addTab(Activity context, @IdRes int toggle, @IdRes int tab) {
            this.tab[count] = context.findViewById(tab);
            this.toggle[count] = context.findViewById(toggle);
            this.toggle[count].setOnClickListener(this);
            count++;
        }

        @Override
        public void onClick(View v) {
            for (int i = 0; i < count; i++) {
                final boolean thistab = (toggle[i].equals(v));
                toggle[i].setChecked(thistab);
                tab[i].setVisibility(thistab ? View.VISIBLE : View.GONE);
            }
        }
    }

    private static class SeekController implements SeekBar.OnSeekBarChangeListener {
        public TextView register(Activity context, @IdRes int seek, @IdRes int text, int progress) {
            final SeekBar seekBar = context.findViewById(seek);
            seekBar.setTag(context.findViewById(text));
            seekBar.setOnSeekBarChangeListener(this);

            if (seekBar.getProgress() != progress) {
                seekBar.setProgress(progress);
            } else {
                onProgressChanged(seekBar, progress, false);
            }
            return getMonitor(seekBar);
        }

        private TextView getMonitor(SeekBar seekBar) {
            return (TextView) seekBar.getTag();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            final TextView monitor = getMonitor(seekBar);
            if (monitor != null) monitor.setText(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Do Nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Do Nothing
        }
    }

    private static Set<String> setFromString(String value) {
        final String src = value.replaceAll("[^\\d;]", "");
        return Arrays.stream(src.split(";")).collect(Collectors.<String>toSet());
    }

    private static String setToString(Set<String> set) {
        return set.stream().collect(Collectors.joining(";"));
    }

    private static int getPort(String[] addr) {
        if (addr.length < 2) {
            return 80;
        } else try {
            return Integer.parseInt(addr[1].replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            return 80;
        }
    }

    private class ConnectionHandler {
        private int defaultTextColor = Color.TRANSPARENT;
        protected TextView editAddress = null;
        protected CheckBox checkTethering = null;

        private final TextWatcher onAddressChanged = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (defaultTextColor != Color.TRANSPARENT) editAddress.setTextColor(defaultTextColor);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        private final CompoundButton.OnCheckedChangeListener onTetheringChanged =
                new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final String address = (isChecked ? ConnectionReceiver.getConnectedIp() :
                        MainSettings.TradeHouseAddress);
                conhandler.editAddress.setText(String.format(Locale.getDefault(), "%s:%d",
                        address, getPort(editAddress.getText().toString().split(":", 2))));
                editAddress.setEnabled(!isChecked);
            }
        };

        private final OnCheckClickListener onConnectClick = new OnCheckClickListener();
        private class OnCheckClickListener implements View.OnClickListener {
            private boolean onResumeMonitor = false;

            public void onResume() {
                if (onResumeMonitor) {
                    onResumeMonitor = false;
                    onTetheringChanged.onCheckedChanged(null, checkTethering.isChecked());
                    checkConnection();
                }
            }

            @Override
            public void onClick(View v) {
                // FLAG: Run checkConnection() after onResume() = false
                onResumeMonitor = false;

                // Check modem (tethering) USB cable connection
                if (checkTethering.isChecked()) {
                    ConnectionReceiver.Status status = ConnectionReceiver.
                            connect(Application.app());
                    switch (status) {
                        case connected:
                            break;
                        case unavailable:
                            final CheckConnection none = new CheckConnection(null, 0, 0);
                            none.setResult(false);
                            return;
                        case launched:
                            onResumeMonitor = true;
                            return;
                    }
                }

                // Test connection to given ip address
                checkConnection();
            }

            private void checkConnection() {
                final SeekBar seekCheckDelay = findViewById(R.id.seekCheckDelay);
                final String[] addr = editAddress.getText().toString().split(":", 2);
                final Thread process = new Thread(new CheckConnection(addr[0], getPort(addr),
                        seekCheckDelay.getProgress() * 1000));
                process.start();
            }

            private class CheckConnection implements Runnable {
                private final Button buttonCheck;
                private final String addr;
                private final int port;
                private final int timeout;
                private boolean success = false;

                public CheckConnection(String addr, int port, int timeout) {
                    buttonCheck = findViewById(R.id.buttonConnect);
                    buttonCheck.setEnabled(false);
                    this.addr = addr;
                    this.port = port;
                    this.timeout = timeout;
                }

                public void setResult(boolean success) {
                    this.success = success;
                    runOnUiThread(checkResult);
                }

                private HttpURLConnection createConnection() {
                    final HttpURLConnection connection;
                    try {
                        connection = (HttpURLConnection)
                                new URL("http", addr, port, "").openConnection();
                        connection.setConnectTimeout(timeout);
                        return connection;
                    } catch (IOException e) {
                        Logger.e(e);
                        return null;
                    }
                }

                @Override
                public void run() {
                    final HttpURLConnection connection = createConnection();
                    if (connection != null) try {
                        connection.connect();
                        connection.disconnect();
                        setResult(true);
                    } catch (IOException e) {
                        setResult(false);
                    }
                }

                protected final Runnable checkResult = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (defaultTextColor != Color.TRANSPARENT)
                                defaultTextColor = editAddress.getCurrentTextColor();
                            editAddress.setTextColor(success ? Color.GREEN : Color.RED);
                            buttonCheck.setEnabled(true);
                        } catch (Exception e) {
                            // Form may already be closed (Do Nothing)
                        }
                    }
                };
            }
        }
    }

    /**
     * Spinner data Adapter: list of Objects
     */
    protected static class ObjectAdapter extends AdapterTemplate<Obj> {
        public ObjectAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(true);
        }

        @SafeVarargs
        public ObjectAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setHasStableIds(true);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterInterface.Holder holder, int position) {
            final TextView text1 = holder.getView().findViewById(android.R.id.text1);
            text1.setText(getItem(position).Name);
        }

        @Override
        public Obj getItem(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof List<?>) {
                return (Obj) ((List<?>) dataset).get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            if (position < 0 || position >= getCount()) return INVALID_ROW_ID;
            final Obj item = getItem(position);
            if (item == null) return INVALID_ROW_ID;
            return item.obj_type.hashCode() * 31 + item.obj_code;
        }

        public int findItem(String obj_type, int obj_code) {
            final Object dataset = getDataSet();
            if (dataset instanceof List<?>) {
                for (int i = 0; i < ((List<?>) dataset).size(); i++) {
                    final Obj object = (Obj) ((List<?>) dataset).get(i);
                    if ((object.obj_code == obj_code) && object.obj_type.equals(obj_type))
                        return i;
                }
            }
            return INVALID_POSITION;
        }
    }

    /**
     * Spinner data Adapter: list of Users
     */
    protected static class UserAdapter extends AdapterTemplate<User> {
        public UserAdapter(Context context, @NonNull int... layout) {
            super(context, layout);
            setHasStableIds(true);
        }

        @SafeVarargs
        public UserAdapter(Context context, @NonNull Class<? extends View>... layer) {
            super(context, layer);
            setHasStableIds(true);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterInterface.Holder holder, int position) {
            final TextView text1 = holder.getView().findViewById(android.R.id.text1);
            text1.setText(getItem(position).userName);
        }

        @Override
        public User getItem(int position) {
            final Object dataset = getDataSet();
            if (dataset instanceof List<?>) {
                return (User) ((List<?>) dataset).get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            if (position < 0 || position >= getCount()) return INVALID_ROW_ID;
            final User item = getItem(position);
            if (item == null) return INVALID_ROW_ID;
            return item.userID.hashCode();
        }

        public int findItem(String userId) {
            final Object dataset = getDataSet();
            if (dataset instanceof List<?>) {
                for (int i = 0; i < ((List<?>) dataset).size(); i++) {
                    final User user = (User) ((List<?>) dataset).get(i);
                    if (user.userID.equals(userId)) return i;
                }
            }
            return INVALID_POSITION;
        }
    }
}
