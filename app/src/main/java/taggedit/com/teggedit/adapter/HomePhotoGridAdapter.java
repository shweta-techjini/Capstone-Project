package taggedit.com.teggedit.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import java.util.ArrayList;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.model.TagPhoto;
import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/24/17.
 */

public class HomePhotoGridAdapter extends BaseAdapter implements Filterable {
    private ArrayList<TagPhoto> itemList;
    private Context context;
    private BitmapFactory.Options options;
    private TagFilter tagFilter;
    private ArrayList<TagPhoto> searchList;

    public HomePhotoGridAdapter(Context context) {
        this.itemList = new ArrayList<>();
        this.searchList = new ArrayList<>();
        this.context = context;
        options = new BitmapFactory.Options();
        options.inSampleSize = 4;
    }

    @Override
    public int getCount() {
        if (searchList == null) {
            return 0;
        } else {
            return searchList.size();
        }
    }

    @Override
    public TagPhoto getItem(int i) {
        if (searchList == null) {
            return null;
        } else {
            return searchList.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_item, viewGroup, false);
            view.setTag(new GridViewHolder(view));
        }

        GridViewHolder gridViewHolder = (GridViewHolder) view.getTag();
        String filePath = searchList.get(position).getPhotoPath();
        MyLogger.d(this, "photo path from database  is :: " + filePath);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        gridViewHolder.pic.setImageBitmap(bitmap);

        return view;
    }

    @Override
    public Filter getFilter() {
        if (tagFilter == null) {
            tagFilter = new TagFilter();
        }
        return tagFilter;
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {

        public ImageView pic;

        public GridViewHolder(View itemView) {
            super(itemView);
            pic = (ImageView) itemView.findViewById(R.id.photo);
//            itemView.setOnClickListener(this);
        }
    }

    public void addTagPhoto(TagPhoto tagPhoto) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        itemList.add(tagPhoto);
        notifyDataSetChanged();
    }

    public void setItemList(ArrayList<TagPhoto> tagPhotos) {
        if (searchList == null) {
            searchList = new ArrayList<>();
        } else {
            searchList.clear();
        }
        searchList.addAll(tagPhotos);
        itemList = searchList;
        notifyDataSetChanged();
    }

    private class TagFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if (charSequence != null && charSequence.length() > 0) {
                ArrayList<TagPhoto> filterList = new ArrayList();
                for (int i = 0; i < itemList.size(); i++) {
                    String tags = itemList.get(i).getPhotoTagsName();
                    MyLogger.d(this, "charSequence :: " + charSequence);
                    MyLogger.d(this, "tags of photos:: " + tags);
                    if (tags.contains(charSequence)) {
                        MyLogger.d(this, "tags contains search text:: ");
                        filterList.add(itemList.get(i));
                    }
                }
                filterResults.count = filterList.size();
                filterResults.values = filterList;
            } else {
                filterResults.count = itemList.size();
                filterResults.values = itemList;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            searchList = (ArrayList<TagPhoto>) filterResults.values;
            notifyDataSetChanged();
        }
    }

}
