package controlers.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hootsuite.nachos.NachoTextView;

import java.lang.ref.WeakReference;

import models.Tag;
import models.Forum;

public class CategorySuggest extends AsyncTask<Void, ArrayAdapter, Void> {

    private static final int MAX_COUNT = 2;

    private WeakReference<NachoTextView> chipViewReference;
    private WeakReference<Context> contextReference;

    public CategorySuggest(Context context, NachoTextView chipView) {
        contextReference = new WeakReference<>(context);
        chipViewReference = new WeakReference<>(chipView);
    }


    @Override
    protected Void doInBackground(Void... voids) {

        CollectionReference categoryCollection = FirebaseFirestore
                .getInstance()
                .collection(Forum.ABOUT_LIST);

        categoryCollection
                .whereGreaterThanOrEqualTo("count", MAX_COUNT)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Tag[] categories = queryDocumentSnapshots.toObjects(Tag.class).toArray(new Tag[]{});
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(contextReference.get(), android.R.layout.simple_dropdown_item_1line);

                    for (Tag tag : categories) {
                        arrayAdapter.add(tag.getAbout());
                    }
                    publishProgress(arrayAdapter);
                });

        return null;
    }

    @Override
    protected void onProgressUpdate(ArrayAdapter... values) {
        chipViewReference.get().setAdapter(values[0]);
        super.onProgressUpdate(values);
    }
}
