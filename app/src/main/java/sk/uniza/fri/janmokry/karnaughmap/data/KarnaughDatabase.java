package sk.uniza.fri.janmokry.karnaughmap.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Janci on 20.11.2017.
 */
@Database(entities = {ProjectInfo.class, ProjectKMap.class}, version = 1)
public abstract class KarnaughDatabase extends RoomDatabase {

    public abstract ProjectInfoDao getProjectInfoDao();

    public abstract ProjectKMapDao getProjectKMapDao();
}
