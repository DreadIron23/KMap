package sk.uniza.fri.janmokry.karnaughmap.viewmodel.view;

import android.support.annotation.NonNull;

import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Solution;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.ProjectViewModel;

public interface IKarnaughMapsView extends IBaseProjectView {

    ProjectViewModel getProjectViewModel();
    void applyNewValuesToKMap(@NonNull Solution editedSolution);
}
