package joqu.intervaltrainer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class IntervalData {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;
    @ColumnInfo(name = "interval_id")
    int intervalId;
    @ColumnInfo(name = "session_id")
    int sessionId;
    String data; //max speed, avg speed, distance , time taken , geodata, Datetime started, Datetime ended
}
