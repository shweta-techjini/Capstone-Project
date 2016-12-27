package taggedit.com.teggedit.fragement;


import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.activity.PhotoTagsActivity;
import taggedit.com.teggedit.adapter.TagListAdapter;
import taggedit.com.teggedit.database.PhotoTagsContract;
import taggedit.com.teggedit.database.TagsCurdHelper;
import taggedit.com.teggedit.databinding.PhotoTagTabLayoutBinding;
import taggedit.com.teggedit.listener.TagAdapterEventListener;
import taggedit.com.teggedit.model.Tag;
import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/7/17.
 * <p>
 * A simple {@link Fragment} subclass.
 */
public class PhotoAddTagFragment extends Fragment implements View.OnClickListener, TagAdapterEventListener, LoaderManager.LoaderCallbacks<Cursor> {

    private PhotoTagTabLayoutBinding photoTagTabLayoutBinding;
    private TagListAdapter tagListAdapter;
    private static final int ALL_TAGS_LOADER = 0;
    private HashMap<String, Long> databaseTags;
    private ArrayList<Tag> alreadyDefinedTags;
    private String[] autoCompleteEditText;
    private PhotoTagsActivity photoTagsActivity;

    public PhotoAddTagFragment() {
        // Required empty public constructor
    }

    public static PhotoAddTagFragment newInstance() {
        return new PhotoAddTagFragment();
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
        MyLogger.d(this, "onCreateView : ");
        photoTagTabLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.photo_tag_tab_layout, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        photoTagTabLayoutBinding.tagList.setLayoutManager(layoutManager);
        tagListAdapter = new TagListAdapter(null);
        setEmptyMessage();

        photoTagTabLayoutBinding.multiTagsAutoComplete.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        getLoaderManager().initLoader(ALL_TAGS_LOADER, null, this);

        photoTagTabLayoutBinding.tagList.setAdapter(tagListAdapter);
        photoTagTabLayoutBinding.tickMarkButton.setOnClickListener(this);

        if (savedInstanceState == null) {
            alreadyDefinedTags = photoTagsActivity.getPreTags();
            if (alreadyDefinedTags != null) {
                tagListAdapter.setTagsList(alreadyDefinedTags);
                setEmptyMessage();
            }
        } else {

        }

        return photoTagTabLayoutBinding.getRoot();
    }

    @Override
    public void onClick(View view) {
        String tagText = photoTagTabLayoutBinding.multiTagsAutoComplete.getText().toString();

        if (tagText != null && tagText.trim().length() > 0) {
            StringTokenizer stringTokenizer = new StringTokenizer(tagText, ",");
            if (stringTokenizer.countTokens() > 0) {
                while (stringTokenizer.hasMoreTokens()) {
                    String token = stringTokenizer.nextToken().trim().toLowerCase();
                    MyLogger.d(this, "token is :: " + token);
                    if (token.length() > 0) {
                        Tag tag = new Tag(token);
                        if (tagListAdapter.isAlreadyExist(tag)) {
                            // show toast that it is already exist in the list
                            Toast.makeText(getContext(), tag.getName() + " Tag already exist, please enter different tag", Toast.LENGTH_SHORT).show();
                        } else {
                            if (databaseTags.containsKey(tag.getName())) {
                                MyLogger.d(this, "already in database do not insert it");
                                tag.setId(databaseTags.get(tag.getName()));
                            } else {
                                long tagId = TagsCurdHelper.insertTag(tag, getContext());
                                MyLogger.d(this, "inserted tagId in database :: " + tagId);
                                tag.setId(tagId);
                            }
                            tagListAdapter.addTag(tag);
                            setEmptyMessage();
                        }
                    }
                }
            }
        }

        photoTagTabLayoutBinding.multiTagsAutoComplete.setText("");
    }

    private void setEmptyMessage() {
        if (tagListAdapter.getItemCount() == 0) {
            photoTagTabLayoutBinding.setEmptyTag(true);
        } else {
            photoTagTabLayoutBinding.setEmptyTag(false);
        }
    }

    public ArrayList<Tag> getTagsForPhoto() {
        if (tagListAdapter == null) {
            MyLogger.d(this, "getTagForPhotos called and adapter is null");
            return null;
        } else {
            MyLogger.d(this, "getTagForPhotos called and adapter is not null");
            return tagListAdapter.getTagsList();
        }
    }

    @Override
    public void deleteTag(int position) {
        tagListAdapter.removeTag(position);
        setEmptyMessage();
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri tagUri = PhotoTagsContract.TagsEntry.CONTENT_URI;
        MyLogger.d(this, "tags uri is :: " + tagUri);
        String sortOrder = PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME + " ASC";
        return new android.support.v4.content.CursorLoader(getContext(), tagUri, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (loader != null) {
            MyLogger.d(this, "cursor is not null ");
            databaseTags = new HashMap<>();
            while (data.moveToNext()) {
                Tag tag = new Tag();
                tag.setId(data.getInt(data.getColumnIndex(PhotoTagsContract.TagsEntry.COLUMN_TAG_ID)));
                tag.setName(data.getString(data.getColumnIndex(PhotoTagsContract.TagsEntry.COLUMN_TAG_NAME)));
                MyLogger.d(this, "tag is " + tag.toString());
                databaseTags.put(tag.getName(), tag.getId());
            }

            Set<String> keys = databaseTags.keySet();
            autoCompleteEditText = keys.toArray(new String[databaseTags.size()]);
            ArrayAdapter<String> tagsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, autoCompleteEditText);
            photoTagTabLayoutBinding.multiTagsAutoComplete.setAdapter(tagsAdapter);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("reload_pre_tags_loader", true);
    }
}
