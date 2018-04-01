package sk.uniza.fri.janmokry.karnaughmap.kmap;

import java.util.List;

/**
 * Collection holding KMapCollections. Data holder for TruthTableView.
 *
 * Created by Janci on 30.3.2018.
 */

public class TruthTableCollection {

    private List<KMapCollection> mKMapCollections;

    public TruthTableCollection(List<KMapCollection> mKMapCollections) {
        this.mKMapCollections = mKMapCollections;
    }
}
