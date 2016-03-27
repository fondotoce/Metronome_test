package com.fondotoce.metronomef;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    SeekBar sbWeight;
    final Animation animation = new AlphaAnimation(1, 0);

    boolean vibrOn = true;
    boolean flashOn = true;
    boolean soundOn = true;
    boolean serviceStart = true;

    int bpmValue = 100;

    ImageButton butVbr, butFlash, butSnd;
    ImageView imVIndctr;

    SeekBar seekBar;
    Button btnServ;
    EditText edtTxt;

    LinearLayout.LayoutParams lParams1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        butVbr = (ImageButton) findViewById(R.id.but_vib);
        butFlash = (ImageButton) findViewById(R.id.but_flsh);
        butSnd = (ImageButton) findViewById(R.id.but_snd);
        imVIndctr = (ImageView) findViewById(R.id.im_v_indicator);

        edtTxt = (EditText) findViewById(R.id.edtxt);

        sbWeight = (SeekBar) findViewById(R.id.seekbar);
        sbWeight.setOnSeekBarChangeListener(this);
        btnServ = (Button) findViewById(R.id.btn_service);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        lParams1 = (LinearLayout.LayoutParams) btnServ.getLayoutParams();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        bpmValue = progress;
        if (bpmValue == 0)
        {
            bpmValue = 1;
        }
        edtTxt.setText(String.valueOf(bpmValue));
    }

    public void onClickService(View v) {
        if (serviceStart)
        {
            //Adjusting to get value within the limits
            bpmValue = Integer.valueOf(edtTxt.getText().toString());
            if (bpmValue > 100)
            {
                bpmValue = 100;
                edtTxt.setText("100");
            }
            if (bpmValue == 0)
            {
                bpmValue = 1;
                edtTxt.setText("1");
            }

            //Preparing data to be sent to MetroService
            Bundle extras = new Bundle();
            extras.putInt("bpm", bpmValue);
            extras.putBoolean("vibrOn", vibrOn);
            extras.putBoolean("flashOn", flashOn);
            extras.putBoolean("soundOn", soundOn);
            //Initiating the MetroService
            startService(new Intent(MainActivity.this, MetroService.class).putExtras(extras));
            btnServ.setText("STOP");
            indicatorGreen();
        }
        else
        {
            stopIndFlash();
            //Terminating the MetroService
            stopService(new Intent(MainActivity.this, MetroService.class));
            btnServ.setText("START");
        }
        serviceStart = !serviceStart;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //Just to please the compiler
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //Just to please the compiler
    }


    public void onClickVibr(View v) {
        //Deciding whether we need vibrations
        if (vibrOn)
            butVbr.setImageResource(R.drawable.vibration_off);
        else
            butVbr.setImageResource(R.drawable.vibration_on);
        vibrOn = !vibrOn;
    }

    public void onClickFlsh(View v) {
        //Deciding whether we need flashes
        if (flashOn)
            butFlash.setImageResource(R.drawable.flash_off);
        else
            butFlash.setImageResource(R.drawable.flash_on);
        flashOn = !flashOn;
    }

    public void onClickSnd(View v) {
        //Deciding whether we need sound
        if (soundOn)
            butSnd.setImageResource(R.drawable.sound_off);
        else
            butSnd.setImageResource(R.drawable.sound_on);
        soundOn = !soundOn;
    }

    private void indicatorGreen() {
        //Clearly here we get some discrepancy due to the insignificant inaccuracy of BPM value in MetroService.
        //Elimination of such a discrepancy is beyond the scope of this test task.
        animation.setDuration(60000/bpmValue/2);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        imVIndctr.startAnimation(animation);
    }

    private void stopIndFlash() {
        //Stop indicator
        animation.setRepeatCount(0);;
    }
}



