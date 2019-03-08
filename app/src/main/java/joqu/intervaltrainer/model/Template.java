package joqu.intervaltrainer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

@Entity
public class Template {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;
    public String name;
    public String type;
    public String description;

    @Ignore
    public Template(@NonNull int id, String name, String type, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public Template(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    // Get a template obj from a JSON string
    public static Template fromJSON(String templateJSON) {
        try {
            JSONObject obj = new JSONObject(templateJSON);
            return new Template(obj.getString("name"),
                                obj.getString("type"),
                                obj.getString("description"));
        } catch (JSONException e) {
            Log.e("",e.toString());
            return null;
        }
    }

    // Retrive a JSON string for this class
    public String toJSON() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name",this.name);
            obj.put("type",this.type);
            obj.put("description",this.description);
            return obj.toString();
        } catch (JSONException e) {
            Log.e("",e.toString());
            return null;
        }
    }
}
