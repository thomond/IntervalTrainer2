package joqu.intervaltrainer.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.Random;

import joqu.intervaltrainer.Const;

@Entity
(foreignKeys = {
@ForeignKey(
entity = Interval.class,
parentColumns = "id",
childColumns = "interval_id"
),
@ForeignKey(
entity = Session.class,
parentColumns = "id",
childColumns = "session_id"
)
})
public class IntervalData {
    public int step;
    @PrimaryKey//(autoGenerate = true)
    @NonNull
    public int id;
    @ColumnInfo(name = "interval_id")
    public int intervalId;


    @ColumnInfo(name = "session_id")
    public int sessionId;
    public String data; //max speed, avg speed, distance , time taken , geodata, Datetime started, Datetime ended
    public long started;
    public long ended;
    public float distance;



    @Ignore
    // Collection of speeds collected to be averaged out at finalization
    LinkedList<Float> mSpeeds = new LinkedList<Float>();
    @ColumnInfo(name = "avg_speed")
    public float avgSpeed;
    @ColumnInfo(name = "fastest_speed")
    public float fastestSpeed;
    public float pace;



    @Ignore
    public IntervalData(int intervalId, int sessionId) {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.intervalId = intervalId;
        this.sessionId = sessionId;
    }

    @Ignore
    public IntervalData(int id, int intervalId, int sessionId, long started, long ended) {
        this.id = id;
        this.intervalId = intervalId;
        this.sessionId = sessionId;
        this.started = started;
        this.ended = ended;
    }
    public IntervalData(int intervalId, int sessionId, int step){
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.intervalId = intervalId;
        this.sessionId = sessionId;
        this.step = step;
    }

    @NonNull
    public static String getType(Interval i) {
        String type ="";
        switch(i.type)
        {
            case Const.INTERVAL_TYPE_RUN: type = "\uD83C\uDFC3"; break; // 🏃
            case Const.INTERVAL_TYPE_WALK: type = "\uD83D\uDEB6"; break; // 🚶‍
            case Const.INTERVAL_TYPE_COOLDOWN: type = "\uD83E\uDD75"; break; // hot face
        }
        return type;
    }

    public static String getType(int typeNum) {
        String type ="";
        switch(typeNum)
        {
            case Const.INTERVAL_TYPE_RUN: type = "\uD83C\uDFC3"; break; // 🏃
            case Const.INTERVAL_TYPE_WALK: type = "\uD83D\uDEB6"; break; // 🚶‍
            case Const.INTERVAL_TYPE_COOLDOWN: type = "\uD83E\uDD75"; break; // hot face
        }
        return type;
    }

    @Ignore
    public void addSpeed(float speed) {
        this.mSpeeds.add(speed);
    }

    @Ignore
    public float getAvgSpeed() {
        if (mSpeeds.isEmpty()) return 0;
        float total = 0;
        for (float s : mSpeeds){
            total += s;
        }
        return total/mSpeeds.size();
    }
}
