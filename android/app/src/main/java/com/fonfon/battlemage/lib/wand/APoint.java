package com.fonfon.battlemage.lib.wand;

import android.hardware.SensorManager;

import java.util.Arrays;

public class APoint {

  private float[] values;
  private long time;

  public APoint(float[] values, long time) {
    this.values = values;
    this.time = time;
    this.values[2] = this.values[2] - SensorManager.GRAVITY_EARTH;

    this.values[0] = this.values[0] * 10000;
    this.values[1] = this.values[1] * 10000;
    this.values[2] = this.values[2] * 10000;
  }

  public long getTime() {
    return time;
  }

  public float[] getValues() {
    return values;
  }

  public void minus(APoint point) {
    if(point == null)
      return;
    values[0] -= point.values[0];
    values[1] -= point.values[1];
    values[2] -= point.values[2];
  }

  public void threshold(int value) {
    values[0] = Math.abs(values[0]) > value ? values[0] : 0;
    values[1] = Math.abs(values[1]) > value ? values[1] : 0;
    values[2] = Math.abs(values[2]) > value ? values[2] : 0;
  }

  @Override
  public String toString() {
    return time + ": " + Arrays.toString(values);
  }

}
