/**
 * 2012 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.util;

import com.foxykeep.datadroid.BuildConfig;

import android.util.Log;

/**
 * Centralized helper for logging information for DataDroid.
 * <p>
 * To modify the log level, use the following command :
 *
 * <pre>
 *   adb shell setprop log.tag.DataDroid &lt;LOGLEVEL>
 *   with LOGLEVEL being one of the following :
 *     VERBOSE, DEBUG, INFO, WARN or ERROR
 * </pre>
 * <p>
 * The default log level is DEBUG.
 * <p>
 * Also the logs are disabled in a release build by default. If you want them to be enabled, modify
 * the value of {@link #ENABLE_LOGS_IN_RELEASE}.
 *
 * @author Foxykeep
 */
public final class DataDroidLog {

    /**
     * Primary log tag for games output.
     */
    private static final String LOG_TAG = "DataDroid";

    /**
     * Whether the logs are enabled in release builds or not.
     */
    private static final boolean ENABLE_LOGS_IN_RELEASE = false;

    public static boolean canLog(int level) {
        return (ENABLE_LOGS_IN_RELEASE || BuildConfig.DEBUG) && Log.isLoggable(LOG_TAG, level);
    }

    public static void d(String tag, String message) {
        if (canLog(Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (canLog(Log.VERBOSE)) {
            Log.v(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (canLog(Log.INFO)) {
            Log.i(tag, message);
        }
    }

    public static void i(String tag, String message, Throwable thr) {
        if (canLog(Log.INFO)) {
            Log.i(tag, message, thr);
        }
    }

    public static void w(String tag, String message) {
        if (canLog(Log.WARN)) {
            Log.w(tag, message);
        }
    }

    public static void w(String tag, String message, Throwable thr) {
        if (canLog(Log.WARN)) {
            Log.w(tag, message, thr);
        }
    }

    public static void e(String tag, String message) {
        if (canLog(Log.ERROR)) {
            Log.e(tag, message);
        }
    }

    public static void e(String tag, String message, Throwable thr) {
        if (canLog(Log.ERROR)) {
            Log.e(tag, message, thr);
        }
    }
}
