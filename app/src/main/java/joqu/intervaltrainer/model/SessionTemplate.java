package joqu.intervaltrainer.model;

import android.os.AsyncTask;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import joqu.intervaltrainer.model.entities.Interval;
import joqu.intervaltrainer.model.entities.Template;

import static joqu.intervaltrainer.Const.TAG;

/**
 * Class to encapsulate data entities for a session template
 *
 */
public class SessionTemplate{

    private Template mTemplate;
    private List<Interval> mTemplateIntervals;
    private Interval mCurrentInterval;

    public SessionTemplate(){mTemplate = null; mTemplateIntervals = new LinkedList<>(); }

    public SessionTemplate(String name, String type, String description) {
        this();
        init(name,type,description);
    }

    public void init(String name, String type, String description){
        mTemplate = new Template(name,type,description);

    }

    public void addInterval(int type, int unitdata, int step){
        Interval interval = new Interval(type,unitdata,mTemplate.id,step);
        mTemplate.time += unitdata; // Update template values
        mTemplateIntervals.add(interval);
    }

    public void initInterval(){
        return;
    }

    public List<Interval> getIntervals() {
        return mTemplateIntervals;
    }

    public void getFromDB(final AppDao dao, final int id){
        // FIXME:  tthis may leak, make static
        try {
            new AsyncTask(){
                @Override
                protected Void doInBackground(Object[] objects) {
                    mTemplate = dao.getTemplateById(id);
                    mTemplateIntervals = dao.getIntervalsTemplateById(id);
                    if (mTemplate!=null) Log.d(TAG,"Read template from DB: "+mTemplate.name);
                    Log.d(TAG,"Read " + mTemplateIntervals.size() + " Interval records");

                    return null;
                }
            }.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getFromDB(final AppDao dao, final String name)  {
        try {
            new AsyncTask(){

                @Override
                protected Void doInBackground(Object[] objects) {
                    mTemplate = dao.getTemplateByName(name);
                    if(mTemplate==null){
                        Log.e(TAG,"Could nt read from Database");

                    }else {
                        mTemplateIntervals = dao.getIntervalsTemplateById(mTemplate.id);
                        if (mTemplate != null) Log.d(TAG, "Read template from DB: " + mTemplate.name);
                        Log.d(TAG, "Read " + mTemplateIntervals.size() + " Interval records");
                    }
                    return null;
                }
            }.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String print(){
        StringBuffer buff = new StringBuffer();
        buff.append(mTemplate.name+"\n");
        buff.append(mTemplate.description+"\n");
        buff.append(mTemplate.type+"\n");
        buff.append(mTemplate.distance + " " + mTemplate.time+"\n");
        buff.append("Intervals:"+"\n");
        for (Interval i :
                mTemplateIntervals) {
            buff.append(i.id+"   ");
            buff.append(i.type+"\t");
        }
        buff.append("\n");



        return buff.toString();
    }


    public boolean hasTemplate(){
        if (mTemplate==null) return false;
        else return true;

    }

    public int getId() {
        return mTemplate.id;
    }

    public Interval getInterval(int index) {
        Log.i(TAG,"Index: " + index + "Step: " + mTemplateIntervals.get(index).step);
        return mTemplateIntervals.get(index);

    }

    public boolean saveAll(AppDao dao) throws InterruptedException {
        //new AppDatabase.InsertAsyncTask(dao).execute(mTemplate, mTemplateIntervals);
        Long rows=0l;
        try {
             rows = new AppDatabase.InsertAsyncTask(dao).execute(mTemplate, mTemplateIntervals).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //while(task.getStatus()!=AsyncTask.Status.FINISHED) Thread.sleep(500);
        Log.i(TAG, rows + " written");
        return true;
    }


    public boolean readAll(AppDao dao) throws InterruptedException {
        AsyncTask task = new AppDatabase.SelectAsyncTask(dao).execute(mTemplateIntervals);
       // while(task.getStatus()!=AsyncTask.Status.FINISHED) Thread.sleep(500);

        task = new AppDatabase.SelectAsyncTask(dao).execute(mTemplate);
        //while(task.getStatus()!=AsyncTask.Status.FINISHED) Thread.sleep(500);
        Log.i(TAG, "data read");
        return true;
    }

    public String getName() {
        return mTemplate.name;
    }
}
