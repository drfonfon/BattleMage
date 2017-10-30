package com.fonfon.battlemage.model;

import android.content.Context;
import android.content.SharedPreferences;

public class User {

  public String name;
  public Long level = 0L;
  public Long exp = 0L;
  public Long randomValue = System.nanoTime();
  public String id;

  public User() {

  }

  public static void save(Context context, User user) {
    SharedPreferences preferences = context.getSharedPreferences("ShPrefsMg", Context.MODE_PRIVATE);
    preferences.edit()
        .putString("id", user.id)
        .putString("name", user.name)
        .putLong("level", user.level)
        .putLong("exp", user.exp)
        .putLong("rand", user.randomValue).apply();
  }

  public static User load(Context context) {
    SharedPreferences p = context.getSharedPreferences("ShPrefsMg", Context.MODE_PRIVATE);
    User user = new User();
    user.id = p.getString("id", null);
    user.name = p.getString("name", null);
    user.level = p.getLong("level", 0L);
    user.exp = p.getLong("exp", 0L);
    user.randomValue = p.getLong("rand", 0L);
    return user;
  }
}
