package sk.uniza.fri.janmokry.karnaughmap.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Janci on 31.03.2018.
 */
@Dao
public interface ProjectKMapDao {

    @Query("SELECT * FROM " + ProjectKMap.TABLE_NAME + " WHERE " + ProjectKMap.COLUMN_PROJECT_ID + " = :projectInfoId ORDER BY " + ProjectKMap.COLUMN_TITLE)
    LiveData<List<ProjectKMap>> findByProjectInfoId(long projectInfoId);

    @Query("DELETE FROM " + ProjectKMap.TABLE_NAME + " WHERE " + ProjectKMap.COLUMN_PROJECT_ID + " = :projectInfoId")
    void deleteAllWhereProjectIdIs(long projectInfoId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<ProjectKMap> projectKMap);
}
