package sk.uniza.fri.janmokry.karnaughmap.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;
import sk.uniza.fri.janmokry.karnaughmap.fragment.ProjectFragment;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.EmptyViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IEmptyView;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class ProjectActivity extends ProjectBaseActivity<IEmptyView, EmptyViewModel> {

    public static final String EXTRA_PROJECT_INFO = "project_info";

    public static Intent newIntent(Context context, ProjectInfo projectInfo) {
        final Intent intent = new Intent(context, ProjectActivity.class);
        intent.putExtra(EXTRA_PROJECT_INFO, projectInfo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        switchToFullscreenOnLandscape();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_project;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            final ProjectInfo projectInfo = (ProjectInfo) getIntent().getExtras().getSerializable(EXTRA_PROJECT_INFO);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.project_container, ProjectFragment.newInstance(projectInfo))
                    .commit();
        }
    }

    /** Switch to fullscreen on landscape. */
    private void switchToFullscreenOnLandscape() {
        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_screen, menu);
        return true;
    }
}
