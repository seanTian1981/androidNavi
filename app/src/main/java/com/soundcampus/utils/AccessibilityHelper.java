package com.soundcampus.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import java.util.Locale;

public class AccessibilityHelper {
    private static final String TAG = "AccessibilityHelper";
    private TextToSpeech tts;
    private boolean isInitialized = false;
    private Context context;

    public AccessibilityHelper(Context context) {
        this.context = context;
        initializeTts();
    }

    private void initializeTts() {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.CHINESE);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Chinese language not supported");
                } else {
                    isInitialized = true;
                    tts.setSpeechRate(0.9f);
                    tts.setPitch(1.0f);
                }
            } else {
                Log.e(TAG, "TTS initialization failed");
            }
        });

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.d(TAG, "Speech started: " + utteranceId);
            }

            @Override
            public void onDone(String utteranceId) {
                Log.d(TAG, "Speech completed: " + utteranceId);
            }

            @Override
            public void onError(String utteranceId) {
                Log.e(TAG, "Speech error: " + utteranceId);
            }
        });
    }

    public void speak(String text) {
        if (isInitialized && text != null && !text.isEmpty()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(System.currentTimeMillis()));
        } else {
            Log.w(TAG, "TTS not initialized or text is empty");
        }
    }

    public void speakQueued(String text) {
        if (isInitialized && text != null && !text.isEmpty()) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, String.valueOf(System.currentTimeMillis()));
        }
    }

    public void stop() {
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
        }
    }

    public boolean isSpeaking() {
        return tts != null && tts.isSpeaking();
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            isInitialized = false;
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}
