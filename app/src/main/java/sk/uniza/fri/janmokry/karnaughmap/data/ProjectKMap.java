package sk.uniza.fri.janmokry.karnaughmap.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import java.io.Serializable;

import static sk.uniza.fri.janmokry.karnaughmap.data.ProjectKMap.COLUMN_PROJECT_ID;
import static sk.uniza.fri.janmokry.karnaughmap.data.ProjectKMap.COLUMN_TITLE;

/**
 * Created by Janci on 31.03.2018.
 *
 * Immutable. Holds data for KMap linked to ProjectInfo via projectId.
 */
@Entity(tableName = ProjectKMap.TABLE_NAME,
        primaryKeys = { COLUMN_PROJECT_ID, COLUMN_TITLE })
public class ProjectKMap implements Serializable {

    public static final String TABLE_NAME = "project_kmaps";
    public static final String COLUMN_PROJECT_ID = "project_id";
    public static final String COLUMN_TITLE = "name";
    public static final String COLUMN_GSON_DATA = "gson_data";

    @ColumnInfo(index = true, name = COLUMN_PROJECT_ID)
    public final long projectId;

    /** Title of a KMap */
    @NonNull
    @ColumnInfo(name = COLUMN_TITLE)
    public final String title;

    @ColumnInfo(name = COLUMN_GSON_DATA)
    public final String gsonData;

    public ProjectKMap(long projectId, @NonNull String title, String gsonData) {
        this.projectId = projectId;
        this.title = title;
        this.gsonData = gsonData;
    }
}
