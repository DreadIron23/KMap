package sk.uniza.fri.janmokry.karnaughmap.kmap;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Collection holding KMapCollections. Data holder for TruthTableView.
 *
 * Created by Janci on 30.3.2018.
 */

public class TruthTableCollection {

    private List<KMapCollection> mKMapCollections;

    private KMapCollection.OnTapListener mOnTapListener;

    public TruthTableCollection(List<KMapCollection> kMapCollections) {
        this.mKMapCollections = kMapCollections;
    }

    public List<KMapCollection> getKMapCollections() {
        return mKMapCollections;
    }

    public int getMaximumVariables() {
        int max = 0;
        for (KMapCollection collection : mKMapCollections) {
            final int variables = collection.getNumberOfVariables();
            if (max < variables) {
                max = variables;
            }
        }
        return max;
    }

    public int getMaximumKMapSize() {
        return (1 << getMaximumVariables());
    }

    public int getNumberOfMaps() {
        return mKMapCollections.size();
    }

    public boolean isValidPosition(int column, int row) {
        return column >= 0 && row >= 0 && column < getMaximumKMapSize() && row < getNumberOfMaps() && // boundary checks
                column < mKMapCollections.get(row).getSize(); // index validity check
    }

    public KMapCell get(int column, int row) {
        return mKMapCollections.get(row).get(column);
    }

    public boolean isEmpty() {
        return mKMapCollections.isEmpty();
    }

    public void onKMapAddition(@NonNull KMapCollection collection) {
        // add to list and register listener
        mKMapCollections.add(collection);
        collection.registerOnTapListener(mOnTapListener);
    }

    public void onKMapRemoval(@NonNull KMapCollection collection) {
        // remove from list, unregister listener and reorder
        mKMapCollections.remove(collection);
        collection.unregisterOnTapListener(mOnTapListener);
        Collections.sort(mKMapCollections, (first, second) ->
                first.getTitle().compareTo(second.getTitle()));
    }

    public void setOnTapListener(@NonNull KMapCollection.OnTapListener listener) {
        mOnTapListener = listener;
        registerOnTapListener();
    }

    public void removeOnTapListener() {
        if (mOnTapListener != null) {
            unregisterOnTapListener();
        }
    }

    private void registerOnTapListener() {
        for (KMapCollection collection : mKMapCollections) {
            collection.registerOnTapListener(mOnTapListener);
        }
    }

    private void unregisterOnTapListener() {
        for (KMapCollection collection : mKMapCollections) {
            collection.unregisterOnTapListener(mOnTapListener);
        }
    }
}
