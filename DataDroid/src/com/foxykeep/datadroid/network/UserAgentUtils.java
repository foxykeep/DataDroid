/**
 * 2012 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.text.TextUtils;

import java.util.Locale;

/**
 * @author Foxykeep
 *
 * Utility class to generate and cache a User-Agent header for HTTP requests.
 */
public final class UserAgentUtils {

    private static String sUserAgent;

    private UserAgentUtils() {
        // No public constructor
    }

    /**
     * Get the User-Agent with the following syntax:
     * <p>
     * Mozilla/5.0 (Linux; U; Android {Build.VERSION.RELEASE}; {locale.toString()}[; {Build.MODEL}]
     * [; Build/{Build.ID}]) {getPackageName()}/{getVersionCode()}
     *
     * @param context The context to use to generate the User-Agent.
     * @return The User-Agent.
     */
    public synchronized static String get(Context context) {
        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }
        if (sUserAgent == null) {
            sUserAgent = generate(context);
        }
        return sUserAgent;
    }

    private static String generate(Context context) {
        StringBuilder sb = new StringBuilder();

        sb.append("Mozilla/5.0 (Linux; U; Android");
        sb.append(Build.VERSION.RELEASE);
        sb.append("; ");
        sb.append(Locale.getDefault().toString());

        String model = Build.MODEL;
        if (!TextUtils.isEmpty(model)) {
            sb.append("; ");
            sb.append(model);
        }

        String buildId = Build.ID;
        if (!TextUtils.isEmpty(buildId)) {
            sb.append("; Build/");
            sb.append(buildId);
        }

        sb.append(") ");

        int versionCode = 0;
        String packageName = context.getPackageName();
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // Keep the versionCode 0 as default.
        }

        sb.append(packageName);
        sb.append("/");
        sb.append(versionCode);

        return sb.toString();
    }

}
