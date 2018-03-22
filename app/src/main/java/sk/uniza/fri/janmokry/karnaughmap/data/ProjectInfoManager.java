package sk.uniza.fri.janmokry.karnaughmap.data;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;

/**
 * Created by Janci on 22.11.2017.
 *
 * Abstraction over data access for {@link ProjectInfo}
 */

public class ProjectInfoManager implements SL.IService {

    private final ProjectInfoDao mProjectInfoDao = SL.get(DatabaseService.class).provide().getProjectInfoDao();

    public void getAllAsync(Consumer<? super List<ProjectInfo>> onSuccess) {
        mProjectInfoDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess);
    }

    public void isProjectNameUsedAsync(String projectName, Consumer<Boolean> onSuccess) {
        mProjectInfoDao.isProjectNameUsed(projectName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess);
    }

    /**
     *
     * @param projectInfo new ProjectInfo to be inserted
     * @param onSuccess callback returns ID of projectInfo inserted
     */
    public void insertAsync(ProjectInfo projectInfo, Consumer<Long> onSuccess) {
        Single.fromCallable( () -> mProjectInfoDao.insert(projectInfo))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess);
    }

    public void updateProjectNameAsync(ProjectInfo editingProjectInfo, String newProjectName, Action onComplete) {
        Completable.fromRunnable( () -> mProjectInfoDao.update(editingProjectInfo.renameTo(newProjectName)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete);
    }

    public void delete(ProjectInfo projectInfo, Action onComplete) {
        Completable.fromRunnable( () -> mProjectInfoDao.delete(projectInfo))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onComplete);
    }
}
