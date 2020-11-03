package com.example.leonardo.watermeter.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by Administrator on 2018/1/24 0024.
 */

public class VolumeManage {
    private static AudioManager audioManager;

    public static void SetVolume(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_PLAY_SOUND);
    }
}
