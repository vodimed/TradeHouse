package com.common.extensions.exchange;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ServiceQueue implements Runnable {
    // TODO: run in main thread const setting
    // TODO: run create/destroy in main thread const setting

    private final ConcurrentLinkedQueue<Controller> queue = new ConcurrentLinkedQueue<Controller>();
    private volatile Controller controller = null; // ThreadLocal does not support access from another thread
    private volatile Thread processor = null;
    private volatile boolean cancelled = false;

    public synchronized int enqueue(@NonNull Controller item) {
        cancelled = false;
        queue.offer(item);
        if (processor == null) create();
        return item.hashCode();
    }

    public Controller[] listAwaiting() {
        return queue.toArray(new Controller[0]);
    }

    public Controller[] listExecuting() {
        final Controller[] resut = new Controller[0];
        return resut;
    }

    public void cancel(int code) {
        if (thiscode() != code) return;
        boolean cancellable;

        try {
            cancellable = controller.cancel();
            Thread.sleep(100);
        } catch (Throwable throwable) {
            cancellable = false;
        }

        if (thiscode() != code) return;
        // TODO: cancelable
        processor.interrupt(); // Kill all
        create();
    }

    public void cancelAll() {
        cancelled = true;
        queue.clear();
        Thread.yield(); // Assignment in run(): controller = queue.poll();
        cancel(thiscode());
    }

    private void create() {
        processor = new Thread(this);
        processor.setUncaughtExceptionHandler(handler);
        processor.start(); // TODO: join()
    }

    private int thiscode() {
        try {
            return controller.hashCode();
        } catch (Throwable throwable) {
            return 0;
        }
    }

    @Override
    public synchronized void run() {
        for (int i = 0; i < 2; i++) {
            while (!cancelled) {
                controller = queue.poll();
                if (controller == null) break;
                controller.run();
            }
            controller = null;
            processor = null;
        }
    }

    private static final Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(@NonNull Thread thread, @NonNull Throwable e) {
            // controller.run(); TODO: ThreadLocal
            // controller = null; TODO: ThreadLocal
        }
    };

    protected static class Controller implements Runnable {
        private final ServiceInterface.Task task;
        private final ServiceInterface.JobInfo work;
        private final ServiceInterface.Receiver receiver;
        private final Bundle params;
        private final Bundle result = new Bundle();
        private boolean provided = false;
        private int stage = 0;

        public Controller() {
            this.task = null;
            this.work = null;
            this.receiver = null;
            this.params = null;
        }

        public Controller(@NonNull ServiceInterface.JobInfo work, @Nullable Bundle params,
                          @NonNull ServiceInterface.Receiver receiver) throws ReflectiveOperationException
        {
            this.task = work.getTask().newInstance();
            this.work = work;
            this.receiver = receiver;
            this.params = params;
        }

        public boolean cancel() throws Throwable {
            return task.onCancel();
        }

        @Override
        public void run() {
            while (stage < 3) try {
                switch (stage) {
                    case 0:
                        stage++;
                        task.onCreate(params, result);
                        provided = task.call();
                    case 1:
                        stage++;
                        task.onDestroy();
                    case 2:
                        stage++;
                        if (provided) receiver.onServiceResult(work, result);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Throwable e) {
                // TODO: ThreadDeath
                result.putSerializable(ServiceInterface.THROWABLE, e);
                provided = true;
            }
        }
    }
}
