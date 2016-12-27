package taggedit.com.teggedit.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import taggedit.com.teggedit.BuildConfig;
import taggedit.com.teggedit.R;
import taggedit.com.teggedit.databinding.ActivitySettingsBinding;

/**
 * Created by Shweta on 1/7/17.
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String HIDE_SKIP = "hide_skip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingsBinding activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        activitySettingsBinding.tagsList.setOnClickListener(this);
        activitySettingsBinding.feedback.setOnClickListener(this);
        activitySettingsBinding.intro.setOnClickListener(this);
        activitySettingsBinding.appVersionCode.setText(BuildConfig.VERSION_NAME);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tags_list:
                // Open tags activity
                startActivity(new Intent(this, ListTagsActivity.class));
                break;
            case R.id.feedback:
                // Send feedback
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", "shweta@techjini.com",
                                null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;
            case R.id.intro:
                // Intro screen
                Intent intent = new Intent(this, IntroActivity.class);
                intent.putExtra(HIDE_SKIP, true);
                startActivity(intent);
                break;
        }
    }
}
