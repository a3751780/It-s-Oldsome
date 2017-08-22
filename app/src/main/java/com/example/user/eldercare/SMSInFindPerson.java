package com.example.user.eldercare;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SMSInFindPerson extends Activity {
    Button SmsCancel;
    public static SoundPool soundPool1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_find_person);
        SmsCancel=(Button) findViewById(R.id.button1);
        SmsCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                EmergencyWarningActivity.soundPool.stop(0);
            }
        });
        Bundle bunde=this.getIntent().getExtras();
        if (bunde!=null) {
            soundPool1 = new SoundPool(10, AudioManager.STREAM_ALARM,10);
            soundPool1.load(this, R.raw.help, 1);
            soundPool1.setOnLoadCompleteListener(new OnLoadCompleteListener() {
                public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
                    soundPool1.play(1, 1, 1, 0, -1, 1);
                }
            });
        }

    }
}
