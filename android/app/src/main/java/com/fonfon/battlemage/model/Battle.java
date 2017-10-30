package com.fonfon.battlemage.model;

public class Battle {

  public String p1;
  public String p2;

  public int p1H = 20;
  public int p2H = 20;

  public int p1p0 = -1;
  public int p1p1 = -1;
  public int p1p2 = -1;

  public int p2p0 = -1;
  public int p2p1 = -1;
  public int p2p2 = -1;

  public int turn = 1;

  public boolean isActive = false;

  public Battle() {
  }
}
