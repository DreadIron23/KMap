package sk.uniza.fri.janmokry.karnaughmap.data;

import android.arch.lifecycle.LiveData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;

/**
 * Created by Janci on 31.03.2018.
 *
 * Abstraction over data access for {@link ProjectKMap}
 *
 * Our strategy is we override(delete and then insert) all KMaps on save.
 */

public class ProjectKMapManager implements SL.IService {

    private final ProjectKMapDao mProjectKMapDao = SL.get(DatabaseService.class).provide().getProjectKMapDao();

    public void deleteAllWhereProjectInfoIs(ProjectInfo projectInfo, Action onComplete) {
        Completable.fromRunnable( () -> mProjectKMapDao.deleteAllWhereProjectIdIs(projectInfo.id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete);
    }

    public void updateProjectKMapsAsync(ProjectInfo projectInfo, List<String> serializedKMapCollections,
                                        List<String> kMapTitles, Action onComplete) {
        Completable.fromRunnable( () -> {
            mProjectKMapDao.deleteAllWhereProjectIdIs(projectInfo.id);
            mProjectKMapDao.insert(ProjectKMap.transform(projectInfo, serializedKMapCollections, kMapTitles));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete);
    }

    public LiveData<List<ProjectKMap>> getKMapLiveData(ProjectInfo projectInfo) {
        return mProjectKMapDao.findByProjectInfoId(projectInfo.id);
    }
}
