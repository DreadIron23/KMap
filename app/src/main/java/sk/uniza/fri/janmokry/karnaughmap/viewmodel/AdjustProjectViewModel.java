package sk.uniza.fri.janmokry.karnaughmap.viewmodel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import sk.uniza.fri.janmokry.karnaughmap.activity.AdjustProjectActivity;
import sk.uniza.fri.janmokry.karnaughmap.data.EventBusService;
import sk.uniza.fri.janmokry.karnaughmap.data.ListOfProjectsPreferences;
import sk.uniza.fri.janmokry.karnaughmap.data.event.RefreshListOfProjectsContentEvent;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IAdjustProjectView;

/**
 * Business logic for creating new and updating existing project screen.
 */
public class AdjustProjectViewModel extends ProjectBaseViewModel<IAdjustProjectView> {

    @Nullable
    private String mOldProjectName;

    @Override
    public void onCreate(Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        mOldProjectName = arguments.getString(AdjustProjectActivity.ARG_OLD_PROJECT_NAME);
    }

    @Override
    public void onBindView(@NonNull IAdjustProjectView view) {
        super.onBindView(view);

        if (isEditMode()) {
            view.setName(mOldProjectName);
        }
    }

    public boolean hasTextChanged(String currentText) {
        return isEditMode() ? !currentText.equals(mOldProjectName) : !currentText.isEmpty();
    }

    public void projectNameConfirmed(String newProjectName) {
        final ListOfProjectsPreferences preferences = SL.get(ListOfProjectsPreferences.class);

        if (preferences.isProjectNameUsed(newProjectName)) {
            showAlreadyUsedNameError();
            return;
        }

        if (isEditMode()) {
            preferences.updateProjectName(mOldProjectName, newProjectName);
        } else {
            preferences.addProjectName(newProjectName);
            // TODO open new project screen
        }

        SL.get(EventBusService.class).post(new RefreshListOfProjectsContentEvent());
        finishView();
    }

    private boolean isEditMode() {
        return mOldProjectName != null;
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
