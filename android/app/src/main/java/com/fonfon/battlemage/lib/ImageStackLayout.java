package com.fonfon.battlemage.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.graphics.Color;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fonfon.battlemage.R;

@BindingMethods(@BindingMethod(type = ImageStackLayout.class, attribute = "count", method = "setCount"))
public final class ImageStackLayout extends LinearLayout {

  private int count = 0;
  private int maxCount = 0;
  private int imageRef = 0;

  public ImageStackLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }

  public ImageStackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }

  private void init(AttributeSet attrs) {
    if (attrs != null) {
      TypedArray a = getContext().getTheme().obtainStyledAttributes(
          attrs,
          R.styleable.ImageStackLayout,
          0, 0);

      try {
        maxCount = a.getInteger(R.styleable.ImageStackLayout_isl_maxCount, 0);
        count = a.getInteger(R.styleable.ImageStackLayout_isl_count, 0);
        imageRef = a.getResourceId(R.styleable.ImageStackLayout_isl_image, 0);
      } finally {
        a.recycle();
      }
    }
    for (int i = 0; i < maxCount; i++) {
      AppCompatImageView iv = new AppCompatImageView(getContext());
      iv.setBackgroundColor(Color.TRANSPARENT);
      LayoutParams params = new LayoutParams(
          ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT);
      iv.setPadding(0,0,0,8);
      iv.setLayoutParams(params);
      VectorDrawableCompat drawable = VectorDrawableCompat.create(getContext().getResources(), imageRef, getContext().getTheme());
      if (drawable != null) {
        iv.setImageDrawable(drawable.mutate());
      }
      addView(iv);
    }
    setCount(count);
  }

  public void setCount(int count) {
    this.count = count <= maxCount ? count : maxCount;
    for (int i = 0; i < getChildCount(); i++) {
      if (i >= count) {
        getChildAt(i).setVisibility(View.GONE);
      } else {
        getChildAt(i).setVisibility(View.VISIBLE);
      }
    }
  }

  public int getCount() {
    return count;
  }
}