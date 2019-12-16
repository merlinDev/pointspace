package models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity
public class DraftForum implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "header")
    private String header;
    @ColumnInfo(name = "aboutList")
    private String aboutList;
    @ColumnInfo(name = "contentList")
    private String contentList;
    @ColumnInfo(name = "timeStamp")
    private long timeStamp;

    public DraftForum() {
    }

    @Ignore
    public DraftForum(String header, String aboutList, String contentList) {
        this.header = header;
        this.aboutList = aboutList;
        this.contentList = contentList;
        this.timeStamp = new Date().getTime();
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getAboutList() {
        return aboutList;
    }

    public void setAboutList(String aboutList) {
        this.aboutList = aboutList;
    }

    public String getContentList() {
        return contentList;
    }

    public void setContentList(String contentList) {
        this.contentList = contentList;
    }
}
