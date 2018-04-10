package sk.uniza.fri.janmokry.karnaughmap.viewmodel;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;

import sk.uniza.fri.janmokry.karnaughmap.data.EventBusService;
import sk.uniza.fri.janmokry.karnaughmap.data.event.KMapAdditionEvent;
import sk.uniza.fri.janmokry.karnaughmap.data.event.KMapRemovalEvent;
import sk.uniza.fri.janmokry.karnaughmap.data.event.KMapVariableCountChangeEvent;
import sk.uniza.fri.janmokry.karnaughmap.util.SL;
import sk.uniza.fri.janmokry.karnaughmap.viewmodel.view.ITruthTableView;

/**
 * Business logic for {@link sk.uniza.fri.janmokry.karnaughmap.fragment.TruthTableFragment}
 */
public class TruthTableViewModel extends ProjectBaseViewModel<ITruthTableView> {

    @Override
    public void onCreate(Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        SL.get(EventBusService.class).register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SL.get(EventBusService.class).unregister(this);
    }

    @Subscribe
    public void onKMapAddition(KMapAdditionEvent event) {
        if (getView() != null) {
            getView().onKMapAddition(event.collection);
        }
    }

    @Subscribe
    public void onKMapRemoval(KMapRemovalEvent event) {
        if (getView() != null) {
            getView().onKMapRemoval(event.collection);
        }
    }

    @Subscribe
    public void onKMapVariableCountChange(KMapVariableCountChangeEvent event) {
        if (getView() != null) {
            getView().layoutTruthTable();
        }
    }
}
