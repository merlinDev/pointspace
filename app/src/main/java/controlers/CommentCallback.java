package controlers;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

import models.Comment;

public class CommentCallback extends DiffUtil.Callback {

    private List<Comment> oldData;
    private List<Comment> newData;

    public CommentCallback(List<Comment> oldData, List<Comment> newData) {
        this.oldData = oldData;
        this.newData = newData;
    }

    @Override
    public int getOldListSize() {
        return oldData.size();
    }

    @Override
    public int getNewListSize() {
        return newData.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldData.get(oldItemPosition).equals(newData.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldData.get(oldItemPosition).getTimeStamp() == newData.get(newItemPosition).getTimeStamp();
    }
}
