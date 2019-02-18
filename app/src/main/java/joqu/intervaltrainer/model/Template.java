package joqu.intervaltrainer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Template {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;
    public String name;
    public String type;
    public String description;

    @Ignore
    public Template(@NonNull int id, String name, String type, String description) {
        this.id = id;
        this.type = type;
        this.description = description;
    }

    public Template(String name, String type, String description) {
        this.type = type;
        this.description = description;
    }
}
