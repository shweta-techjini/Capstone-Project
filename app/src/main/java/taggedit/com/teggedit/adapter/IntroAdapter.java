package taggedit.com.teggedit.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.databinding.IntroPageLayoutBinding;

/**
 * Created by Shweta on 1/6/17.
 */

public class IntroAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;

    public IntroAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    private int[] title = {R.string.intro_one, R.string.intro_two,
            R.string.intro_three};

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        IntroPageLayoutBinding introPageLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.intro_page_layout, container, true);
        introPageLayoutBinding.description.setText(title[position]);
        introPageLayoutBinding.titleImage.setImageResource(R.drawable.icon);
        return introPageLayoutBinding.getRoot();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }
}
