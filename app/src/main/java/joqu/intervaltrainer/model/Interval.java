package joqu.intervaltrainer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Interval {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;
    public int type; // Warm-up || Run || Walk
    public String parameters; // length(seconds) || Distance (m) , minimum speed (km/h)
    @ColumnInfo(name = "template_id")
    public int templateID;
    @Ignore
    public Interval(@NonNull int id, int type, String parameters, int templateID) {
        this.id = id;
        this.type = type;
        this.parameters = parameters;
        this.templateID = templateID;
    }

    public Interval( int type, String parameters, int templateID) {
        this.type = type;
        this.parameters = parameters;
        this.templateID = templateID;
    }

}
