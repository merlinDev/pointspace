package room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import models.DraftForum;

@Dao
public interface DraftForumDao {

    @Query("SELECT * FROM draftforum")
    List<DraftForum> getAll();

    @Query("SELECT * FROM DraftForum WHERE id = :id")
    DraftForum getDraftForum(int id);

    @Insert
    void saveDraftForum(DraftForum draftForum);

    @Delete
    void delete(DraftForum draftForum);

    @Query("DELETE FROM draftforum")
    void deleteAll();

}
