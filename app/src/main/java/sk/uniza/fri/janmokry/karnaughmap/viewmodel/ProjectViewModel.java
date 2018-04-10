package sk.uniza.fri.janmokry.karnaughmap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectInfo;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectKMap;
import sk.uniza.fri.janmokry.karnaughmap.data.ProjectKMapManager;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;
import sk.uniza.fri.janmokry.karnaughmap.kmap.TruthTableCollection;
import sk.uniza.fri.janmokry.karnaughmap.util.ConversionUtil;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IProjectView;

import static sk.uniza.fri.janmokry.karnaughmap.fragment.ProjectFragment.ARG_PROJECT_INFO;

/**
 * Business logic for project fragment
 */
public class ProjectViewModel extends ProjectBaseViewModel<IProjectView> {

    private ProjectInfo mProjectInfo;

    private LiveData<List<ProjectKMap>> mProjectKMaps;

    public MutableLiveData<TruthTableCollection> mTruthTableCollection = new MutableLiveData<>();

    private Observer<List<ProjectKMap>> mProjectKMapsTransformObserver = projectKMaps -> {
        Completable.fromRunnable( () -> {
            final TruthTableCollection truthTableCollection =
                    new TruthTableCollection(ConversionUtil.transformToKMapCollections(projectKMaps));
            mTruthTableCollection.postValue(truthTableCollection);
        })
                .subscribeOn(Schedulers.computation())
                .subscribe();
    };

    @Override
    public void onCreate(Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);
        mProjectInfo = (ProjectInfo) arguments.getSerializable(ARG_PROJECT_INFO);

        loadData();
    }

    private void loadData() {
        mProjectKMaps = SL.get(ProjectKMapManager.class).getKMapLiveData(mProjectInfo);
        mProjectKMaps.observeForever(mProjectKMapsTransformObserver);
    }

    public void onKarnaughMapsSave(ArrayList<KMapCollection> kMapCollections) {
        SL.get(ProjectKMapManager.class).updateProjectKMapsAsync(
                mProjectInfo, kMapCollections, () -> {});
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mProjectKMaps.removeObserver(mProjectKMapsTransformObserver);
    }
}
