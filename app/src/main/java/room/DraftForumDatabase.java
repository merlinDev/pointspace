package room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import models.DraftForum;

@Database(entities = {DraftForum.class}, version = 1, exportSchema = false)
public abstract class DraftForumDatabase extends RoomDatabase {
    public abstract DraftForumDao forumDao();
}
