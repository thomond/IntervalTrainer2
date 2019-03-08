package joqu.intervaltrainer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Entity
public class Interval {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;
    public int type; // Warm-up || Run || Walk
    public String parameters; // length(seconds) || Distance (m) , minimum speed (km/h)
    public int step;
    @ColumnInfo(name = "template_id")
    public int templateID;
    @Ignore
    public Interval(@NonNull int id, int type, String parameters, int templateID) {
        this.id = id;
        this.type = type;
        this.parameters = parameters;
        this.templateID = templateID;
    }

    public Interval( int type, String parameters, int templateID,int step) {
        this.type = type;
        this.parameters = parameters;
        this.templateID = templateID;
    }

    // Get a interval obj from a JSON string
    public static Interval fromJSON(String intervalJSON) {
        try {
            JSONObject obj = new JSONObject(intervalJSON);
            return new Interval(obj.getInt("type"),
                    obj.getString("parameters"),
                    obj.getInt("templateID"),
                    obj.getInt("step"));
        } catch (JSONException e) {
            Log.e("",e.toString());
            return null;
        }
    }

    // Retrive a JSON string for this class
    public String toJSON() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type",this.type);
            obj.put("parameters",this.parameters);
            obj.put("templateID",this.templateID);
            obj.put("step",this.step);
            return obj.toString();
        } catch (JSONException e) {
            Log.e("",e.toString());
            return null;
        }
    }
    
}
