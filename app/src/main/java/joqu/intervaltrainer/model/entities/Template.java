package joqu.intervaltrainer.model.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.Random;

@Entity
public class Template {
    @PrimaryKey//(autoGenerate = true)
    @NonNull
    public int id;
    public String name;
    public String type;
    public String description;
    public long time;
    public long distance;


    public Template(String name, String type, String description) {
        this.id = new Random().nextInt(Integer.MAX_VALUE);
        this.name = name;
        this.type = type;
        this.description = description;
    }
    @Ignore
    public Template(int id, String name, String type, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }
}


