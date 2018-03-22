package sk.uniza.fri.janmokry.karnaughmap.data;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

import sk.uniza.fri.janmokry.karnaughmap.util.SL;

/**
 * Service for GSON instance.
 *
 * Created by Janci on 10.11.2017.
 */
public class GsonService implements SL.IService {

    private final Gson mGson;

    public GsonService(Context context) {
        mGson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
                .create();
    }

    public Gson provide() {
        return mGson;
    }
}
