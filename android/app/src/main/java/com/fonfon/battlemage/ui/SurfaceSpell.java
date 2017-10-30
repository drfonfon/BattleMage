package com.fonfon.battlemage.ui;

import com.fonfon.battlemage.model.MagicType;
import com.fonfon.battlemage.model.Spell;

import su.levenetc.android.textsurface.Text;
import su.levenetc.android.textsurface.TextBuilder;
import su.levenetc.android.textsurface.TextSurface;
import su.levenetc.android.textsurface.animations.Circle;
import su.levenetc.android.textsurface.animations.Parallel;
import su.levenetc.android.textsurface.animations.Sequential;
import su.levenetc.android.textsurface.animations.ShapeReveal;
import su.levenetc.android.textsurface.contants.Align;
import su.levenetc.android.textsurface.contants.Direction;
import su.levenetc.android.textsurface.contants.Side;
import su.levenetc.android.textsurface.contants.TYPE;

public class SurfaceSpell {

  public static void play(TextSurface textSurface, Spell spell) {
    Text textA = TextBuilder.create(spell.p0.name)
        .setSize(28)
        .setPosition(Align.SURFACE_CENTER)
        .setColor(MagicType.values()[spell.p0.type.intValue()].color)
        .build();
    Text textB = TextBuilder.create(spell.p1.name)
        .setSize(28)
        .setPosition(Align.BOTTOM_OF | Align.CENTER_OF, textA).build();
    Text textC = TextBuilder.create(spell.p2.name)
        .setSize(28)
        .setPosition(Align.BOTTOM_OF| Align.CENTER_OF, textB).build();

    textSurface.reset();
    textSurface.play(TYPE.SEQUENTIAL,
        new Parallel(
            new Sequential(ShapeReveal.create(textA, 500, Circle.show(Side.RIGHT, Direction.OUT), false)),
            new Sequential(ShapeReveal.create(textB, 500, Circle.show(Side.RIGHT, Direction.OUT), false)),
            new Sequential(ShapeReveal.create(textC, 500, Circle.show(Side.RIGHT, Direction.OUT), false))
        )
    );
  }
}
