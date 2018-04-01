package sk.uniza.fri.janmokry.karnaughmap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectKMap;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectKMapManager;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IProjectView;

import static sk.uniza.fri.janmokry.karnaughmap.fragment.ProjectFragment.ARG_PROJECT_INFO;

/**
 * Business logic for project fragment
 */
public class ProjectViewModel extends ProjectBaseViewModel<IProjectView> {

    private ProjectInfo mProjectInfo;

    public LiveData<List<ProjectKMap>> mProjectKMaps;

    @Override
    public void onCreate(Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);
        mProjectInfo = (ProjectInfo) arguments.getSerializable(ARG_PROJECT_INFO);

        loadData();
    }

    private void loadData() {
        mProjectKMaps = SL.get(ProjectKMapManager.class).getKMapLiveData(mProjectInfo);
    }


    public void onKMapAddition(KMapCollection kMapCollection) {
        // TODO: hookup events to TruthTable
    }

    public void onKMapRemoval(KMapCollection kMapCollection) {
        // TODO: hookup events to TruthTable
    }

    public void onKarnaughMapsSave(List<String> serializedKMapCollections, List<String> kMapTitles) {
        SL.get(ProjectKMapManager.class).updateProjectKMapsAsync(
                mProjectInfo, serializedKMapCollections, kMapTitles, () -> {});
    }
}
