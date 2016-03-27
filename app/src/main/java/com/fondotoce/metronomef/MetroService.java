package com.fondotoce.metronomef;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by fondotoce on 24.03.2016.
 */
public class MetroService extends IntentService {

    MediaPlayer mPlayer;
    Vibrator vibrator;

    private Camera camera;
    private Camera.Parameters parameter;
    private boolean useVibrator = true;
    private boolean useFlash = true;
    private boolean useSound = true;

    public MetroService() {
        super("Metronome Service");
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //Data received from the main activity
        Bundle extras = intent.getExtras();
        int bpmValue = extras.getInt("bpm", 0);

        if (bpmValue == 0)
            bpmValue = 1;

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //Checking if device has got VIBRATOR
        if (!vibrator.hasVibrator())
        {
            useVibrator = false;
        }

        useFlash = getApplication().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        //Checking if device has got CAMERA
        if(useFlash){
            this.camera = Camera.open(0);
            parameter = this.camera.getParameters();

        }


        if (useVibrator)
            useVibrator = extras.getBoolean("vibrOn", true);
        if (useFlash)
            useFlash = extras.getBoolean("flashOn", true);
        if (useSound)
            useSound = extras.getBoolean("soundOn", true);

        //Preparing index items for switch statement which is better than inserting
        // if statement in each iteration in terms of accuracy
        int yesVibr = 0;
        int yesFlash = 0;
        int yesSound = 0;
        if (useVibrator)
            yesVibr = 100;
        if (useFlash)
            yesFlash = 10;
        if (useSound)
            yesSound = 1;

        //Hopefully the inaccuracy 1 to 4 milliseconds is acceptable for the purpose of the test
        try {
            mPlayer= MediaPlayer.create(this, R.raw.beat);
            if (bpmValue > 0)
                while (true) {
                    try {
                        switch (yesVibr + yesFlash + yesSound )
                        {
                            case (111):   //vibration + flashes + sound
                            {
                                turnOnTheFlash();
                                mPlayer.start();
                                vibrator.vibrate(200);
                                turnOffTheFlash();
                                TimeUnit.MILLISECONDS.sleep(60000 / bpmValue);
                                break;
                            }
                            case (110):   //vibration + flashes
                            {
                                turnOnTheFlash();
                                vibrator.vibrate(200);
                                turnOffTheFlash();
                                TimeUnit.MILLISECONDS.sleep(60000 / bpmValue);
                                break;
                            }
                            case (101):   //vibration + sound
                            {
                                mPlayer.start();
                                vibrator.vibrate(200);
                                TimeUnit.MILLISECONDS.sleep(60000 / bpmValue);
                                break;
                            }
                            case (100):   //vibration only
                            {
                                vibrator.vibrate(200);
                                TimeUnit.MILLISECONDS.sleep(60000 / bpmValue);
                                break;
                            }
                            case (11):   //flashes + sound
                            {
                                turnOnTheFlash();
                                mPlayer.start();
                                turnOffTheFlash();
                                TimeUnit.MILLISECONDS.sleep(60000 / bpmValue);
                                break;
                            }
                            case (10):   //flashes only
                            {
                                turnOnTheFlash();
                                turnOffTheFlash();
                                TimeUnit.MILLISECONDS.sleep(60000 / bpmValue);
                                break;
                            }
                            case (1):   //sound only
                            {
                                mPlayer.start();
                                TimeUnit.MILLISECONDS.sleep(60000 / bpmValue);
                                break;
                            }
                            default:   //all options were cancelled
                                break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void turnOffTheFlash() {
        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        this.camera.setParameters(parameter);
        this.camera.stopPreview();
    }

    private void turnOnTheFlash() {
        if(this.camera != null){
            parameter = this.camera.getParameters();
            parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            this.camera.setParameters(parameter);
            this.camera.startPreview();
        }
    }

    public void onDestroy() {
        //Getting rid of used objects
        super.onDestroy();
        turnOffTheFlash();
        camera.release();
        mPlayer.stop();
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        vibrator.cancel();
    }
}
