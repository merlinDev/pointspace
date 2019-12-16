package models;

import java.io.Serializable;

public class Content implements Serializable {

    public static final String CONTENT_TEXT = "text";
    public static final String CONTENT_IMAGE = "image";
    public static final String CONTENT_LOCATION = "location";

    private String type;
    private String content;

    public Content() {

    }

    public Content(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
