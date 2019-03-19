package joqu.intervaltrainer;

public interface Const {
    String TAG = "joqu.Intervaltrainer";
    String INTENT_EXTRA_TEMPLATE_ID_INT = "template_id";
    String INTENT_EXTRA_GPS_LAT_DOUBLE = "latitude";
    String INTENT_EXTRA_GPS_LONG_DOUBLE = "longitude";
    String BROADCAST_COUNTDOWN_UPDATE = "joqu.intervaltrainer.COUNTDOWN_UPDATE";
    String BROADCAST_COUNTDOWN_DONE = "joqu.intervaltrainer.BROADCAST_COUNTDOWN_DONE";
    String BROADCAST_GPS_UPDATE = "joqu.intervaltrainer.SVC_UPDATE";
    String BROADCAST_SVC_STOP = "joqu.intervaltrainer.SVC_STOP";
    String BROADCAST_SVC_START = "joqu.intervaltrainer.SVC_START";
    String BROADCAST_SVC_PAUSE = "joqu.intervaltrainer.SVC_PAUSE";
    String INTENT_EXTRA_TIMELEFT_LONG = "millisUntilFinished";
    String BROADCAST_SVC_RESUME = "joqu.intervaltrainer.SVC_RESUME" ;
    String INTENT_EXTRA_DO_TIMER_BOOL = "timerOnly";
    String INTENT_EXTRA_DO_GPS_BOOL = "GPSOnly";
    String INTENT_EXTRA_GPS_DIST_FLOAT = "distance";
    String INTENT_EXTRA_GPS_SPEED_FLOAT = "speed";
    String INTENT_EXTRA_COUNTDOWN_TYPE_INT = "type";
    int MESSAGE_SESSION_DONE = 1;
    String BROADCAST_SVC_STOPPED = "joqu.intervaltrainer.SVC_STOPPED";
    String BROADCAST_SVC_STARTED = "joqu.intervaltrainer.SVC_STARTED";
    String BROADCAST_SVC_PAUSED = "joqu.intervaltrainer.SVC_PAUSED";
}
