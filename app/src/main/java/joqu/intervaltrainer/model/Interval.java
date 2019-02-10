package joqu.intervaltrainer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Interval {
    @PrimaryKey
    int id;
    int type; // Warm-up || Run || Walk
    String parameters; // length(seconds) || Distance (m) , minimum speed (km/h)
    @ColumnInfo(name = "template_id")
    int templateID;
}
