package sk.uniza.fri.janmokry.karnaughmap;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import sk.uniza.fri.janmokry.karnaughmap.util.SL;

public class ProjectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
        SL.init(this);
    }
}
