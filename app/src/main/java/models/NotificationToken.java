package models;

public class NotificationToken {
    private String uid;
    private String token;

    public NotificationToken() {
    }

    public NotificationToken(String uid, String token) {
        this.uid = uid;
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
