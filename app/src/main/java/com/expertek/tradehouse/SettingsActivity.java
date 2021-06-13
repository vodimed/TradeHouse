package com.expertek.tradehouse;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.IdRes;

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

        seekcontrol.register(this, R.id.seekLines, R.id.textLines);
        seekcontrol.register(this, R.id.seekConnectDelay, R.id.textConnectDelay);
        seekcontrol.register(this, R.id.seekCheckDelay, R.id.textCheckDelay);
        seekcontrol.register(this, R.id.seekLoadDelay, R.id.textLoadDelay);
        seekcontrol.register(this, R.id.seekSendDelay, R.id.textSendDelay);
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
        public void register(Activity context, @IdRes int seek, @IdRes int text) {
            final SeekBar seekBar = context.findViewById(seek);
            seekBar.setTag(context.findViewById(text));
            onProgressChanged(seekBar, seekBar.getProgress(), false);
            seekBar.setOnSeekBarChangeListener(this);
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

        }
    }
}
