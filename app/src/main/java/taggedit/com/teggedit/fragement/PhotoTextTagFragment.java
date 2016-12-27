package taggedit.com.teggedit.fragement;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.List;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.activity.PhotoTagsActivity;
import taggedit.com.teggedit.databinding.PhotoTextTabLayoutBinding;
import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/7/17.
 * <p>
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoTextTagFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoTextTagFragment extends Fragment {

    private PhotoTagsActivity photoTagsActivity;
    private PhotoTextTabLayoutBinding photoTextTabLayoutBinding;

    public PhotoTextTagFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PhotoTextTagFragment.
     */
    public static PhotoTextTagFragment newInstance() {
        return new PhotoTextTagFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLogger.d(this, "onCreate");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        photoTagsActivity = (PhotoTagsActivity) getActivity();
        MyLogger.d(this, "onAttach");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        photoTextTabLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.photo_text_tab_layout, container, false);
        return photoTextTabLayoutBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        photoTextTabLayoutBinding.loading.setVisibility(View.VISIBLE);
        photoTextTabLayoutBinding.textInImage.setVisibility(View.INVISIBLE);
        new TextInImage().execute();
    }

    private class TextInImage extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            TextRecognizer textRecognizer = new TextRecognizer.Builder(photoTagsActivity.getApplicationContext()).build();
            Frame frame = new Frame.Builder().setBitmap(photoTagsActivity.getSelectedBitmap()).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < items.size(); ++i) {
                TextBlock item = items.valueAt(i);
                if (item != null && item.getValue() != null) {
                    stringBuilder.append(item.getValue());
                    item.getBoundingBox();
                    MyLogger.d(this, "TextBlock value " + item.getValue());
                    MyLogger.d(this, "TextBlock box " + item.getBoundingBox().toString());
                    readTextFromTextBlock(item);
                }
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            photoTextTabLayoutBinding.loading.setVisibility(View.GONE);
            photoTextTabLayoutBinding.textInImage.setVisibility(View.VISIBLE);
            if (result.length() > 0)
                photoTextTabLayoutBinding.textInImage.setText(result);
            else
                photoTextTabLayoutBinding.textInImage.setText(R.string.no_text_identify);
        }
    }

    private void readTextFromTextBlock(TextBlock mText) {
        // Break the text into multiple lines and draw each one according to its own bounding box.
        List<? extends Text> textComponents = mText.getComponents();
        for (Text currentText : textComponents) {
            float left = currentText.getBoundingBox().left;
            float bottom = currentText.getBoundingBox().bottom;
            MyLogger.d(this, "Each Text in TextBlock " + currentText.getValue());
            MyLogger.d(this, "Each Text in TextBlock left bound" + left);
            MyLogger.d(this, "Each Text in TextBlock bottom bound" + bottom);
//            canvas.drawText(currentText.getValue(), left, bottom, sTextPaint);
        }
    }
}
