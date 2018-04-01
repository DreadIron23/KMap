package sk.uniza.fri.janmokry.karnaughmap.viewmodel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;

import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.data.EventBusService;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfoManager;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectKMapManager;
import sk.uniza.fri.janmokry.karnaughmap.data.event.RefreshListOfProjectsContentEvent;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IListOfProjectsView;

/**
 * Business logic for list of project screen
 */
public class ListOfProjectsViewModel extends ProjectBaseViewModel<IListOfProjectsView> {

    @Nullable
    private List<ProjectInfo> mProjectInfos;

    @Override
    public void onCreate(Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        SL.get(EventBusService.class).register(this);
        initProjectNames();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SL.get(EventBusService.class).unregister(this);
    }

    @Override
    public void onBindView(@NonNull IListOfProjectsView view) {
        super.onBindView(view);

        if(mProjectInfos != null) {
            setDataToView();
        }
    }

    @Subscribe
    public void onRefreshListOfProjectsContentPosted(RefreshListOfProjectsContentEvent event) {
        initProjectNames();
        setDataToView();
    }

    public void deleteProject(ProjectInfo projectInfo) {
        SL.get(ProjectInfoManager.class).deleteAsync(projectInfo, () -> deleteFromView(projectInfo));
        SL.get(ProjectKMapManager.class).deleteAllWhereProjectInfoIs(projectInfo, () -> {} );
    }

    private void deleteFromView(ProjectInfo projectInfo) {
        if (getView() != null) {
            getView().deleteProject(projectInfo);
        }
    }

    private void setDataToView() {
        final IListOfProjectsView view = getView();
        if (view != null) {
            view.setData(mProjectInfos);
        }
    }

    private void initProjectNames() {
        SL.get(ProjectInfoManager.class).getAllAsync(projectInfos -> {
            mProjectInfos = projectInfos;
            setDataToView();
        });
    }
}
