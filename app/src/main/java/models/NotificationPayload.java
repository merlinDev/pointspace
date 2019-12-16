package models;

public class NotificationPayload {
    private String title;
    private String text;
    private String priority;

    public NotificationPayload(String title, String text, String priority) {
        this.title = title;
        this.text = text;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
