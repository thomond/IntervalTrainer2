package joqu.intervaltrainer;

import androidx.annotation.NonNull;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    @NonNull
    public static String getDateStr() {
        return String.valueOf(new Date().getTime());
    }
    @NonNull
    public static long getDateLong() {
        return new Date().getTime();
    }
    @NonNull
    public static String getDateStr(String format) {
        return (new SimpleDateFormat(format, Locale.getDefault()).format(new Date().getTime()));
    }

    public static String millisBetween(long start, long end){
        try {
            long timeLength = end - start;

            return String.valueOf(timeLength);
        }catch (Exception e){
            Log.e(Const.TAG, e.toString());
            return "NaN";
        }
    }

    public static String millisToTimeFormat(Long millis,String format){
        //return millisToTimeFormat(String.valueOf(millis),format);
        try {

            String formattedTime = new SimpleDateFormat(format, Locale.getDefault()).format(millis);
            return formattedTime;
        }catch (Exception e){
            Log.e(Const.TAG, e.toString());
            return "NaN";
        }
    }

    public static String millisToTimeFormat(String millis,String format){
        try {

            String formattedTime = new SimpleDateFormat(format, Locale.getDefault()).format(new Date(millis));
            return formattedTime;
        }catch (Exception e){
            Log.e(Const.TAG, e.toString());
            return "NaN";
        }
    }

}
