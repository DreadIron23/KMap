package sk.uniza.fri.janmokry.karnaughmap.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;
import sk.uniza.fri.janmokry.karnaughmap.fragment.ProjectFragment;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.ProjectViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IProjectView;

public class ProjectActivity extends ProjectBaseActivity<IProjectView, ProjectViewModel> {

    public static final String EXTRA_PROJECT_INFO = "project_info";

    public static Intent newIntent(Context context, ProjectInfo projectInfo) {
        final Intent intent = new Intent(context, ProjectActivity.class);
        intent.putExtra(EXTRA_PROJECT_INFO, projectInfo);
        return intent;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_project;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_project);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }

        if (savedInstanceState == null) {
            final ProjectInfo projectInfo = (ProjectInfo) getIntent().getExtras().getSerializable(EXTRA_PROJECT_INFO);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.project_container, ProjectFragment.newInstance(projectInfo))
                    .commit();
        }
    }
}