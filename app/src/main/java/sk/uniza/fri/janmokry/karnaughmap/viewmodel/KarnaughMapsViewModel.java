package sk.uniza.fri.janmokry.karnaughmap.viewmodel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Quine;
import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Solution;
import sk.uniza.fri.janmokry.karnaughmap.data.EventBusService;
import sk.uniza.fri.janmokry.karnaughmap.data.event.KMapAdditionEvent;
import sk.uniza.fri.janmokry.karnaughmap.data.event.KMapRemovalEvent;
import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IKarnaughMapsView;

/**
 * Business logic for KarnaughMapsFragment.
 */
public class KarnaughMapsViewModel extends ProjectBaseViewModel<IKarnaughMapsView> {

    public static class KMapConfigurationSolver {

        public final MutableLiveData<Solution> solution = new MutableLiveData<>();
        private Disposable mDisposable;
        private Disposable mAlgorithmStopper;


        public void solve(KMapCollection collection) {
            // cancel work
            dispose();

            // set and compute
            mAlgorithmStopper = new AlgorithmStopper();
            mDisposable = Single.fromCallable(() -> new Quine(collection.getNumberOfVariables()).compute(collection, mAlgorithmStopper))
                    .subscribeOn(Schedulers.computation())
                    .subscribe(result -> {
                        if (!result.isDisposed()) {
                            solution.postValue(result);
                        }
                    });
        }

        public void onDestroy() {
            dispose();
        }

        private void dispose() {
            if (mDisposable != null) {
                mDisposable.dispose();
                mAlgorithmStopper.dispose();
            }
        }

        private static class AlgorithmStopper implements Disposable {

            private volatile boolean isDisposed = false;

            @Override
            public void dispose() {
                isDisposed = true;
            }

            @Override
            public boolean isDisposed() {
                return isDisposed;
            }
        }
    }

    private final HashMap<String, KMapConfigurationSolver> mSolvers = new HashMap<>();

    /** Called when KMap is added to fragment */
    public void onKMapAddition(KMapCollection kMapCollection) {
        SL.get(EventBusService.class).post(new KMapAdditionEvent(kMapCollection));
    }

    /** Called when KMap is deleted from fragment */
    public void onKMapRemoval(KMapCollection kMapCollection) {
        unregisterKMapFromConfigurationCalculations(kMapCollection);
    }

    /** Should be called when new KMapView is created */
    public void registerKMapForConfigurationCalculations(@NonNull String title, @NonNull LifecycleOwner owner,
                                                         @NonNull Observer<Solution> observer) {
        // add if needed
        if (!mSolvers.containsKey(title)) {
            mSolvers.put(title, new KMapConfigurationSolver());
        }
        // register
        final KMapConfigurationSolver solver = mSolvers.get(title);
        solver.solution.observe(owner, observer);
    }

    public void onKMapsTitleChange(List<String> oldTitles, List<String> newTitles, KMapCollection removedItem) {
        final ArrayList<KMapConfigurationSolver> solverBuffer = new ArrayList<>();
        for (String oldTitle : oldTitles) {
            solverBuffer.add(mSolvers.remove(oldTitle));
        }

        for (int index = 0; index < solverBuffer.size(); index++) {
            mSolvers.put(newTitles.get(index), solverBuffer.get(index));
        }

        // fire event after kmaps have been renamed
        SL.get(EventBusService.class).post(new KMapRemovalEvent(removedItem));
    }

    public void onKMapConfigurationComputationTrigger(KMapCollection collection) {
        mSolvers.get(collection.getTitle()).solve(collection);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (String key : mSolvers.keySet()) {
            mSolvers.get(key).onDestroy();
        }
    }

    private void unregisterKMapFromConfigurationCalculations(KMapCollection kMapCollection) {
        final KMapConfigurationSolver removed = mSolvers.remove(kMapCollection.getTitle());
        removed.onDestroy();
    }
}
