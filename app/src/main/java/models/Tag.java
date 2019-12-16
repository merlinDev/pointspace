package models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Tag {

    @NonNull
    @PrimaryKey
    private String about;
    @ColumnInfo(name = "count")
    private long count;

    public Tag() {

    }

    @Ignore
    public Tag(String about) {
        this.about = about;
        this.count = 1;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
