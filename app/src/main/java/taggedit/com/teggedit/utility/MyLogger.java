package taggedit.com.teggedit.utility;

import android.util.Log;

/**
 * Created by Shweta on 1/7/17.
 */

public class MyLogger {
    private static String TAG = "TeggedIt";
    private static final boolean LOG_ENABLE = true;

    private MyLogger() {
    }

    public static void v(final Object object, final String message) {
        if (LOG_ENABLE) {
            Log.v(TAG, object.getClass().getSimpleName() + "::" + message);
        }
    }

    public static void i(final Object object, final String message) {
        if (LOG_ENABLE) {
            Log.i(TAG, object.getClass().getSimpleName() + "::" + message);
        }
    }

    public static void d(final Object object, final String message) {
        if (LOG_ENABLE) {
            Log.d(TAG, object.getClass().getSimpleName() + "::" + message);
        }
    }

    public static void w(final Object object, final String message) {
        if (LOG_ENABLE) {
            Log.w(TAG, object.getClass().getSimpleName() + "::" + message);
        }
    }

    public static void e(final Object object, final String message) {
        if (LOG_ENABLE) {
            Log.e(TAG, object.getClass().getSimpleName() + "::" + message);
        }
    }

    public static void e(Object object, final String message,
                         final Throwable throwable) {
        if (LOG_ENABLE) {
            Log.e(TAG, object.getClass().getSimpleName() + "::" + message,
                    throwable);
        }
    }
}
