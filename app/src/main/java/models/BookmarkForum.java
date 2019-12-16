package models;

public class BookmarkForum extends Forum {
    private String bookmarkerUid;

    public BookmarkForum() {

    }

    public BookmarkForum(String bookmarkerUid, Forum forum) {
        setAbout(forum.getAbout());
        setContents(forum.getContents());
        setHeader(forum.getHeader());
        setPrivacy(forum.getPrivacy());
        setId(forum.getId());
        setUid(forum.getUid());
        this.bookmarkerUid = bookmarkerUid;
    }

    public String getBookmarkerUid() {
        return bookmarkerUid;
    }

    public void setBookmarkerUid(String bookmarkerUid) {
        this.bookmarkerUid = bookmarkerUid;
    }
}
