package models;

import java.util.Date;

public class Comment {
    private String uid;
    private String forumId;
    private String comment;
    private long timeStamp;
    private long likes;

    public Comment(){

    }

    public Comment(String uid, String forumId, String comment) {
        this.uid = uid;
        this.forumId = forumId;
        this.comment = comment;
        this.timeStamp = new Date().getTime();
        this.likes = 0;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }
}
