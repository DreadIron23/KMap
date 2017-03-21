package sk.uniza.fri.janmokry.karnaughmap;

import android.app.Application;

import sk.uniza.fri.janmokry.karnaughmap.util.SL;

public class ProjectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SL.init(this);
    }
}
