package taggedit.com.teggedit.datastore;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Shweta on 1/7/17.
 */

public class TaggedItPreference {

    private SharedPreferences sharedPreferences;
    private static TaggedItPreference taggedItPreference;
    public final String PREFS_NAME = "TaggedItPrefsFile";
    private final String FRESH_INSTALL = "freshInstall";

    private TaggedItPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static TaggedItPreference getTaggedItPreference(Context context) {
        if (taggedItPreference == null) {
            taggedItPreference = new TaggedItPreference(context);
        }
        return taggedItPreference;
    }

    public void setFreshInstall(boolean b) {
        sharedPreferences.edit().putBoolean(FRESH_INSTALL, b).commit();
    }

    public Boolean getFreshInstall() {
        return sharedPreferences.getBoolean(FRESH_INSTALL, true);
    }
}
