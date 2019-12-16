package room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import models.Tag;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM Tag")
    List<Tag> getAll();

    @Query("SELECT * FROM Tag WHERE about = :string")
    Tag getCategory(String string);

    @Query("SELECT * FROM Tag WHERE about LIKE :about LIMIT 5")
    List<Tag> getCategories(String about);

    @Insert
    void addAll(Tag... categories);

    @Delete
    void delete(Tag tag);

    @Query("DELETE FROM Tag")
    void deleteAll();
}
