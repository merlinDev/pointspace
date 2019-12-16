package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lazymessanger.R;
import com.google.android.material.chip.ChipGroup;

import controlers.asyncTasks.CategoryLoader;

public class CategoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_layout, container, false);

        ChipGroup chipGroup = view.findViewById(R.id.chipGroup);
        chipGroup.setChipSpacingVertical(16);

        new CategoryLoader(getContext(), chipGroup).execute();

        return view;
    }
}
