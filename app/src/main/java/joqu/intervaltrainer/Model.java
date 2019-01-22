package joqu.intervaltrainer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;

import java.util.List;
/**
 * Template:
 * Id
 * Interval_IDs[]
 * Persist() => SQLite DB
 * Load()  <= DB
 * <p>
 * Interval:
 * id
 * Type: Warm-up || Run || Walk
 * Parameters: length(seconds) || Distance (m) , minimum speed (km/h)
 * Persist() => SQLite DB
 * Load()  <= DB
 * <p>
 * Interval_data:
 * Id
 * Interval_id
 * Session_id
 * Data: max speed, avg speed, distance , time taken , geodata, Datetime started, Datetime ended
 * Persist() => SQLite DB
 * Load()  <= DB
 * <p>
 * Session:
 * Id
 * Template_ID
 * Datetime started
 * Datetime ended
 * Data: max speed, avg speed, distance , time taken , geodata
 * Persist() => SQLite DB
 * Load()  <= DB
 * TODO:
 * Add autokeys for Entity ids
 * Add sanity checking for data strings
 * Add getters/setters as needed
 * Add LiveData to returned Lists for eahc entity
 */

/** TODO:
 Add autokeys for Entity ids
 Add sanity checking for data strings
 Add getters/setters as needed
 Add LiveData to returned Lists for eahc entity

 */


/**
 Model data containing classes for data Entities in DB and DAO for access in program
 Annotations docs: https://developer.android.com/topic/libraries/architecture/room.html
 */
public class Model {

    @Entity
    public class Template {
        @PrimaryKey
        int id;

        @ColumnInfo(name = "interval_ids")
        int[] intervalIds;
    }

    @Dao
    public interface TemplateDao {
        @Query("select * from template where id=:id")
        public Template getTemplateById(int id);

        @Query("select interval_ids from template")
        public int[] getIntervalsById(int id);

        @Insert
        public void SaveTemplate(Template template);

        @Delete
        public void DeleteTemplate(Template template);
    }

    @Entity
    public class Interval {
        @PrimaryKey
        int id;
        int type; // Warm-up || Run || Walk
        String parameters; // length(seconds) || Distance (m) , minimum speed (km/h)

    }

    @Dao
    public interface IntervalDao {
        @Query("select * from interval where id=:id")
        public Interval getIntervalById(int id);

        @Query("select type from interval where :id=id")
        public int getTypeById(int id);

        @Query("select parameters from interval where :id=id")
        public String getParametersById(int id);

        @Insert
        public void SaveInterval(Interval interval);

        @Delete
        public void DeleteInterval(Interval interval);
    }


}

