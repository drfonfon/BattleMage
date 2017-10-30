package com.fonfon.battlemage.model;

import com.fonfon.battlemage.lib.wand.Move;

public  class SpellMove {
  public Move move;
  public Spell spell;

  public SpellMove(Move move, Spell spell) {
    this.move = move;
    this.spell = spell;
  }

  @Override
  public String toString() {
    if(spell != null) {
      return move.toString() + " -> " + spell.toString();
    } else {
      return move.toString();
    }
  }
}