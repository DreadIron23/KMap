package sk.uniza.fri.janmokry.karnaughmap.data;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import sk.uniza.fri.janmokry.karnaughmap.util.SL;

/**
 * Service for posting in app events.
 */
public class EventBusService extends Bus implements SL.IService {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    EventBusService.super.post(event);
                }
            });
        }
    }
}
