package taggedit.com.teggedit;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by Shweta on 1/25/17.
 */

public class TeggetItApp extends Application {

    public static FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }
}
