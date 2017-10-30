package com.fonfon.battlemage.ui;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.fonfon.battlemage.App;
import com.fonfon.battlemage.R;
import com.fonfon.battlemage.databinding.ActivityMainBinding;
import com.fonfon.battlemage.model.SpellPart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding binding;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    binding.textName.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));

    binding.buttonTraining.setOnClickListener(v -> {
      if (App.getApp().spellTree != null) {
        startActivity(new Intent(MainActivity.this, MagicActivity.class));
      } else {
        Toast.makeText(this, "Ваше дерево заклинаний еще не готово!", Toast.LENGTH_SHORT).show();
      }
    });

    binding.buttonBattleCreate.setOnClickListener(v -> {
      if (App.getApp().spellTree != null) {
        startActivity(new Intent(MainActivity.this, CreateBattleActivity.class).putExtra("isCreator", true));
      } else {
        Toast.makeText(this, "Ваше дерево заклинаний еще не готово!", Toast.LENGTH_SHORT).show();
      }
    });

    binding.buttonBattleConnect.setOnClickListener(v -> {
      if (App.getApp().spellTree != null) {
        startActivity(new Intent(MainActivity.this, CreateBattleActivity.class).putExtra("isCreator", false));
      } else {
        Toast.makeText(this, "Ваше дерево заклинаний еще не готово!", Toast.LENGTH_SHORT).show();
      }
    });

    setfont(binding.textName);
    setfont(binding.buttonBattleConnect);
    setfont(binding.buttonBattleCreate);
    setfont(binding.buttonTraining);

    getSpellThree();


  }

  @Override
  protected void onResume() {
    super.onResume();
    if (App.getApp().user.id == null) {
      startActivity(new Intent(MainActivity.this, LoginActivity.class));
    } else {
      binding.textName.setText(App.getApp().user.name);
      getSpellThree();
    }
    Dexter.withActivity(this).withPermission(Manifest.permission.RECORD_AUDIO)
        .withListener(new PermissionListener() {
          @Override
          public void onPermissionGranted(PermissionGrantedResponse response) {

          }

          @Override
          public void onPermissionDenied(PermissionDeniedResponse response) {
            finish();
          }

          @Override
          public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

          }
        }).check();
  }

  private void getSpellThree() {
    FirebaseDatabase.getInstance().getReference().child("spells").addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        App.getApp().p0 = getPart(dataSnapshot.child("p0"));
        App.getApp().p1 = getPart(dataSnapshot.child("p1"));
        App.getApp().p2 = getPart(dataSnapshot.child("p2"));
        App.getApp().generateTree();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        finish();
      }
    });
  }

  private ArrayList<SpellPart> getPart(DataSnapshot snapshot) {
    ArrayList<SpellPart> spellParts = new ArrayList<>();
    for (DataSnapshot child : snapshot.getChildren()) {
      spellParts.add(child.getValue(SpellPart.class));
    }
    return spellParts;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding.unbind();
  }

  private void setfont(TextView textView) {
    textView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));
  }
}
