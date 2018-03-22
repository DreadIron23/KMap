package sk.uniza.fri.janmokry.karnaughmap.viewmodel.view;

import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;

public interface IListOfProjectsView extends IBaseProjectView {

    void setData(List<ProjectInfo> projectInfoList);

    void deleteProject(ProjectInfo projectInfo);
}
