package com.fonfon.battlemage.lib;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;

public class VibroLoop {

  private Vibrator vibrator;
  private int vibro = 0;
  private int pauses = 0;
  private int maxCount = 0;

  private Handler handler;
  private Runnable runnable = new Runnable() {
    @Override
    public void run() {
      vibrator.vibrate(vibro);
      maxCount--;
      if (maxCount > 0) {
        handler.postDelayed(runnable, pauses);
      } else {
        stop();
      }
    }
  };

  public VibroLoop(Context context) {
    this.handler = new Handler();
    vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
  }

  public void start(int vibro, int pauses, int count) {
    this.vibro = vibro;
    this.pauses = pauses;
    this.maxCount = count;
    handler.post(runnable);
  }

  public void stop() {
    handler.removeCallbacks(runnable);
  }
}
