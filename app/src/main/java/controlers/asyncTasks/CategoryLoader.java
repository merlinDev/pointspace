package controlers.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;
import java.util.List;

import models.Tag;
import models.Forum;

public class CategoryLoader extends AsyncTask<Void, Chip, Void> {

    private WeakReference<ChipGroup> chipGroupWeakReference;
    private WeakReference<Context> contextWeakReference;

    public CategoryLoader(Context context, ChipGroup chipGroup) {
        contextWeakReference = new WeakReference<>(context);
        chipGroupWeakReference = new WeakReference<>(chipGroup);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        FirebaseFirestore.getInstance()
                .collection(Forum.ABOUT_LIST)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Tag> categories = queryDocumentSnapshots.toObjects(Tag.class);

            for (Tag tag : categories) {
                Chip chip = new Chip(contextWeakReference.get());
                chip.setText(tag.getAbout());
                publishProgress(chip);
            }
        });

        return null;
    }

    @Override
    protected void onProgressUpdate(Chip... values) {
        super.onProgressUpdate(values);
        Chip chip = values[0];
        chipGroupWeakReference.get().addView(chip);
    }
}
