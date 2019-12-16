package models;

import java.util.Date;

public class Vote {
    private String uid;
    private String forumId;
    private long timeStamp;

    public Vote() {

    }

    public Vote(String uid, String forumId) {
        this.uid = uid;
        this.forumId = forumId;
        this.timeStamp = new Date().getTime();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
