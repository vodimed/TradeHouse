package com.expertek.tradehouse.obsolete;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobServiceEngine;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.expertek.tradehouse.HandleException;
import com.expertek.tradehouse.MainApplication;
import com.expertek.tradehouse.MainSettings;
import com.expertek.tradehouse.R;
import com.expertek.tradehouse.exchange.TradeHouseActivity;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TradeHouseService_ extends Service {
    public static final int MSG_ACTION_A = 1;

    private final Socket mTradeHouse = new Socket();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TradeHouseService_.class.getSimpleName(), "TradeHouseJService OPER!");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Handler of incoming messages from clients.
     */
    private final ServiceMessenger_ eventProcessor = new ServiceMessenger_() {
        @Override
        protected boolean onIncomingEvent(int what, int arg1, int arg2, Object obj, Messenger replyTo) {
            // Check if settings of connection parameters were changed
            final InetSocketAddress remote = new InetSocketAddress(
                    MainSettings.ThreadHouseAddress, MainSettings.ThreadHousePort);

            // Connect to remote socket of TradeHouse server
            if (!remote.equals(mTradeHouse.getRemoteSocketAddress())) try {
                mTradeHouse.close();
                mTradeHouse.connect(remote, MainSettings.ConnectionTimeout);
            } catch (IOException e) {
                clearLocalQueue();
                HandleException.accept(e, R.string.errTradeHouseServiceConnection);
            }

            // Execute requested task
            if (!mTradeHouse.isConnected())
                return false;
            /*
            os.flush() after os.writeBytes(dataString + "\n");
            */

            try {
                switch (what) {
                    case MSG_ACTION_A:
                        postInquiry(replyTo, MSG_ACTION_A, 123, 0, null);
                        return true;
                    default:
                        return false;
                }
            } catch (RemoteException e) {
                HandleException.accept(e, R.string.errTradeHouseServiceConnection);
                return false;
            }
        }
    };

    @Override
    public void onCreate() {
        Log.d(TradeHouseService_.class.getSimpleName(), "TradeHouseJService CREATE!");
        super.onCreate();
        startForeground(R.string.service_tradehouse, MainApplication.createNotification(
                TradeHouseActivity.class,
                R.string.service_tradehouse,
                R.string.msgTradeHouseServiceNotification));
    }

    @Override
    public void onDestroy() {
        Log.d(TradeHouseService_.class.getSimpleName(), "TradeHouseJService DESTROY!");
        eventProcessor.clearLocalQueue();
        stopForeground(false);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction() != null) {
            return eventProcessor.getBinder();
        } else {
            return new JobScheduleFacility(this).getBinder();
        }
    }

    private static class JobScheduleFacility extends JobServiceEngine {
        public JobScheduleFacility(Service service) {
            super(service);
        }

        @Override
        public boolean onStartJob(JobParameters params) {
            return false;
        }

        @Override
        public boolean onStopJob(JobParameters params) {
            return false;
        }
    }
}