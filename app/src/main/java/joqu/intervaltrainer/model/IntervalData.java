package joqu.intervaltrainer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class IntervalData {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;
    @ColumnInfo(name = "interval_id")
    public int intervalId;
    @Ignore
    public IntervalData(@NonNull int id, int intervalId, int sessionId, String data) {
        this.id = id;
        this.intervalId = intervalId;
        this.sessionId = sessionId;
        this.data = data;
    }

    @ColumnInfo(name = "session_id")
    public int sessionId;
    public String data; //max speed, avg speed, distance , time taken , geodata, Datetime started, Datetime ended
    public String started;
    public String ended;
    public int distance;
    @ColumnInfo(name = "avg_speed")
    public int avgSpeed;
    @ColumnInfo(name = "fastest_speed")
    public int fastestSpeed;
    public int pace;

    @Ignore
    public IntervalData(int intervalId, int sessionId, String data) {
        this.intervalId = intervalId;
        this.sessionId = sessionId;
        this.data = data;
    }
    @Ignore
    public IntervalData(int intervalId, int sessionId) {
        this.intervalId = intervalId;
        this.sessionId = sessionId;
    }

    IntervalData(){}
}
