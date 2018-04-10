package sk.uniza.fri.janmokry.karnaughmap.data.event;

import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;

/**
 * Event signalizing KMap has been removed.
 *
 * Created by Janci on 10.4.2018.
 */

public class KMapRemovalEvent {

    public final KMapCollection collection;

    public KMapRemovalEvent(KMapCollection collection) {
        this.collection = collection;
    }
}
