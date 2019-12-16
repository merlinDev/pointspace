package models;

public class ForumView {
    private String uid;
    private String forumId;

    public ForumView(){

    }

    public ForumView(String uid, String forumId) {
        this.uid = uid;
        this.forumId = forumId;
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
}
