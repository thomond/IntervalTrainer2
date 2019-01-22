package joqu.intervaltrainer;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SessionDao{

    @Query("select * from session where id=:id")
    public Session getSessionById(int id);

    @Query("select * from session")
    public LiveData<List<Session>> getAllSessions();

    @Query("delete from session")
    public void deleteAll();

    @Insert
    public long addSession(Session session);

    @Delete
    public void deleteSession(Session session);

}
