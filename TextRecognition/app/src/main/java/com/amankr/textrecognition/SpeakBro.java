package com.amankr.textrecognition;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class SpeakBro {
    TextToSpeech ttsi;
    public void speakGivenText(String str, Context context){
        ttsi = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    ttsi.setLanguage(Locale.US);
                    ttsi.setSpeechRate(1);
                    ttsi.speak(str,TextToSpeech.QUEUE_ADD,null);
                }
            }
        });
    }

}
