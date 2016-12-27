package taggedit.com.teggedit.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/24/17.
 */

public class TagWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        MyLogger.d(this, "onGetViewFactory");
        return new TagWidgetRemoteViewFactory(getApplicationContext());
    }
}
