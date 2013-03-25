/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * MultiThreadIntentService is a base class for {@link Service}s that handle asynchronous requests
 * (expressed as {@link Intent}s) on demand. Clients send requests through
 * {@link android.content.Context#startService(Intent)} calls; the service is started as needed,
 * handles each Intent in turn using a worker thread, and stops itself when it runs out of work.
 * <p>
 * This "work queue processor" pattern is commonly used to offload tasks from an application's main
 * thread. The MultiThreadedIntentService class exists to simplify this pattern and take care of the
 * mechanics. To use it, extend MultiThreadedIntentService and implement
 * {@link #onHandleIntent(Intent)}. MultiThreadedIntentService will receive the Intents, launch a
 * worker thread, and stop the service as appropriate.
 * <p>
 * All requests are handled on multiple worker threads -- they may take as long as necessary (and
 * will not block the application's main loop). By default only one concurrent worker thread is
 * used. You can modify the number of current worker threads by overriding
 * {@link #getMaximumNumberOfThreads()}.
 * <p>
 * For obvious efficiency reasons, MultiThreadedIntentService won't stop itself as soon as all tasks
 * has been processed. It will only stop itself after a certain delay (about 30s). This optimization
 * prevents the system from creating new instances over and over again when tasks are sent.
 *
 * @author Foxykeep
 */
public abstract class MultiThreadedIntentService extends Service {
    
    private static final long STOP_SELF_DELAY = TimeUnit.SECONDS.toMillis(30L);

    private ExecutorService mThreadPool;
    private boolean mRedelivery;

    private ArrayList<Future<?>> mFutureList;

    private Handler mHandler;
    
    private final Runnable mStopSelfRunnable = new Runnable() {
        @Override
        public void run() {
            stopSelf();
        }
    };

    private final Runnable mWorkDoneRunnable = new Runnable() {
        @Override
        public void run() {
            if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
                throw new IllegalStateException(
                        "This runnable can only be called in the Main thread!");
            }

            final ArrayList<Future<?>> futureList = mFutureList;
            for (int i = 0; i < futureList.size(); i++) {
                if (futureList.get(i).isDone()) {
                    futureList.remove(i);
                    i--;
                }
            }

            if (futureList.isEmpty()) {
                mHandler.postDelayed(mStopSelfRunnable, STOP_SELF_DELAY);
            }
        }
    };

    /**
     * Sets intent redelivery preferences. Usually called from the constructor with your preferred
     * semantics.
     * <p>
     * If enabled is true, {@link #onStartCommand(Intent, int, int)} will return
     * {@link Service#START_REDELIVER_INTENT}, so if this process dies before
     * {@link #onHandleIntent(Intent)} returns, the process will be restarted and the intent
     * redelivered. If multiple Intents have been sent, only the most recent one is guaranteed to be
     * redelivered.
     * <p>
     * If enabled is false (the default), {@link #onStartCommand(Intent, int, int)} will return
     * {@link Service#START_NOT_STICKY}, and if the process dies, the Intent dies along with it.
     */
    public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        int maximumNumberOfThreads = getMaximumNumberOfThreads();
        if (maximumNumberOfThreads <= 0) {
            throw new IllegalArgumentException("Maximum number of threads must be " +
                    "strictly positive");
        }
        mThreadPool = Executors.newFixedThreadPool(maximumNumberOfThreads);
        mHandler = new Handler();
        mFutureList = new ArrayList<Future<?>>();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onStart(Intent intent, int startId) {
        mHandler.removeCallbacks(mStopSelfRunnable);
        mFutureList.add(mThreadPool.submit(new IntentRunnable(intent)));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThreadPool.shutdown();
    }

    /**
     * Unless you provide binding for your service, you don't need to implement this method, because
     * the default implementation returns null.
     *
     * @see android.app.Service#onBind
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Define the maximum number of concurrent worker threads used to execute the incoming Intents.
     * <p>
     * By default only one concurrent worker thread is used at the same time. Overrides this method
     * in subclasses to change this number.
     * <p>
     * This method is called once in the {@link #onCreate()}. Modifying the value returned after the
     * {@link #onCreate()} is called will have no effect.
     *
     * @return The maximum number of concurrent worker threads
     */
    protected int getMaximumNumberOfThreads() {
        return 1;
    }

    private class IntentRunnable implements Runnable {
        private final Intent mIntent;

        public IntentRunnable(Intent intent) {
            mIntent = intent;
        }

        public void run() {
            onHandleIntent(mIntent);
            mHandler.removeCallbacks(mWorkDoneRunnable);
            mHandler.post(mWorkDoneRunnable);
        }
    }

    /**
     * This method is invoked on the worker thread with a request to process. The processing happens
     * on a worker thread that runs independently from other application logic. When all requests
     * have been handled, the IntentService stops itself, so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link Context#startService(Intent)}.
     */
    abstract protected void onHandleIntent(Intent intent);
}
