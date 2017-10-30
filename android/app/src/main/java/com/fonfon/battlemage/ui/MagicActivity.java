package com.fonfon.battlemage.ui;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.fonfon.battlemage.App;
import com.fonfon.battlemage.R;
import com.fonfon.battlemage.databinding.ActivityMagicBinding;
import com.fonfon.battlemage.lib.SoundListener;
import com.fonfon.battlemage.lib.SoundPoolPlayer;
import com.fonfon.battlemage.lib.TapView;
import com.fonfon.battlemage.lib.VibroLoop;
import com.fonfon.battlemage.lib.wand.Move;
import com.fonfon.battlemage.lib.wand.Wand;
import com.fonfon.battlemage.model.MagicType;
import com.fonfon.battlemage.model.Spell;
import com.fonfon.battlemage.model.SpellMove;
import com.fonfon.battlemage.model.Tree;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MagicActivity extends AppCompatActivity implements TapView.TapViewListener {

  public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
  UsbManager usbManager;
  UsbDevice device;
  UsbSerialDevice serialPort;
  UsbDeviceConnection connection;

  private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
        boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
        if (granted) {
          connection = usbManager.openDevice(device);
          serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
          if (serialPort != null) {
            if (serialPort.open()) { //Set Serial Connection Parameters.
              serialPort.setBaudRate(9600);
              serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
              serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
              serialPort.setParity(UsbSerialInterface.PARITY_NONE);
              serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
            }
          }
        }
      } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
        onClickStart();
      } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
        if (serialPort != null) {
          serialPort.close();
        }
      }
    }
  };

  private ValueEventListener turnListener = new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
      Long turn = (Long) dataSnapshot.getValue();
      if (turn != null) {
        //p1
        if (turn == 0) {
          if (isCreator) { //you turn
            setYouTurn();
          } else {
            setOpponentTurn();
          }
        } else { //p2
          if (isCreator) {
            setOpponentTurn();
          } else { //you turn
            setYouTurn();
          }
        }
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
  };

  public void onClickStart() {
    HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
    if (!usbDevices.isEmpty()) {
      boolean keep = true;
      for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
        device = entry.getValue();
        int deviceVID = device.getVendorId();
        if (deviceVID == 0x2341) {
          PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
          usbManager.requestPermission(device, pi);
          keep = false;
        } else {
          connection = null;
          device = null;
        }

        if (!keep)
          break;
      }
    }
  }


  private ActivityMagicBinding binding;

  private Wand wand;
  private SoundPoolPlayer soundPoolPlayer;
  private VibroLoop vibroLoop;

  private boolean isCreator;
  private String battlePin;
  private DatabaseReference reference;
  private String you;
  private String opponent;

  private Handler turnHandler = new Handler();
  private Runnable turnEndRunable = () -> {
    reference.child("turn").setValue(isCreator ? 1 : 0);
    reference.child(you + "p0").setValue(-1);
    reference.child(you + "p1").setValue(-1);
    reference.child(you + "p2").setValue(-1);
  };

  private Long attackSpellType0 = -1L;
  private Long attackSpellType1 = -1L;
  private Long attackSpellType2 = -1L;

  private Handler atcHandl = new Handler();
  private Runnable atcRun = new Runnable() {
    @Override
    public void run() {
      if (attackSpellType0 > 0 && attackSpellType1 > 0) {
        MagicType type = MagicType.values()[attackSpellType0.intValue()];
        binding.shutterView.setBackgroundColor(type.color);
        binding.shutterView.animateView(100, 100, 1);
        vibroLoop.start(100, 100, 1);
        reference.child(you + "H").runTransaction(new Transaction.Handler() {
          @Override
          public Transaction.Result doTransaction(MutableData mutableData) {
            Long h = (Long) mutableData.getValue();
            if (h == null) return Transaction.success(mutableData);
            if (attackSpellType1 == 3) {
              Long val = h + attackSpellType2 + 1;
              mutableData.setValue(val > 25 ? 25 : val);
            } else {
              mutableData.setValue(h - attackSpellType2 - 1);
            }
            return Transaction.success(mutableData);
          }

          @Override
          public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

          }
        });
        atcHandl.postDelayed(atcRun, 1000);
      }
    }
  };

  SoundListener soundListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (App.getApp().spellTree == null) {
      finish();
    }

    usbManager = (UsbManager) getSystemService(USB_SERVICE);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_magic);
    wand = new Wand(this);
    binding.tapView.setListener(this);
    soundPoolPlayer = new SoundPoolPlayer();
    soundPoolPlayer.setSoundList(this, App.getApp().soundList);
    vibroLoop = new VibroLoop(this);

    isCreator = getIntent().getBooleanExtra("isCreator", false);
    battlePin = getIntent().getStringExtra("battlePin");

    if (battlePin != null) {
      reference = FirebaseDatabase.getInstance().getReference().child("battles").child(battlePin);
      you = isCreator ? "p1" : "p2";
      opponent = isCreator ? "p2" : "p1";
      setHealthListener();
      checkTurn();
      chechSpell();
    } else {
      binding.progress.setVisibility(View.INVISIBLE);
      binding.opponentLives.setVisibility(View.INVISIBLE);
      binding.imageOpponent.setVisibility(View.INVISIBLE);
      binding.textTurn.setVisibility(View.INVISIBLE);
      binding.t1.setVisibility(View.INVISIBLE);
    }

    setfont(binding.textTurn);
    setfont(binding.t1);
    setfont(binding.t2);

    soundListener = new SoundListener();

    IntentFilter filter = new IntentFilter();
    filter.addAction(ACTION_USB_PERMISSION);
    filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
    filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
    registerReceiver(broadcastReceiver, filter);
  }

  @Override
  protected void onResume() {
    super.onResume();
    wand.startSensorListener();
    atcHandl.post(atcRun);
    onClickStart();
  }

  @Override
  protected void onPause() {
    super.onPause();
    wand.stopSensorListener();
    atcHandl.removeCallbacks(atcRun);
    turnHandler.removeCallbacks(turnEndRunable);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    FirebaseDatabase.getInstance().getReference().removeEventListener(turnListener);
    binding.unbind();
  }

  @Override
  public void onStartTap() {
    wand.startMovesListen();
    soundListener.start();
  }

  @Override
  public void onStopTap() {
    wand.stopMovesListen();
    soundListener.stop();
    List<Move> moves = wand.getMoves();
    if (moves.size() > 0) {
      Tree<SpellMove> cTree = App.getApp().spellTree;
      for (Move move : moves) {
        for (Tree<SpellMove> childThree : cTree.getSubTrees()) {
          if (childThree.getHead().move.eqMove(move)) {
            cTree = childThree;
          }
        }
      }
      Spell spell = cTree.getHead().spell;
      if (spell != null && spell.p0 != null && spell.p0.name != null) {
        MagicType type = MagicType.values()[spell.p0.type.intValue()];
        SurfaceSpell.play(binding.textSurface, spell);
        binding.shutterView.setBackgroundColor(type.color);
        binding.shutterView.animateView(type.starts, type.stops, type.loops);
        soundPoolPlayer.play(type.sound);
        vibroLoop.start(100, 1000, spell.p1.type == 0 ? 1 : 2);
        if (serialPort != null) {
          byte[] bytes = new byte[]{spell.p0.type.byteValue(), (byte) type.starts, (byte) type.stops, (byte) type.loops};
          serialPort.write(bytes);
        }

        int scream = soundListener.getCurrentDecibels() / 10000;

        binding.textDamageScream.setText(String.valueOf(scream));
        binding.info.setVisibility(View.VISIBLE);
        binding.imageMagic.setImageResource(type.image);
        binding.textMagic.setText(type.name);
        binding.textDamage.setText(String.valueOf(spell.p2.type + 1));

        turn(spell, scream);
      }
    }
  }

  private void chechSpell() {
    if (battlePin != null) {
      reference.child(opponent + "p0").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          attackSpellType0 = (Long) dataSnapshot.getValue();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
      reference.child(opponent + "p1").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          attackSpellType1 = (Long) dataSnapshot.getValue();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
      reference.child(opponent + "p2").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          attackSpellType2 = (Long) dataSnapshot.getValue();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
    }
  }

  private void checkTurn() {
    if (battlePin != null) {
      reference.child("turn").addValueEventListener(turnListener);
    }
  }

  private void setOpponentTurn() {
    binding.tapView.setVisibility(View.GONE);
    binding.textTurn.setVisibility(View.VISIBLE);
    binding.progress.setVisibility(View.INVISIBLE);
  }

  private void setYouTurn() {
    binding.tapView.setVisibility(View.VISIBLE);
    binding.textTurn.setVisibility(View.GONE);
    binding.progress.setVisibility(View.VISIBLE);
    binding.progress.setPercent(0);
    binding.progress.setSmoothPercent(100, 5000 * 100);
    turnHandler.postDelayed(turnEndRunable, 5 * 1000);
  }

  private void turn(Spell spell, int scream) {
    if (battlePin != null) {
      reference.child(opponent + "H").runTransaction(new Transaction.Handler() {
        @Override
        public Transaction.Result doTransaction(MutableData mutableData) {
          Long h = (Long) mutableData.getValue();
          reference.child(you + "p0").setValue(spell.p0.type);
          reference.child(you + "p1").setValue(spell.p1.type);
          reference.child(you + "p2").setValue(spell.p2.type);
          reference.child("turn").setValue(isCreator ? 1 : 0);
          attackSpellType0 = -1L;
          attackSpellType1 = -1L;
          attackSpellType2 = -1L;
          turnHandler.removeCallbacks(turnEndRunable);
          if (h == null) return Transaction.success(mutableData);
          if (spell.p0.type == 3) {
            Long val = h + spell.p2.type + 1 + scream;
            mutableData.setValue(val > 25 ? 25 : val);
          } else {
            mutableData.setValue(h - spell.p2.type - 1 - scream);
          }
          return Transaction.success(mutableData);
        }

        @Override
        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

        }
      });
    }
  }

  private void changeLeftImage(int old, int n) {
    if (n < old) {
      binding.imageOpponent.setImageResource(R.drawable.img_head2_left);
    } else {
      binding.imageOpponent.setImageResource(R.drawable.img_face3_left);
    }
    binding.imageOpponent.postDelayed(() -> {
      binding.imageOpponent.setImageResource(R.drawable.img_head1_left);
    }, 500);
  }

  private void changeRightImage(int old, int n) {
    if (n < old) {
      binding.imageMe.setImageResource(R.drawable.img_head2_right);
    } else {
      binding.imageMe.setImageResource(R.drawable.img_face3_left);
    }
    binding.imageMe.postDelayed(() -> {
      binding.imageMe.setImageResource(R.drawable.img_head1_right);
    }, 500);
  }

  private void setHealthListener() {
    if (battlePin != null) {
      reference.child(you + "H").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          int old = binding.youLives.getCount();
          Long val = (Long) dataSnapshot.getValue();
          if (val != null) {
            changeLeftImage(old, val.intValue());
            binding.youLives.setCount(val.intValue());
            if (val <= 0) {
              startActivity(new Intent(MagicActivity.this, MessageActivity.class).putExtra("looze", true));
              reference.child("isActive").setValue(false);
              finish();
            }
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });

      reference.child(opponent + "H").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          int old = binding.opponentLives.getCount();
          Long val = (Long) dataSnapshot.getValue();
          if (val != null) {
            changeRightImage(old, val.intValue());
            binding.opponentLives.setCount(val.intValue());
            if (val <= 0) {
              startActivity(new Intent(MagicActivity.this, MessageActivity.class).putExtra("looze", false));
              reference.child("isActive").setValue(false);
              finish();
            }
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
    }
  }

  private void setfont(TextView textView) {
    textView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));
  }
}
