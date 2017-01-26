package taggedit.com.teggedit.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import taggedit.com.teggedit.BuildConfig;
import taggedit.com.teggedit.R;

/**
 * Created by Shweta on 1/7/17.
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String HIDE_SKIP = "hide_skip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        CardView tag_card = ButterKnife.findById(this, R.id.tag_card);
        tag_card.setOnClickListener(this);
        findViewById(R.id.tag_card).setOnClickListener(this);
        findViewById(R.id.feedback_card).setOnClickListener(this);
        findViewById(R.id.intro_card).setOnClickListener(this);
        ((TextView) findViewById(R.id.app_version_code)).setText(BuildConfig.VERSION_NAME);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tag_card:
                // Open tags activity
                startActivity(new Intent(this, ListTagsActivity.class));
                break;
            case R.id.feedback_card:
                // Send feedback
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts(getString(R.string.mailto), getString(R.string.email_id),
                                null));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));
                break;
            case R.id.intro_card:
                // Intro screen
                Intent intent = new Intent(this, IntroActivity.class);
                intent.putExtra(HIDE_SKIP, true);
                startActivity(intent);
                break;
        }
    }
}
