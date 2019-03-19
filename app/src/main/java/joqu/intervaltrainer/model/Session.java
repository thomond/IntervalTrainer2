package joqu.intervaltrainer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationSettingsRequest;

import java.util.LinkedList;
import java.util.List;


@Entity
public class Session {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;
    @ColumnInfo(name = "template_id")
    @NonNull
    public int templateId;
    public String started;
    public String ended;
    public String title;
    public String description;
    public String data;
    @ColumnInfo(name = "location_data")
    public String locationData;
    public float distance;
    @ColumnInfo(name = "avg_speed")
    public int avgSpeed;
    @ColumnInfo(name = "fastest_speed")
    public int fastestSpeed;
    public int pace;

    public Session(){}

    @Ignore
    public Session(@NonNull int id, @NonNull int templateId, String started, String ended, String data) {
        this.id = id;
        this.templateId = templateId;
        this.started = started;
        this.ended = ended;
        this.data = data;
    }
    @Ignore
    public Session(int templateId, String started, String ended, String data){
        this.templateId = templateId;
        this.started = started;
        this.ended = ended;
        this.data = data;
    }
    @Ignore
    public Session(int templateId, String started)
    {
        this.templateId = templateId;
        this.started = started;
    }

    public void addLocation(Location location)
    {
        StringBuilder s = new StringBuilder();

        locationData += s.append(location.getLatitude()).append(',').append(location.getLongitude()).append(';').toString();



    }

    public List<Location> getLocation(){
        LinkedList<Location> locList = new LinkedList<>();
        for (String substring :
                locationData.split(";")) {
            String points[] = substring.split(",");
            if (points.length > 2) {
                Location l = new Location("none");
                l.setLatitude(Double.valueOf(points[0]));
                l.setLongitude(Double.valueOf(points[1]));
                locList.add(l);
            }
            else return null;
        }
        return locList;
    }

    public String getString()
    {
        return new StringBuilder().append("Started: "+started)
                .append("\nEnded:" + ended)
                .append("Locations: "+locationData)
                .toString();
    }
}

