package com.fonfon.battlemage.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ShutterView extends View {

  public ShutterView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  public void animateView(int startDuration, int endDuration, int loops) {
    setVisibility(View.VISIBLE);
    setAlpha(0.f);

    ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
    alphaInAnim.setDuration(startDuration * 100);

    ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
    alphaOutAnim.setDuration(endDuration * 100);

    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
    animatorSet.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        setVisibility(View.GONE);
        if ((loops - 1) > 0) {
          animateView(startDuration, endDuration, loops - 1);
        }
      }
    });
    animatorSet.start();
  }
}