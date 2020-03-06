package joqu.intervaltrainer.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.Random;

@Entity
(foreignKeys = {
@ForeignKey(
        entity = Template.class,
        parentColumns = "id",
        childColumns = "template_id"
)
})
public class Interval {
    @PrimaryKey//(autoGenerate = true)
    @NonNull
    public int id;
    public int type; // Warm-up || Run || Walk
    public long time; // length(seconds) of interval in seconds
    public int distance; // Distance to trvael in metres
    public String unitType; // Type of unit used by interval; time, distance etc.
    public int step;
    @ColumnInfo(name = "template_id")
    public int templateID;

    public Interval(int type, long time, int templateID, int step) {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.type = type;
        this.time = time;
        this.templateID = templateID;
        this.step = step;
    }
    @Ignore
    public Interval(int id, int type, long time, int templateID, int step) {
        this.id = id;
        this.type = type;
        this.time = time;
        this.templateID = templateID;
    }
}
