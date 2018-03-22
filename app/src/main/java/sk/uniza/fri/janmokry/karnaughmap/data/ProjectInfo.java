package sk.uniza.fri.janmokry.karnaughmap.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.provider.BaseColumns;

import java.io.Serializable;

/**
 * Created by Janci on 16.11.2017.
 *
 * Immutable
 */
@Entity(tableName = ProjectInfo.TABLE_NAME)
public class ProjectInfo implements Serializable {

    public static final String TABLE_NAME = "projects";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_NAME = "name";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = COLUMN_ID)
    public final long id;

    @ColumnInfo(name = COLUMN_NAME)
    public final String name;

    @Ignore
    public ProjectInfo(String name) {
        this.id = 0L;
        this.name = name;
    }

    public ProjectInfo(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProjectInfo renameTo(String newName) {
        return new ProjectInfo(id, newName);
    }
}
