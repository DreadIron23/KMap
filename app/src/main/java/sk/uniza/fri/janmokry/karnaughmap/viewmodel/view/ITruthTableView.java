package sk.uniza.fri.janmokry.karnaughmap.viewmodel.view;

import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;

public interface ITruthTableView extends IBaseProjectView {

    void onKMapAddition(KMapCollection collection);
    void onKMapRemoval(KMapCollection collection);
    void layoutTruthTable();
    void invalidate();
}
