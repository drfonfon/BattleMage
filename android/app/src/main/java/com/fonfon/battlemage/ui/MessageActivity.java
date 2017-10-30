package com.fonfon.battlemage.ui;

import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.fonfon.battlemage.R;
import com.fonfon.battlemage.databinding.ActivityMessageBinding;

public class MessageActivity extends AppCompatActivity {

  private ActivityMessageBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_message);
    setfont(binding.message);

    if (getIntent().getBooleanExtra("looze", false)) {
      binding.message.setText("Это фиаско, братан!");
    } else {
      binding.message.setText("Победа! Ты крут!");
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding.unbind();
  }

  private void setfont(TextView textView) {
    textView.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/font.ttf"));
  }
}
