package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.activity.ProjectBaseActivity;
import sk.uniza.fri.janmokry.karnaughmap.adapter.ProjectViewPageAdapter;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;
import sk.uniza.fri.janmokry.karnaughmap.view.NonSwipeableViewPager;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.ProjectViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IProjectView;

/**
 * View logic
 */
public class ProjectFragment extends ProjectBaseFragment<IProjectView, ProjectViewModel> implements IProjectView {

    public static final String ARG_PROJECT_INFO = "projectInfo";

    public static ProjectFragment newInstance(ProjectInfo projectInfo) {
        final ProjectFragment projectFragment = new ProjectFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_PROJECT_INFO, projectInfo);
        projectFragment.setArguments(args);
        return projectFragment;
    }

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.toolbar_title)
    protected TextView mToolbarTitle;

    @BindView(R.id.tab_layout)
    protected TabLayout mTabLayout;

    @BindView(R.id.pager)
    protected NonSwipeableViewPager mViewPager;

    @BindView(R.id.pager_layout)
    protected LinearLayout mPagerLayout;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewPager.setAdapter(new ProjectViewPageAdapter(getChildFragmentManager(), getContext()));
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_project;
    }

    @Override
    protected void init() {

        final ProjectBaseActivity projectBaseActivity = (ProjectBaseActivity) getActivity();
        projectBaseActivity.setSupportActionBar(mToolbar);
        final ActionBar supportActionBar = projectBaseActivity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        final ProjectInfo projectInfo = (ProjectInfo) getArguments().getSerializable(ARG_PROJECT_INFO);
        setTitle(projectInfo.name);
    }

    private void setTitle(String title) {
        mToolbarTitle.setText(title);
    }
}
