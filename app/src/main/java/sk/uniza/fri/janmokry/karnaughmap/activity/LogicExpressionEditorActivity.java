package sk.uniza.fri.janmokry.karnaughmap.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;

import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Solution;
import sk.uniza.fri.janmokry.karnaughmap.fragment.LogicExpressionEditorFragment;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.EmptyViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IEmptyView;

public class LogicExpressionEditorActivity extends ProjectBaseActivity<IEmptyView, EmptyViewModel> {

    private static final String LOGIC_EXPRESSION_EDITOR_FRAGMENT_TAG = "logicExpressionEditorFragmentTag";
    public static final String ARG_SOLUTION = "solution";
    public static final String ARG_NUMBER_OF_VARIABLES = "numberOfVariables";

    public static Intent newIntent(Context context, Solution solution, int numberOfVariables) {
        final Intent intent = new Intent(context, LogicExpressionEditorActivity.class);
        intent.putExtra(ARG_SOLUTION, solution);
        intent.putExtra(ARG_NUMBER_OF_VARIABLES, numberOfVariables);
        return intent;
    }

    private LogicExpressionEditorFragment mLogicExpressionEditorFragment;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_logic_expression_editor;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }

        final Solution solution = (Solution) getIntent().getSerializableExtra(ARG_SOLUTION);
        final int numberOfVariables = getIntent().getIntExtra(ARG_NUMBER_OF_VARIABLES, Integer.MIN_VALUE);
        final SpannableStringBuilder title = new SpannableStringBuilder(getResources()
                .getText(R.string.title_activity_logic_expression_editor));
        setTitle(title);

        if (savedInstanceState == null) {
            mLogicExpressionEditorFragment = LogicExpressionEditorFragment.newInstance(solution, numberOfVariables);
            loadFragment(mLogicExpressionEditorFragment, LOGIC_EXPRESSION_EDITOR_FRAGMENT_TAG);
        } else {
            mLogicExpressionEditorFragment =
                    (LogicExpressionEditorFragment) getSupportFragmentManager()
                            .findFragmentByTag(LOGIC_EXPRESSION_EDITOR_FRAGMENT_TAG);
        }
    }

    private void loadFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, tag)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logic_expression_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (arePendingChangesToBeSaved()) {
                    showClosingConfirmationDialog();
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showClosingConfirmationDialog() {
        new AlertDialog.Builder(this, R.style.DialogStyle)
                .setTitle(R.string.adjust_project_screen_delete_dialog_title)
                .setMessage(R.string.adjust_project_screen_delete_dialog_message)
                .setPositiveButton(R.string.adjust_project_screen_delete_dialog_logout, (dialog, whichButton) -> finish())
                .setNegativeButton(R.string.adjust_project_screen_delete_dialog_discard, null)
                .show();
    }

    private boolean arePendingChangesToBeSaved() {
        return mLogicExpressionEditorFragment.hasExpressionChanged();
    }

    @Override
    public void onBackPressed() {
        if (arePendingChangesToBeSaved()) {
            showClosingConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }
}
