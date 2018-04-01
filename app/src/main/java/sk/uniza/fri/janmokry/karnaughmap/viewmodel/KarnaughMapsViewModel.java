package sk.uniza.fri.janmokry.karnaughmap.viewmodel;

import android.support.annotation.NonNull;

import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.IKarnaughMapsView;

/**
 * Business logic for KarnaughMapsFragment.
 */
public class KarnaughMapsViewModel extends ProjectBaseViewModel<IKarnaughMapsView> {

    @Override
    public void onBindView(@NonNull IKarnaughMapsView view) {
        super.onBindView(view);
    }

    /** Called when KMap is deleted from fragment */
    public void onKMapRemoval(KMapCollection kMapCollection) {
        if (getView() != null) {
            getView().getProjectViewModel().onKMapRemoval(kMapCollection);
        }
    }

    /** Called when KMap is added to fragment */
    public void onKMapAddition(KMapCollection kMapCollection) {
        if (getView() != null) {
            getView().getProjectViewModel().onKMapAddition(kMapCollection);
        }
    }
}
