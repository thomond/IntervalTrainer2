package joqu.intervaltrainer.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import joqu.intervaltrainer.model.entities.Interval;
import joqu.intervaltrainer.model.entities.IntervalData;
import joqu.intervaltrainer.model.entities.Session;
import joqu.intervaltrainer.model.entities.Template;

@Dao
public interface AppDao
{
    ////// Session access


    @Query("select * from session where id=:id")
    public Session getSessionById(int id);

    @Query("select * from session")
    public List<Session> getAllSessions();

    @Query("delete from session")
    public void deleteSessions();

    @Insert
    public long addSession(Session session);

    @Delete
    public void deleteSession(Session session);


    ////// Interval Data Access
   // @Query("select * from intervalData where interval_id=:id")
    //public List<IntervalData> getIntervalDataById(int id);

    @Query("select * from intervalData where session_id=:id")
    public List<IntervalData> getIntervalDataBySessionId(int id);

    @Insert
    public long addIntervalData(IntervalData mIntervalData);

    @Delete
    public void deleteIntervalData(IntervalData mIntervalData);


    ////// Template access //////
    @Query("select * from template where id=:id")
    public Template getTemplateById(int id);

    @Query("select * from template where name=:name")
    public Template getTemplateByName(String name);

    //@Query("select interval_ids from template")
    //public int[] getIntervalsById(int id);

    @Insert
    public long addTemplate(Template template);

    @Delete
    public void DeleteTemplate(Template template);


    ////// Interval access /////
    @Query("select * from interval where id=:id")
    public Interval getIntervalById(int id);

    ////// Interval access /////
    @Query("select * from interval where template_id=:id")
    public List<Interval> getIntervalsTemplateById(int id);

    @Query("select type from interval where :id=id")
    public int getTypeById(int id);



    @Insert
    public long  addInterval(Interval interval);

    @Delete
    public void deleteInterval(Interval interval);

    @Query("select * from template")
    List<Template> getAllTemplates();

    @Query("select * from interval")
    List<Interval> getAllIntervals();


    @Query("select * from intervalData")
    List<IntervalData> getAllIntervalData();

}
