package joqu.intervaltrainer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity
public class Session {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    int id;
    @ColumnInfo(name = "template_id")
    @NonNull
    int templateId;
    String started;
    String ended;
    String data;
    public Session(int templateId,String started, String ended, String data){
        this.templateId = templateId;
        this.started = started;
        this.ended = ended;
        this.data = data;
    }
}

