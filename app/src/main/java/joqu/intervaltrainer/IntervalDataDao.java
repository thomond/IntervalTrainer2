package joqu.intervaltrainer;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import joqu.intervaltrainer.IntervalData;

@Dao
public interface IntervalDataDao {
    @Query("select * from intervalData where interval_id=:id")
    public LiveData<List<IntervalData>> getIntervalDataById(int id);

    @Query("select * from intervalData where session_id=:id")
    public LiveData<List<IntervalData>> getIntervalDataBySessionId(int id);

    @Insert
    public void SaveIntervalData(IntervalData mIntervalData);

    @Delete
    public void DeleteIntervalData(IntervalData mIntervalData);
}
