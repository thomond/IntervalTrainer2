package joqu.intervaltrainer.model.entities;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
    // TODO: move location data into intervaldata from session
    @ColumnInfo(name = "location_data")
    public String locationData;
    public long started;
    public long ended;
    public float distance;



    @Ignore
    private Location mLastLocation =null;
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

    public void addLocation(Location location) {
        StringBuilder s = new StringBuilder();
        if(locationData==null) locationData="";
        locationData += s.append(location.getLatitude()).append(',').append(location.getLongitude()).append(';').toString();

        // Calculate distance travelled
        if (mLastLocation != null)
            distance  += location.distanceTo(mLastLocation);
        // add unique speed to list to calculate total average
        addSpeed(location.getSpeed());
        mLastLocation = location;
    }

    public List<Location> getLocations(){
        LinkedList<Location> locList = new LinkedList<>();
        if (locationData == null || locationData.isEmpty())
            return Collections.EMPTY_LIST;
        for (String substring :
                locationData.split(";")) {
            String points[] = substring.split(",");
            if (points.length == 2) {
                Location l = new Location("none");
                l.setLatitude(Double.valueOf(points[0]));
                l.setLongitude(Double.valueOf(points[1]));
                locList.add(l);
            }
            else return null;
        }
        return locList;
    }

    public void addLocations(String locations) {
        locationData = locations;
    }
}
