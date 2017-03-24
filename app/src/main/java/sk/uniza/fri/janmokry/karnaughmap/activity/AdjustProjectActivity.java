package sk.uniza.fri.janmokry.karnaughmap.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.fragment.AdjustProjectFragment;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.EmptyViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IEmptyView;

public class AdjustProjectActivity extends ProjectBaseActivity<IEmptyView, EmptyViewModel> {

    private static final String ADJUST_PROJECT_FRAGMENT_TAG = "adjustProjectFragmentTag";
    public static final String ARG_OLD_PROJECT_NAME = "oldProjectName";

    public static Intent newIntent(Context context) {
        return new Intent(context, AdjustProjectActivity.class);
    }

    public static Intent newIntentForEditing(Context context, String oldProjectName) {
        final Intent intent = new Intent(context, AdjustProjectActivity.class);
        intent.putExtra(ARG_OLD_PROJECT_NAME, oldProjectName);
        return intent;
    }

    @Nullable
    private String mOldProjectName;

    private AdjustProjectFragment mAdjustProjectFragment;

    @Override
    public Class<EmptyViewModel> getViewModelClass() {
        return EmptyViewModel.class;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_adjust_project;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_adjust_project);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }

        mOldProjectName = getIntent().getStringExtra(ARG_OLD_PROJECT_NAME);
        if (mOldProjectName != null) {
            setTitle(R.string.title_activity_adjust_project_edit_mode);
        }

        if (savedInstanceState == null) {
            mAdjustProjectFragment = AdjustProjectFragment.newInstance(mOldProjectName);
            loadFragment(mAdjustProjectFragment, ADJUST_PROJECT_FRAGMENT_TAG);
        } else {
            mAdjustProjectFragment = (AdjustProjectFragment) getSupportFragmentManager().findFragmentByTag(ADJUST_PROJECT_FRAGMENT_TAG);
        }
    }

    private void loadFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mAdjustProjectFragment.hasTextChanged()) {
                    new AlertDialog.Builder(this, R.style.DialogStyle)
                            .setTitle(R.string.adjust_project_screen_delete_dialog_title)
                            .setMessage(R.string.adjust_project_screen_delete_dialog_message)
                            .setPositiveButton(R.string.adjust_project_screen_delete_dialog_logout, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                }})
                            .setNegativeButton(R.string.adjust_project_screen_delete_dialog_discard, null)
                            .show();
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }
}
