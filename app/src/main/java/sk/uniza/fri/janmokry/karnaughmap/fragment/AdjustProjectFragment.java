package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.activity.ProjectActivity;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.AdjustProjectViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IAdjustProjectView;

import static sk.uniza.fri.janmokry.karnaughmap.activity.AdjustProjectActivity.ARG_EDITING_PROJECT_INFO;

public class AdjustProjectFragment extends ProjectBaseFragment<IAdjustProjectView, AdjustProjectViewModel> implements IAdjustProjectView {

    public static AdjustProjectFragment newInstance(@Nullable ProjectInfo editingProjectInfo) {
        final AdjustProjectFragment fragment = new AdjustProjectFragment();
        final Bundle args = new Bundle();
        args.putSerializable(ARG_EDITING_PROJECT_INFO, editingProjectInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.project_name)
    protected EditText mProjectName;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_adjust_project;
    }

    @Override
    protected void init() {
    }

    @OnClick(R.id.confirmation_button)
    public void onConfirmationButtonClicked() {
        if (isMessageEmpty()) {
            mProjectName.setError(getContext().getString(R.string.adjust_project_error_no_name));
            mProjectName.requestFocus();
            return;
        }

        getViewModel().projectNameConfirmed(mProjectName.getText().toString());
    }

    public boolean hasTextChanged() {
        return getViewModel().hasTextChanged(mProjectName.getText().toString());
    }

    @Override
    public void finishView() {
        getActivity().finish();
    }

    @Override
    public void showAlreadyUsedNameError() {
        mProjectName.setError(getContext().getString(R.string.adjust_project_error_already_used_name));
        mProjectName.requestFocus();
    }


    @Override
    public void setName(String projectName) {
        mProjectName.setText(projectName);
    }

    @Override
    public void launchProjectActivity(ProjectInfo projectInfo) {
        final Intent intent = ProjectActivity.newIntent(getContext(), projectInfo);
        startActivity(intent);
    }

    private boolean isMessageEmpty() {
        return mProjectName.getText().toString().isEmpty();
    }
}
