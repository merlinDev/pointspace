
package controlers;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import models.Forum;

public class AdapterCallback extends DiffUtil.Callback {

    private static final String TAG = "AdapterCallback";

    private List<Forum> oldForum;
    private List<Forum> newForum;

    public AdapterCallback(List<Forum> oldSnaps, List<Forum> newSnaps) throws IndexOutOfBoundsException{
        this.oldForum = oldSnaps;
        this.newForum = newSnaps;
    }

    @Override
    public int getOldListSize() {
        return oldForum.size();
    }

    @Override
    public int getNewListSize() {
        return newForum.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Log.d(TAG, "areItemsTheSame: same item found...");
        return oldForum.get(oldItemPosition).getTimeStamp() == newForum.get(newItemPosition).getTimeStamp();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Log.d(TAG, "areContentsTheSame: same content found");
        return newForum.get(oldItemPosition).getHeader().equals(newForum.get(newItemPosition).getHeader());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
