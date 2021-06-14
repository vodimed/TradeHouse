package com.expertek.tradehouse;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.IdRes;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SettingsActivity extends Activity {
    private final TabController tabcontrol = new TabController(3);
    private final SeekController seekcontrol = new SeekController();

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

        final TextView connect = findViewById(R.id.editConnect);
        connect.setText(String.format(Locale.getDefault(), "%s:%d",
                MainSettings.TradeHouseAddress, MainSettings.TradeHousePort));

        final Switch offline = findViewById(R.id.checkOffline);
        offline.setChecked(MainSettings.WorkOffline);

        final Switch loadinv = findViewById(R.id.checkLoadInvents);
        loadinv.setChecked(MainSettings.LoadInvents);

        final Switch checkmarks = findViewById(R.id.checkMarks);
        checkmarks.setChecked(MainSettings.CheckMarks);

        final TextView prefixes = findViewById(R.id.editPrefixes);
        prefixes.setText(setToString(MainSettings.BarcodePrefixes));
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

        final TextView connection = findViewById(R.id.editConnect);
        final String[] addr = connection.getText().toString().split(":", 2);
        MainSettings.TradeHouseAddress = addr[0];
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
        private final ToggleButton toggle[];
        private final LinearLayout tab[];
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
        return Arrays.stream(value.split(";")).collect(Collectors.<String>toSet());
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
}
