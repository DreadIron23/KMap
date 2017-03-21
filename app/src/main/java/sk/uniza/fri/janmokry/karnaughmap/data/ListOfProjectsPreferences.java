package sk.uniza.fri.janmokry.karnaughmap.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import sk.uniza.fri.janmokry.karnaughmap.util.SL;

/**
 * Data access the to list of projects
 */
public class ListOfProjectsPreferences implements SL.IService {

    private static final String TAG = ListOfProjectsPreferences.class.getName();
    private static final String PROJECT_NAMES = "project_names";

    private final Context mContext;
    private final SharedPreferences mSharedPreferences;

    public ListOfProjectsPreferences(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    public Set<String> getProjectNames() {
        return mSharedPreferences.getStringSet(PROJECT_NAMES, new HashSet<String>());
    }

    public boolean isProjectNameUsed(String projectName) {
        final Set<String> names = mSharedPreferences.getStringSet(PROJECT_NAMES, new HashSet<String>());
        return names.contains(projectName);
    }

    public void addProjectName(String projectName) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        final Set<String> set = mSharedPreferences.getStringSet(PROJECT_NAMES, new HashSet<String>());

        set.add(projectName);

        editor.putStringSet(PROJECT_NAMES, set);
        editor.apply();
    }

    public void removeProjectName(String projectName) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        final Set<String> set = mSharedPreferences.getStringSet(PROJECT_NAMES, new HashSet<String>());

        set.remove(projectName);

        editor.putStringSet(PROJECT_NAMES, set);
        editor.apply();
    }

    public void updateProjectName(String oldProjectName, String newProjectName) {
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        final Set<String> set = mSharedPreferences.getStringSet(PROJECT_NAMES, new HashSet<String>());

        set.remove(oldProjectName);
        set.add(newProjectName);

        editor.putStringSet(PROJECT_NAMES, set);
        editor.apply();
    }
}
