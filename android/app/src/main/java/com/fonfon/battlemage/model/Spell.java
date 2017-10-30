package com.fonfon.battlemage.model;

import com.fonfon.battlemage.App;
import com.fonfon.battlemage.lib.wand.Move;

import java.util.Random;

public class Spell {

  public SpellPart p0;
  public SpellPart p1;
  public SpellPart p2;

  public Spell(SpellPart p0, SpellPart p1, SpellPart p2) {
    this.p0 = p0;
    this.p1 = p1;
    this.p2 = p2;
  }

  @Override
  public String toString() {
    return p0.name + " " + p1.name + " " + p2.name;
  }

  public static Tree<SpellMove> getSpellTree() {
    Random random = new Random(App.getApp().user.randomValue);
    Tree<SpellMove> tree = new Tree<>(new SpellMove(new Move(Move.NONE, Move.NONE, Move.NONE), null));

    for (Move move : Move.movs) {
      SpellPart p0 = App.getApp().p0.get(random.nextInt(App.getApp().p0.size()));
      SpellPart p1 = App.getApp().p1.get(random.nextInt(App.getApp().p1.size()));
      SpellPart p2 = App.getApp().p2.get(random.nextInt(App.getApp().p2.size()));
      tree.addLeaf(new SpellMove(move, new Spell(p0, p1, p2)));
    }

    for (Tree<SpellMove> spellThree : tree.getSubTrees()) {
      generateSubtree(spellThree, 1, random);
    }
    return tree;
  }

  public static void generateSubtree(Tree<SpellMove> tree, int level, Random random) {
    if (level < 5) {
      for (Move move : Move.movs) {
        if (move != tree.getHead().move) {
          SpellPart p0 = App.getApp().p0.get(random.nextInt(App.getApp().p0.size()));
          SpellPart p1 = App.getApp().p1.get(random.nextInt(App.getApp().p1.size()));
          SpellPart p2 = App.getApp().p2.get(random.nextInt(App.getApp().p2.size()));
          tree.addLeaf(new SpellMove(move, new Spell(p0, p1, p2)));
        }
      }
      for (Tree<SpellMove> spellThree : tree.getSubTrees()) {
        generateSubtree(spellThree, level + 1, random);
      }
    }
  }

}
