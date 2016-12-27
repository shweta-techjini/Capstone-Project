package taggedit.com.teggedit.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.fragement.PhotoAddTagFragment;
import taggedit.com.teggedit.fragement.PhotoTextTagFragment;

/**
 * Created by Shweta on 1/10/17.
 */

public class PhotoTagViewPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    private int[] title = {R.string.tags, R.string.photo_text};

    public PhotoTagViewPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("PhotoTagViewPager", "position is " + position);
        switch (position) {
            case 0:
                PhotoAddTagFragment photoAddTagFragment = PhotoAddTagFragment.newInstance();
                return photoAddTagFragment;
            case 1:
                return PhotoTextTagFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(title[position]);
    }
}
