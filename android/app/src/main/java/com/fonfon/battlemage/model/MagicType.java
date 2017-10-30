package com.fonfon.battlemage.model;

import android.graphics.Color;

import com.fonfon.battlemage.R;

public enum MagicType {

  dark(Color.BLACK, R.raw.s_0, 1, 5, 6, R.drawable.ic_mrak, "мрак"),
  kumar(Color.parseColor("#2196F3"), R.raw.s_1, 1, 1, 2, R.drawable.ic_kumar, "кумар"),
  greed(Color.parseColor("#4CAF50"), R.raw.s_2, 2, 5, 1, R.drawable.ic_travischa, "травища"),
  heel(Color.parseColor("#009688"), R.raw.s_3, 3, 5, 1, R.drawable.ic_zhiza, "жиза"),
  blood(Color.parseColor("#F44336"), R.raw.s_4, 2, 3, 3, R.drawable.ic_krovyaka, "кровяка"),
  porca(Color.parseColor("#9C27B0"), R.raw.s_5, 1, 5, 1, R.drawable.ic_porcha, "порча"),
  ugar(Color.parseColor("#FFEB3B"), R.raw.s_6, 3, 3, 1, R.drawable.ic_ugar, "угар"),
  shiza(Color.WHITE, R.raw.s_7, 1, 1, 3, R.drawable.ic_shiza, "шиза");

  public int color;
  public int sound;
  public int starts;
  public int stops;
  public int loops;
  public int image;
  public String name;

  MagicType(int color, int sound, int starts, int stops, int loops, int image, String name) {
    this.color = color;
    this.sound = sound;
    this.starts = starts;
    this.stops = stops;
    this.loops = loops;
    this.image = image;
    this.name = name;
  }
}
