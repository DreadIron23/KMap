package sk.uniza.fri.janmokry.karnaughmap.data.event;

import sk.uniza.fri.janmokry.karnaughmap.kmap.KMapCollection;

/**
 * Event signalizing KMap has been added.
 *
 * Created by Janci on 10.4.2018.
 */

public class KMapAdditionEvent {

    public final KMapCollection collection;

    public KMapAdditionEvent(KMapCollection collection) {
        this.collection = collection;
    }
}
