package sk.uniza.fri.janmokry.karnaughmap.viewmodel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.data.EventBusService;
import sk.uniza.fri.janmokry.karnaughmap.data.ListOfProjectsPreferences;
import sk.uniza.fri.janmokry.karnaughmap.data.event.RefreshListOfProjectsContentEvent;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IListOfProjectsView;

/**
 * Business logic for list of project screen
 */
public class ListOfProjectsViewModel extends ProjectBaseViewModel<IListOfProjectsView> {

    @Nullable
    private List<String> mProjectNames;

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

        if(mProjectNames != null) {
            setDataToView();
        }
    }

    @Subscribe
    public void onRefreshListOfProjectsContentPosted(RefreshListOfProjectsContentEvent event) {
        initProjectNames();
        setDataToView();
    }

    public void deleteProject(String projectName) {
        SL.get(ListOfProjectsPreferences.class).removeProjectName(projectName);
        initProjectNames();
        deleteFromView(projectName);
    }

    private void deleteFromView(String projectName) {
        if (getView() != null) {
            getView().deleteProject(projectName);
        }
    }

    private void setDataToView() {
        final IListOfProjectsView view = getView();
        if (view != null) {
            view.setData(mProjectNames);
        }
    }

    private void initProjectNames() {
        mProjectNames = new ArrayList<>(SL.get(ListOfProjectsPreferences.class).getProjectNames());
    }
}
