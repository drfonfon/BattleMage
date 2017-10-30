package com.fonfon.battlemage.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.fonfon.battlemage.App;
import com.fonfon.battlemage.R;
import com.fonfon.battlemage.databinding.ActivityCreatebattleBinding;
import com.fonfon.battlemage.model.Battle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateBattleActivity extends AppCompatActivity {

  private ValueEventListener creatorListener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      if (dataSnapshot.exists()) {
        ref.child(pinCode + "p").child("isActive").setValue(true);
        startActivity(new Intent(CreateBattleActivity.this, MagicActivity.class)
            .putExtra("isCreator", true)
            .putExtra("battlePin", pinCode + "p"));
        finish();
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

  private ValueEventListener cpnnectionListenet = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      if (dataSnapshot.exists()) {
        Boolean isActive = (Boolean) dataSnapshot.getValue();
        if (isActive != null) {
          if (isActive) {
            startActivity(new Intent(CreateBattleActivity.this, MagicActivity.class)
                .putExtra("isCreator", false)
                .putExtra("battlePin", pin.get() + "p"));
            finish();
          } else {
            ref.child(pin.get() + "p").child("p2").setValue(App.getApp().user.id);
          }
        }
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
      finish();
    }
  };

  private Battle battle;
  private ActivityCreatebattleBinding binding;

  public ObservableField<String> pin = new ObservableField<>("");
  DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("battles");
  long pinCode = -1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_createbattle);
    binding.setActivity(this);

    if (getIntent().getBooleanExtra("isCreator", false)) {
      binding.textCode.setVisibility(View.GONE);
      create();
    } else {
      binding.text.setText("Введите пинкод битвы");
    }

    binding.textCode.setOnEditorActionListener((v, actionId, event) -> actionId == EditorInfo.IME_ACTION_SEND && connect());
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding.unbind();
    ref.removeEventListener(creatorListener);
    ref.removeEventListener(cpnnectionListenet);
  }

  private boolean connect() {
    hideKeyboardFrom(this, binding.textCode);
    ref.child(pin.get() + "p").child("isActive").addValueEventListener(cpnnectionListenet);
    return true;
  }

  private void create() {
    battle = new Battle();
    battle.p1 = App.getApp().user.id;

    ref.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        pinCode = dataSnapshot.getChildrenCount() + 1;
        binding.text.setText("Пин-код битвы " + pinCode);
        ref.child(pinCode + "p").setValue(battle);
        ref.child(pinCode + "p").child("p2").addValueEventListener(creatorListener);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        finish();
      }
    });
  }

  public static void hideKeyboardFrom(Context context, View view) {
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    if(imm != null) {
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

}
