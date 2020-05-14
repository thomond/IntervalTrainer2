package joqu.intervaltrainer.services;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

import joqu.intervaltrainer.Const;


public class ServiceTTSManager implements TextToSpeech.OnUtteranceCompletedListener {
    public static boolean isInitialized() {
        return isInitialized;
    }
    private static boolean isInitialized = false;

    static TextToSpeech.OnInitListener mTTSListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if(status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.UK);
                isInitialized = true;
                Log.i(Const.TAG,"TTS Initialized");
            }else {
                Log.e(Const.TAG, "TTS Failure");
            }
        }
    };

    public static boolean isSpeaking(){
        if(tts==null) return false;
        return tts.isSpeaking();
    }

    static TextToSpeech tts;

    public static void init(Context context){
        tts = new TextToSpeech(context,mTTSListener);
    }



    public static int speak(CharSequence text,  String utteranceId) {
        try{
            return tts.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId);

        }catch(Exception e){
            Log.e(Const.TAG,e.getMessage());
        }
        return 0;
    }




    public static int stop() {
        try{
            return tts.stop();
        }catch(NullPointerException e){
            Log.e(Const.TAG,"tts not initalised"+e.getMessage());
            return -1;
        }
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {

    }
}
