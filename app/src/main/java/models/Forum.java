package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Forum implements Serializable {

    public static final int ANYONE = 1;
    public static final int NEED_APPROVAL = 2;

    public final static String FORUMS = "forums";
    public final static String FORUM_IMAGES = "forumImages";

    public final static String HEADER_TEXT = "text";
    public final static String HEADER_IMAGE = "image";
    public final static String HEADER_LOCATION = "location";
    public static final String ABOUT_LIST = "forum_abouts";
    public static final String BOOKMARKS = "bookmarks";

    private String id;
    private String uid;
    private String header;
    private List<String> about;
    private int privacy;
    private long timeStamp;
    private ArrayList<Content> contents;

    public Forum() {
    }

    public Forum(String uid, String header, ArrayList<Content> contents, List<String> about, int privacy) {
        this.uid = uid;
        this.header = header;
        this.contents = contents;
        this.about = about;
        this.privacy = privacy;
        this.timeStamp = new Date().getTime();
    }

    public ArrayList<Content> getContents() {
        return contents;
    }

    public void setContents(ArrayList<Content> contents) {
        this.contents = contents;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<String> getAbout() {
        return about;
    }

    public void setAbout(List<String> about) {
        this.about = about;
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
