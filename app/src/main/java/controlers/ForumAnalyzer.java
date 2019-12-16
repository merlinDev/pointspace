package controlers;

import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ForumAnalyzer {
    private FirebaseFirestore database;
    private ArrayList<BarEntry> entries;

    public ForumAnalyzer(ArrayList<BarEntry> entries) {
        this.entries = entries;
        this.database = FirebaseFirestore.getInstance();
    }

    public void getCommentCount(String forumId) {
        database.collection("comments")
                .whereEqualTo("forumId", forumId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int commentSize = queryDocumentSnapshots.size();
                        BarEntry barEntry = new BarEntry(commentSize, 1);
                        entries.add(barEntry);
                    }
                });
    }
}
