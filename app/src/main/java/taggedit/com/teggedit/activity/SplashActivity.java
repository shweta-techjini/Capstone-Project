package taggedit.com.teggedit.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.datastore.TaggedItPreference;

/**
 * Created by Shweta on 1/6/17.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_ACTIVITY_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TaggedItPreference.getTaggedItPreference(getApplicationContext()).getFreshInstall()) {
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
            }
        }, SPLASH_ACTIVITY_TIME);
    }
}
