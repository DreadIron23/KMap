package sk.uniza.fri.janmokry.karnaughmap.viewmodel.view;

public interface IAdjustProjectView extends IProjectView {

    void finishView();

    void showAlreadyUsedNameError();

    void setName(String projectName);
}
