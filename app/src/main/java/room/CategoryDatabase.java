package room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import models.Tag;

@Database(entities = {Tag.class}, version = 1, exportSchema = false)
public abstract class CategoryDatabase extends RoomDatabase {
    public abstract CategoryDao categoryDao();
}
