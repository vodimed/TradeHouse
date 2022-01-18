package com.common.extensions.exchange;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.common.extensions.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceQueue implements Runnable {
    private static final int processors_limit = 1; // Max number of working threads
    private static final List<String> empty_list = empty_list();
    private final AtomicInteger processors_count = new AtomicInteger(0);
    private final ConcurrentLinkedQueue<Controller> waiting = new ConcurrentLinkedQueue<Controller>();
    private final ConcurrentLinkedQueue<Controller> working = new ConcurrentLinkedQueue<Controller>();
    private final ConcurrentLinkedQueue<Thread> processors = new ConcurrentLinkedQueue<Thread>();

    public void enqueue(@NonNull ServiceInterface.JobInfo work, @Nullable Bundle params,
                        @NonNull ServiceInterface.Receiver receiver) throws ReflectiveOperationException
    {
        final Controller controller = new Controller(work, params, receiver);
        synchronized (waiting) { // for: cancelAll()
            waiting.offer(controller);
        }
        Thread.yield(); // for: destroy_processor()
        create_processor();
    }

    private void create_processor() {
        if (processors_count.incrementAndGet() > processors_limit) {
            processors_count.decrementAndGet();
        } else {
            final Thread processor = new Thread(this);
            processor.start();
            processors.offer(processor);
        }
    }

    private void destroy_processor(Thread processor) {
        processors_count.decrementAndGet();
        processors.remove(processor);
        // garbage_queue();

        // Kill another process, but not itself
        if (!processor.equals(Thread.currentThread())) {
            processor.interrupt();
        }
    }

    private void garbage_queue() {
        synchronized (working) { // for: concurrent garbage_queue()
            for (;;) {
                final Controller controller = working.peek();
                if ((controller == null) || !controller.isTerminated()) break;
                working.poll();
            }
        }
    }

    @Override
    public void run() {
        for (;;) {
            final Controller controller = waiting.poll();
            if (controller == null) break;
            working.offer(controller);
            controller.run();
        }
        destroy_processor(Thread.currentThread());
    }

    public List<ServiceInterface.JobInfo> listAwaiting() {
        final ArrayList<ServiceInterface.JobInfo> result = new ArrayList<ServiceInterface.JobInfo>(waiting.size());

        for (Controller controller : waiting) {
            result.add(controller.jobInfo());
        }
        return result;
    }

    public List<ServiceInterface.JobInfo> listExecuting() {
        final ArrayList<ServiceInterface.JobInfo> result = new ArrayList<ServiceInterface.JobInfo>(working.size());

        for (Controller controller : working) {
            result.add(controller.jobInfo());
        }
        return result;
    }

    public List<String> getProgress(@NonNull ServiceInterface.JobInfo work, int since) {
        for (Controller controller : working) {
            if (work.equals(controller.jobInfo())) {
                return controller.getProgress(since);
            }
        }
        return empty_list;
    }

    private static List<String> empty_list() {
        final List<String> result = new ArrayList<String>(1);
        result.add("Wait");
        return result;
    }

    private void cancel(@NonNull ServiceInterface.JobInfo work, ConcurrentLinkedQueue<Controller> queue) {
        for (Controller controller : queue) {
            if (work.equals(controller.jobInfo())) {
                if (queue.remove(controller)) {
                    final Thread processor = controller.getProcessor();

                    if (!controller.cancel()) {
                        destroy_processor(processor);
                        create_processor();
                    }
                }
            }
        }
    }

    public void cancel(@NonNull ServiceInterface.JobInfo work) {
        cancel(work, waiting);
        cancel(work, working);
    }

    public void cancelAll() {
        synchronized (waiting) { // for: enqueue()
            waiting.clear();

            for (;;) {
                final Controller controller = working.poll();
                if (controller == null) break;
                controller.cancel();
            }

            for (;;) {
                final Thread processor = processors.poll();
                if (processor == null) break;
                destroy_processor(processor);
            }

            working.clear();
        }
    }

    /**
     * Task execution Controller
     */
    private static class Controller implements Runnable {
        private enum Step {none, start, init, exec, send, fine, done}
        private static final ExceptionHandler handler = new ExceptionHandler();
        private final ServiceInterface.ServiceTask task;
        private final ServiceInterface.JobInfo work;
        private final ServiceInterface.Receiver receiver;
        private Thread processor = null;
        private final Bundle params;
        private Bundle result = null;
        private Exception except = null;
        private volatile Step step = Step.none;
        private volatile boolean cancelled = false;

        public Controller(@NonNull ServiceInterface.JobInfo work, @Nullable Bundle params,
                          @NonNull ServiceInterface.Receiver receiver) throws ReflectiveOperationException
        {
            this.task = work.getTask().newInstance();
            this.work = work;
            this.receiver = receiver;
            this.params = params;
        }

        public ServiceInterface.JobInfo jobInfo() {
            return work;
        }

        public Thread getProcessor() {
            return processor;
        }

        public List<String> getProgress(int since) {
            return task.getProgress(since);
        }

        public boolean isTerminated() {
            return (step == Step.done);
        }

        public boolean cancel() {
            // Exit if this task is just scheduled but not run
            cancelled = true;
            if (step == Step.none) return true;

            // Cancel the task if it is already running
            try {
                task.setProgress("Cancel");
                task.onCancel();
                for (int i = 0; i < 10; i++) {
                    if (step == Step.done) break;
                    Thread.sleep(78);
                }
                return (step == Step.done);
            } catch (UnsupportedOperationException e) {
                return false;
            } catch (Exception e) {
                Logger.e(e);
                return false;
            }
        }

        @Override
        public void run() {
            step = Step.start;
            if (cancelled) return;
            processor = handler.configure(this);

            while (step != Step.done) try {
                step = Step.values()[step.ordinal() + 1];

                switch (step) {
                    case init:
                        task.setProgress("Initialize");
                        task.onCreate(params);
                        break;
                    case exec:
                        if (!cancelled) try {
                            task.setProgress("Execute");
                            result = task.call();
                        } catch (InterruptedException e) {
                            throw e;
                        } catch (Exception e) {
                            except = e;
                        }
                        break;
                    case send:
                        if (!cancelled) {
                            if (result != null) receiver.onJobResult(work, result);
                            if (except != null) receiver.onJobException(work, except);
                        }
                        break;
                    case fine:
                        task.setProgress("Finalize");
                        task.onDestroy();
                        break;
                }
            } catch (InterruptedException e) {
                task.setProgress("Interrupt");
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Logger.e(e);
            }
        }

        /**
         * Perform exceptions in the working thread
         */
        private static class ExceptionHandler implements Thread.UncaughtExceptionHandler {
            private static final ThreadLocal<Controller> local = new ThreadLocal<Controller>();

            public Thread configure(Controller object) {
                final Thread processor = Thread.currentThread();
                processor.setUncaughtExceptionHandler(this);
                local.set(object);
                return processor;
            }

            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                final Controller object = local.get();

                if (object != null) {
                    object.step = Step.values()[Step.fine.ordinal() - 1];
                    object.run();
                }
            }
        }
    }
}
