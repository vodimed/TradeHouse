package com.expertek.tradehouse;

import android.app.Activity;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.IdRes;

import com.expertek.tradehouse.tradehouse.USBConnectReceiver;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SettingsActivity extends Activity {
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

        final TextView serial = findViewById(R.id.editSerial);
        serial.setText(MainSettings.SerialNumber);

        final TextView object = findViewById(R.id.editObject);
        object.setText(MainSettings.TradeHouseObject);

        if (MainSettings.Tethering)
            MainSettings.TradeHouseAddress = USBConnectReceiver.getConnectedIp();

        conhandler.editConnect = findViewById(R.id.editConnect);
        conhandler.editConnect.setText(String.format(Locale.getDefault(), "%s:%d",
                MainSettings.TradeHouseAddress, MainSettings.TradeHousePort));
        conhandler.editConnect.addTextChangedListener(conhandler.onConnectTextChanged);

        final CheckBox checkTethering = findViewById(R.id.checkTethering);
        checkTethering.setChecked(MainSettings.Tethering);
        checkTethering.setOnCheckedChangeListener(conhandler.onTetheringCheckedChange);
        conhandler.onTetheringCheckedChange.onCheckedChanged(checkTethering, checkTethering.isChecked());

        final Switch offline = findViewById(R.id.checkOffline);
        offline.setChecked(MainSettings.WorkOffline);

        final Switch loadinv = findViewById(R.id.checkLoadInvents);
        loadinv.setChecked(MainSettings.LoadInvents);

        final Switch checkmarks = findViewById(R.id.checkMarks);
        checkmarks.setChecked(MainSettings.CheckMarks);

        final TextView prefixes = findViewById(R.id.editPrefixes);
        prefixes.setText(setToString(MainSettings.BarcodePrefixes));

        final Button buttonCheck = findViewById(R.id.buttonCheck);
        buttonCheck.setOnClickListener(conhandler.onCheckClick);
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

        final TextView object = findViewById(R.id.editObject);
        MainSettings.TradeHouseObject = object.getText().toString();

        final String[] addr = conhandler.editConnect.getText().toString().split(":", 2);
        MainSettings.TradeHouseAddress = addr[0];
        MainSettings.TradeHousePort = getPort(addr);

        final CheckBox checkTethering = findViewById(R.id.checkTethering);
        MainSettings.Tethering = checkTethering.isChecked();

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
        private TextView editConnect = null;

        private final CompoundButton.OnCheckedChangeListener onTetheringCheckedChange =
                new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editConnect.setEnabled(!isChecked);
            }
        };

        private final TextWatcher onConnectTextChanged = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (defaultTextColor != Color.TRANSPARENT) editConnect.setTextColor(defaultTextColor);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        private final View.OnClickListener onCheckClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SeekBar seekCheckDelay = findViewById(R.id.seekCheckDelay);
                final String[] addr = editConnect.getText().toString().split(":", 2);
                final Thread process = new Thread(new CheckConnection(addr[0], getPort(addr),
                        seekCheckDelay.getProgress() * 1000));
                process.start();
            }

            class CheckConnection implements Runnable {
                private final Button buttonCheck;
                private HttpURLConnection connection;
                private boolean success = false;

                public CheckConnection(String addr, int port, int timeout) {
                    buttonCheck = findViewById(R.id.buttonCheck);
                    buttonCheck.setEnabled(false);

                    try {
                        connection = (HttpURLConnection) new URL("http", addr, port, "").openConnection();
                        connection.setConnectTimeout(timeout);
                    } catch (IOException e) {
                        connection = null;
                        e.printStackTrace();
                    }
                }

                @Override
                public void run() {
                    if (connection != null) try {
                        connection.connect();
                        connection.disconnect();
                        success = true;
                    } catch (IOException e) {
                        success = false;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (defaultTextColor != Color.TRANSPARENT)
                                defaultTextColor = editConnect.getCurrentTextColor();
                            editConnect.setTextColor(success ? Color.GREEN : Color.RED);
                            buttonCheck.setEnabled(true);
                        }
                    });
                }
            }
        };
    }
}
