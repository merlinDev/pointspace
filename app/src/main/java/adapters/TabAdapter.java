package adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class TabAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();

    public static final String HOME = "New";
    public static final String BOOKMARKS = "Bookmarks";
    public static final String PROFILE = "Profile";
    public static final String CATEGORY = "Tag";
    public static final String SETTINGS = "Settings";
    public static final String DRAFTS = "Drafts";

    public TabAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public void addFragment(Fragment fragment, String name) {
        fragmentList.add(fragment);
        nameList.add(name);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return nameList.get(position);
    }
}
