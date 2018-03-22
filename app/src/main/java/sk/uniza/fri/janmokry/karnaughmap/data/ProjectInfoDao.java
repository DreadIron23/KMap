package sk.uniza.fri.janmokry.karnaughmap.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Janci on 20.11.2017.
 */
@Dao
public interface ProjectInfoDao {

    @Query("SELECT * FROM " + ProjectInfo.TABLE_NAME)
    Single<List<ProjectInfo>> getAll();

    @Query("SELECT CASE WHEN EXISTS (" +
                "SELECT * FROM " + ProjectInfo.TABLE_NAME + " WHERE " + ProjectInfo.COLUMN_NAME + " = :projectName" +
            ")" +
            "THEN CAST(1 AS BIT)" +
            "ELSE CAST(0 AS BIT) END")
    Single<Boolean> isProjectNameUsed(String projectName);

    @Insert
    long insert(ProjectInfo projectInfo);

    @Update
    void update(ProjectInfo projectInfo);

    @Delete
    void delete(ProjectInfo projectInfo);
}
