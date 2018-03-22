package sk.uniza.fri.janmokry.karnaughmap.data;

import android.arch.persistence.room.Room;
import android.content.Context;

import sk.uniza.fri.janmokry.karnaughmap.util.SL;

/**
 * Service for database instance.
 *
 * Created by Janci on 20.11.2017.
 */
public class DatabaseService implements SL.IService {

    private final KarnaughDatabase mDatabase;

    public DatabaseService(Context context) {
        mDatabase = Room.databaseBuilder(context.getApplicationContext(), KarnaughDatabase.class, "karnaugh-database")
                .build();
    }

    public KarnaughDatabase provide() {
        return mDatabase;
    }
}
