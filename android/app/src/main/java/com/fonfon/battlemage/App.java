package com.fonfon.battlemage;

import android.app.Application;

import com.fonfon.battlemage.model.Spell;
import com.fonfon.battlemage.model.SpellMove;
import com.fonfon.battlemage.model.SpellPart;
import com.fonfon.battlemage.model.Tree;
import com.fonfon.battlemage.model.User;

import java.util.ArrayList;

public class App extends Application {

  public ArrayList<SpellPart> p0 = new ArrayList<>();
  public ArrayList<SpellPart> p1 = new ArrayList<>();
  public ArrayList<SpellPart> p2 = new ArrayList<>();

  public Tree<SpellMove> spellTree;

  public int[] soundList = new int[]{
      R.raw.s_0, R.raw.s_1, R.raw.s_2, R.raw.s_3, R.raw.s_4, R.raw.s_5, R.raw.s_6, R.raw.s_7
  };

  public User user = new User();

  private static App app;

  public static App getApp() {
    return app;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    user = User.load(getApplicationContext());
    app = this;
  }

  public void generateTree() {
    spellTree = Spell.getSpellTree();
  }
}
