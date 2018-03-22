package sk.uniza.fri.janmokry.karnaughmap.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.fragment.KarnaughMapsFragment;
import sk.uniza.fri.janmokry.karnaughmap.fragment.TruthTableFragment;

/**
 * Created by janmokry on 28.11.2017.
 */
public class ProjectViewPageAdapter extends FragmentPagerAdapter {

    private final Context mContext;

    public ProjectViewPageAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return KarnaughMapsFragment.newInstance();
            case 1:
                return TruthTableFragment.newInstance();
            default:
                throw new IllegalArgumentException("Position "+position+" not supported!");
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.project_screen_tab_0);
            case 1:
                return mContext.getResources().getString(R.string.project_screen_tab_1);
            default:
                throw new IllegalArgumentException("Position "+position+" not supported!");
        }
    }
}
