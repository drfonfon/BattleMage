package com.fonfon.battlemage.ui;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.fonfon.battlemage.App;
import com.fonfon.battlemage.R;
import com.fonfon.battlemage.databinding.ActivityLoginBinding;
import com.fonfon.battlemage.model.User;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

  private ActivityLoginBinding binding;
  public ObservableField<String> name = new ObservableField<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
    binding.setActivity(this);

    binding.textName.setOnEditorActionListener((v, actionId, event) -> actionId == EditorInfo.IME_ACTION_SEND && setUser());

    setfont(binding.textName);
    setfont(binding.text);
  }

  public static void hideKeyboardFrom(Context context, View view) {
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    if(imm != null) {
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  private boolean setUser() {
    hideKeyboardFrom(this, binding.textName);
    User user = new User();
    user.name = name.get();
    FirebaseDatabase.getInstance().getReference().child("users").push().setValue(user, (databaseError, databaseReference) -> {
      if(databaseError == null) {
        databaseReference.child("id").setValue(databaseReference.getKey());
        user.id = databaseReference.getKey();
        App.getApp().user = user;
        User.save(getApplicationContext(), user);
        finish();
      }
    });
    return true;
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
