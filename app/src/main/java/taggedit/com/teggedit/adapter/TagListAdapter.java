package taggedit.com.teggedit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.listener.TagAdapterEventListener;
import taggedit.com.teggedit.model.Tag;
import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/12/17.
 */

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<Tag> tagsList;
    private TagAdapterEventListener listener;
//    private Context context;

    public TagListAdapter(TagAdapterEventListener tagAdapterEventListener) {
        tagsList = new ArrayList<>();
        this.listener = tagAdapterEventListener;
    }

    public void setTagsList(ArrayList<Tag> tags) {
        MyLogger.d(this, "setTagsList is called");

        if (tagsList != null) {
            tagsList.clear();
        }
        tagsList.addAll(tags);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tags_item_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tag tag = tagsList.get(position);
        holder.tagName.setText(tag.getName());
        holder.deleteTag.setTag(position);
        holder.deleteTag.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        if (tagsList == null) {
            return 0;
        } else {
            return tagsList.size();
        }
    }

    public Tag getItemAtPostion(int position) {
        if (tagsList != null)
            return tagsList.get(position);
        else
            return null;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        if (listener == null) {
            removeTag(position);
        } else {
            listener.deleteTag(position);
        }
    }

    public void removeTag(int position) {
        tagsList.remove(position);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tagName;
        public ImageView deleteTag;

        public ViewHolder(View itemView) {
            super(itemView);
            tagName = (TextView) itemView.findViewById(R.id.tag_name);
            deleteTag = (ImageView) itemView.findViewById(R.id.delete_tag);
        }
    }

    public void addTag(Tag tag) {
        if (tagsList == null) {
            tagsList = new ArrayList<>();
        }
        tagsList.add(tag);
        notifyDataSetChanged();
    }

    public boolean isAlreadyExist(Tag tag) {
        if (tagsList != null && tagsList.size() > 0) {
            if (tagsList.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Tag> getTagsList() {
        if (tagsList != null) {
            MyLogger.d(this, "adapter taglist called");
            return tagsList;
        } else {
            return null;
        }
    }
}
