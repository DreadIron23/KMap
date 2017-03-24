package sk.uniza.fri.janmokry.karnaughmap.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import sk.uniza.fri.janmokry.karnaughmap.R;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.AdjustProjectViewModel;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IAdjustProjectView;

import static sk.uniza.fri.janmokry.karnaughmap.activity.AdjustProjectActivity.ARG_OLD_PROJECT_NAME;

public class AdjustProjectFragment extends ProjectBaseFragment<IAdjustProjectView, AdjustProjectViewModel> implements IAdjustProjectView {

    public static AdjustProjectFragment newInstance(@Nullable String oldProjectName) {
        final AdjustProjectFragment fragment = new AdjustProjectFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_OLD_PROJECT_NAME, oldProjectName);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.project_name)
    protected EditText mProjectName;

    @Nullable
    @Override
    public Class<AdjustProjectViewModel> getViewModelClass() {
        return AdjustProjectViewModel.class;
    }

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

    private boolean isMessageEmpty() {
        return mProjectName.getText().toString().isEmpty();
    }
}
