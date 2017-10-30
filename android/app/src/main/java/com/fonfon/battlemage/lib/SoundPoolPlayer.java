package com.fonfon.battlemage.lib;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.util.SparseIntArray;

public final class SoundPoolPlayer {

  private SparseIntArray sounds;
  private SoundPool pool;

  public SoundPoolPlayer() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      createNewSoundPool();
    } else {
      createOldSoundPool();
    }
    sounds = new SparseIntArray();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private void createNewSoundPool() {
    AudioAttributes attributes = new AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build();
    pool = new SoundPool.Builder()
        .setAudioAttributes(attributes)
        .build();
  }

  @SuppressWarnings("deprecation")
  private void createOldSoundPool() {
    pool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
  }

  public void setSoundList(@NonNull Context context, @NonNull int[] soundList) {
    for (Integer soundId : soundList) {
      sounds.put(soundId, pool.load(context, soundId, 1));
    }
  }

  public void play(@RawRes int soundResId) {
    pool.play(sounds.get(soundResId), 1, 1, 0, 0, 1);
  }
}
