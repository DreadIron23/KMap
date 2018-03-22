package sk.uniza.fri.janmokry.karnaughmap.viewmodel.view;

import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;

public interface IAdjustProjectView extends IBaseProjectView {

    void finishView();

    void showAlreadyUsedNameError();

    void setName(String projectName);

    void launchProjectActivity(ProjectInfo projectInfo);
}
