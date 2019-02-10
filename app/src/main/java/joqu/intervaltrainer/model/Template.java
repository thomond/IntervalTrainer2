package joqu.intervaltrainer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Template {
    @PrimaryKey
    int id;

    //@ColumnInfo(name = "interval_ids")
    //int[] intervalIds;
}
