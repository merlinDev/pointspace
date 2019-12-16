package controlers.asyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import com.example.lazymessanger.TagForumsActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.lang.ref.WeakReference;

public class TagLoader extends AsyncTask<String, String, Void> implements View.OnClickListener {

    private WeakReference<ChipGroup> weakReference;
    private WeakReference<Context> contextWeakReference;

    public TagLoader(Context context, ChipGroup chipGroup) {
        this.weakReference = new WeakReference<>(chipGroup);
        this.contextWeakReference = new WeakReference<>(context);

    }

    @Override
    protected Void doInBackground(String... tags) {

        for (String tag : tags) {
            publishProgress(tag);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        Chip chip = new Chip(contextWeakReference.get());
        chip.setText(values[0]);
        chip.setOnClickListener(this);
        weakReference.get().addView(chip);
    }

    @Override
    public void onClick(View view) {
        String tag = ((Chip) view).getText().toString();
        Intent intent = new Intent(contextWeakReference.get(), TagForumsActivity.class);
        intent.putExtra("tag", tag);
        contextWeakReference.get().startActivity(intent);
    }
}
