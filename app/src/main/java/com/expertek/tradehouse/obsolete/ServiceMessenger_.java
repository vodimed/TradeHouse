package com.expertek.tradehouse.obsolete;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.Printer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public abstract class ServiceMessenger_ {
    public static final int MSG_CLEAR_QUEUE = -1;
    public static final int MSG_PRINT_QUEUE = -2;

    // Target we publish for clients to send messages to IncomingHandler.
    private final IncomingHandler mHandler = new IncomingHandler(new IncomingEvent());
    private final Messenger mMessenger = new Messenger(mHandler);

    protected IBinder getBinder() {
        return mMessenger.getBinder();
    }

    protected abstract boolean onIncomingEvent(int what, int arg1, int arg2, Object obj, Messenger replyTo);

    public void postInquiry(@NonNull Messenger service, int what, int arg1, int arg2, Object obj) throws RemoteException {
        final Message msg = Message.obtain(null, what, arg1, arg2, obj);
        msg.replyTo = mMessenger;
        service.send(msg);
    }

    public void clearLocalQueue(int arg1, int arg2) {
        mHandler.handleMessage(Message.obtain(null, MSG_CLEAR_QUEUE));
    }

    public void clearLocalQueue() {
        clearLocalQueue(0, 0);
    }

    /**
     * Handler of incoming messages from service.
     */
    private static class IncomingHandler extends Handler {
        public IncomingHandler(@Nullable Callback callback) {
            super(Looper.myLooper(), callback);
        }
    }

    private class IncomingEvent implements Handler.Callback {
        private int minQueue = Integer.MAX_VALUE;
        private int maxQueue = Integer.MIN_VALUE;

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what < minQueue && msg.what > 0) minQueue = msg.what;
            if (msg.what > maxQueue && msg.what > 0) maxQueue = msg.what;

            try {
                switch (msg.what) {
                    case MSG_CLEAR_QUEUE:
                        if (msg.arg1 == 0 && msg.arg2 == 0) {
                            mHandler.removeMessages(MSG_CLEAR_QUEUE);
                            mHandler.removeMessages(MSG_PRINT_QUEUE);
                        }

                        if (msg.arg1 == 0) msg.arg1 = minQueue;
                        if (msg.arg2 == 0) msg.arg1 = maxQueue;

                        for (int what = msg.arg1; what <= msg.arg2; what++) {
                            mHandler.removeMessages(what);
                        }
                        return true;

                    case MSG_PRINT_QUEUE:
                        // Client side
                        if (msg.obj != null) {
                            final ArrayList<String> queue = ((Bundle)msg.obj).getStringArrayList("");
                            return onIncomingEvent(msg.what, msg.arg1, msg.arg2, queue, msg.replyTo);
                        }

                        // Service side
                        final ArrayList<String> queue = new ArrayList<String>();
                        mHandler.dump(new Printer() {
                            @Override
                            public void println(String x) {
                                queue.add(x);
                            }
                        }, "");

                        final Bundle data = new Bundle();
                        data.putStringArrayList("", queue);
                        postInquiry(msg.replyTo, msg.what, queue.size(), 0, data);
                        return true;

                   default:
                        return onIncomingEvent(msg.what, msg.arg1, msg.arg2, msg.obj, msg.replyTo);
                }
            } catch (Exception e) {
                Log.e(ServiceMessenger_.class.getSimpleName(), "Service communication error", e);
                return false;
            }
        }
    }
}
