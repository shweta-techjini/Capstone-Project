package taggedit.com.teggedit.model;

import java.util.ArrayList;

/**
 * Created by Shweta on 1/19/17.
 */

public class TagPhoto {

    private Long autoIncrementId;
    private String photoPath;
    private ArrayList<Tag> listOfTags;
    private String photoTagIds;
    private String photoTagsName;

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public ArrayList<Tag> getListOfTags() {
        return listOfTags;
    }

    public void setListOfTags(ArrayList<Tag> listOfTags) {
        this.listOfTags = listOfTags;
    }

    public String getNameOfTags() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < listOfTags.size(); i++) {
            stringBuilder.append(listOfTags.get(i).getName());
            if (i != (listOfTags.size() - 1))
                stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

    public String getPhotoTagIds() {
        return photoTagIds;
    }

    public void setPhotoTagIds(String photoTagIds) {
        this.photoTagIds = photoTagIds;
    }

    public Long getAutoIncrementId() {
        return autoIncrementId;
    }

    public void setAutoIncrementId(Long autoIncrementId) {
        this.autoIncrementId = autoIncrementId;
    }

    public String getPhotoTagsName() {
        return photoTagsName;
    }

    public void setPhotoTagsName(String photoTagsName) {
        this.photoTagsName = photoTagsName;
    }
}
