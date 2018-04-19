package sk.uniza.fri.janmokry.karnaughmap.viewmodel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import sk.uniza.fri.janmokry.karnaughmap.activity.AdjustProjectActivity;
import sk.uniza.fri.janmokry.karnaughmap.data.EventBusService;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfoManager;
import sk.uniza.fri.janmokry.karnaughmap.data.event.ProjectNameChangeEvent;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IAdjustProjectView;

/**
 * Business logic for creating new and updating existing project screen.
 */
public class AdjustProjectViewModel extends ProjectBaseViewModel<IAdjustProjectView> {

    @Nullable
    private ProjectInfo mEditingProjectInfo;

    @Override
    public void onCreate(Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        mEditingProjectInfo = (ProjectInfo) arguments.getSerializable(AdjustProjectActivity.ARG_EDITING_PROJECT_INFO);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onBindView(@NonNull IAdjustProjectView view) {
        super.onBindView(view);

        if (isEditMode()) {
            view.setName(mEditingProjectInfo.name);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public boolean hasTextChanged(String currentText) {
        return isEditMode() ? !currentText.equals(mEditingProjectInfo.name) : !currentText.isEmpty();
    }

    public void projectNameConfirmed(String newProjectName) {
        final ProjectInfoManager projectInfoManager = SL.get(ProjectInfoManager.class);

        projectInfoManager.isProjectNameUsedAsync(newProjectName, isProjectNameUsed -> {
                    if (isProjectNameUsed) {
                        showAlreadyUsedNameError();
                    } else {
                        if (isEditMode()) {
                            projectInfoManager.updateProjectNameAsync(mEditingProjectInfo, newProjectName, () -> {
                                        SL.get(EventBusService.class).post(new ProjectNameChangeEvent(newProjectName));
                                        finishView();
                                    });
                        } else {
                            projectInfoManager.insertAsync(new ProjectInfo(newProjectName), insertedId -> {
                                        launchProjectActivity(new ProjectInfo(insertedId, newProjectName));
                                        SL.get(EventBusService.class).post(new ProjectNameChangeEvent(newProjectName));
                                        finishView();
                                    });
                        }
                    }
                });
    }

    private void launchProjectActivity(ProjectInfo projectInfo) {
        if (getView() != null) {
            getView().launchProjectActivity(projectInfo);
        }
    }

    private boolean isEditMode() {
        return mEditingProjectInfo != null;
    }

    private void showAlreadyUsedNameError() {
        if (getView() != null) {
            getView().showAlreadyUsedNameError();
        }
    }

    private void finishView() {
        if (getView() != null) {
            getView().finishView();
        }
    }
}
