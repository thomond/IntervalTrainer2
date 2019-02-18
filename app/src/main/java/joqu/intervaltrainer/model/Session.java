package joqu.intervaltrainer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


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
    public String data;
    @Ignore
    public Session(@NonNull int id, @NonNull int templateId, String started, String ended, String data) {
        this.id = id;
        this.templateId = templateId;
        this.started = started;
        this.ended = ended;
        this.data = data;
    }

    public Session(int templateId, String started, String ended, String data){
        this.templateId = templateId;
        this.started = started;
        this.ended = ended;
        this.data = data;
    }
}

