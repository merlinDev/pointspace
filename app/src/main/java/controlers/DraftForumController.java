package controlers;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.google.gson.Gson;

import java.util.List;

import models.Content;
import models.DraftForum;
import room.DraftForumDatabase;

public class DraftForumController {
    private static final String TAG = "DraftForumController";

    private DraftForumController() {
    }

    public static void saveToDrafts(Context context, String header, List<String> tags, List<Content> contents) {
        Gson gson = new Gson();

        String contentString = gson.toJson(contents);
        String tagString = gson.toJson(tags);

        DraftForum draftForum = new DraftForum(header, tagString, contentString);
        new Thread(() -> {
            DraftForumDatabase database = Room.databaseBuilder(context, DraftForumDatabase.class, "draft-forums").build();
            database.forumDao().saveDraftForum(draftForum);

            Log.d(TAG, "DraftForumController: forum saved to drafts....");
        }).start();
    }
}
