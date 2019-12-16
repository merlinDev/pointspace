package models;

import com.example.lazymessanger.R;

import java.io.Serializable;

public class User implements Serializable {

    public static final int USER_NOT_ACTIVATED = 0;
    public static final int USER_ACTIVATED = 1;
    public static final int USER_BLOCKED = 2;

    private final static String DEFAULT_PIC = "drawable://" + R.drawable.dummy_pic;

    private String name;
    private String uid;
    private int userStat;

    public User() {
    }

    public User(String uid, String name, int userStat) {
        this.uid = uid;
        this.name = name;
        this.userStat = userStat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getUserStat() {
        return userStat;
    }

    public void setUserStat(int userStat) {
        this.userStat = userStat;
    }
}