package joqu.intervaltrainer.model.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import android.location.Location;
import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Entity
(foreignKeys = {
@ForeignKey(
entity = Template.class,
parentColumns = "id",
childColumns = "template_id"
)})

public class Session {
    @PrimaryKey//(autoGenerate = true)
    @NonNull
    public int id;
    @ColumnInfo(name = "template_id")
    @NonNull
    public int templateId;
    public long started;
    public long ended;
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

    public Session(){this.id = new Random().nextInt(Integer.MAX_VALUE);}

    @Ignore
    public Session(int templateId, long started, long ended, String data){
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.templateId = templateId;
        this.started = started;
        this.ended = ended;
        this.locationData = data;
    }

    @Ignore
    public Session(int id, int templateId, long started, long ended, String data){

        this.id = id;
        this.templateId = templateId;
        this.started = started;
        this.ended = ended;
        this.locationData = data;
    }

    public void addLocation(Location location)
    {
        StringBuilder s = new StringBuilder();
        if(locationData==null) locationData="";
        locationData += s.append(location.getLatitude()).append(',').append(location.getLongitude()).append(';').toString();



    }

    public List<Location> getLocations(){
        LinkedList<Location> locList = new LinkedList<>();
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

    public String getString()
    {
        return new StringBuilder().append("Started: "+started)
                .append("\nEnded:" + ended)
                .append("Locations: "+locationData)
                .toString();
    }

    public void addLocations(String locations) {
        locationData = locations;
    }
}

