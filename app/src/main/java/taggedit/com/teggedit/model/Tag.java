package taggedit.com.teggedit.model;

import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/12/17.
 */

public class Tag {

    private long id;
    private String name;

    public Tag() {

    }

    public Tag(long tagId, String tagName) {
        this.id = tagId;
        this.name = tagName;
    }

    public Tag(String tagName) {
        this.name = tagName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name.toLowerCase();
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    @Override
    public boolean equals(Object obj) {
        if (this.name.equalsIgnoreCase(((Tag) obj).getName())) {
            MyLogger.d(this, "both names are equal");
            return true;
        } else {
            MyLogger.d(this, "both names are not equal");
            return false;
        }
    }

    @Override
    public String toString() {
        return "{" + id + ", " + name + "}";
    }
}
