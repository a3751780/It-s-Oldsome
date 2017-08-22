package com.example.user.eldercare;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.example.user.database.MyDBHelper;
import com.example.user.database.Var;

public class EmergencyDetection_mainActivity extends Activity implements
        SensorEventListener {

    File file;
    FallData fd = new FallData();
    /*
     * interface
     */
    ImageButton sleep;
    /*
     * sensor
     */
    SensorManager sm;
    Sensor s;

    double temp, temp2 = 0;
    double SVM = 0;
    double x = 0;
    double y = 0;
    double z = 0;
    double[] theta = new double[3];
    double[] gra = new double[3];
    double[] deg = new double[3];
    double deSVM = 0;
    double deSMA = 0;
    double Ax, Ay, Az = 0;

    String date;
    String time;

    boolean record = false;

    int count = 1;

    long starttime, spenttime, begin;

    long period = 1000;
    Handler hand;

    boolean firsttime = false;


    // DB
    MyDBHelper source;
    // for dropbox upload

    Intent fallIntent;

    NotificationManager barManager;
    Notification barMsg;

    private final String SAVE_DIR = "/sdcard/ElderCare/database/";

    private final static String MSG_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private Context mycon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_detection_main);
       // registerReceiver(broadcastReceiver, new IntentFilter(MSG_RECEIVED));
        // myDropboxTool = new MyDropboxTool(this);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);

        // if the first time open this app, copy DB from res/raw/testdb.db
        // toVar.DBPATH + Var.DBNAME
        copyDBtoSDCard();
        // load DB from var path
        source = new MyDBHelper(this, Var.DBPATH + Var.DBNAME, null, 0);
        starttime = System.currentTimeMillis();
        begin = System.currentTimeMillis();
        hand = new Handler();
        // hand.post(r);
        // Log.e(String.valueOf(starttime), "starttime");
        showNotification();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (hand != null) {
            hand.removeCallbacks(r);
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_emergency_detection_main, menu);
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent ev) {
        // TODO Auto-generated method stub
        hand.post(r);
        if (ev.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            temp = 0;
            temp2 = 0;
            // Log.e("x="+ev.values[0]+" y="+ev.values[1]+" z="+ev.values[2],"sensorchanged");
            for (int i = 0; i < 3; i++) {
                temp += Math.pow(ev.values[i], 2);// math.pow ����
            }
            SVM = Math.sqrt(temp);// math.sqrt �ڸ�
            fd.newx = ev.values[0];
            fd.newy = ev.values[1];
            fd.newz = ev.values[2];
            // Log.e("x="+x+" y="+y+" z="+z,"sensorchanged");
            // 1.��X�C�Ӫ�����
            for (int i = 0; i < 3; i++)
                theta[i] = Math.acos((ev.values[i] / SVM));

            // 2.��XG
            for (int i = 0; i < 3; i++)
                gra[i] = (Math.sqrt(Math.abs(ev.values[i])) * Math
                        .cos(theta[i]));

            // 3.A-G(�ѤUB �~�O)
            for (int i = 0; i < 3; i++)
                deg[i] = ev.values[i] - gra[i];
            // 4.A�a�JSVM,SMA
            for (int i = 0; i < 3; i++)
                temp2 += Math.pow(deg[i], 2);
            deSVM = Math.sqrt(temp2);
            if (count < 3) {
                // Log.e("starttime","starttime="+starttime);
                period();
                // Log.e("if count", "if" + count);
            }

        }
        // Log.e("x="+fd.newx+" y="+fd.newy+" z="+fd.newz,"sensorchanged");

    }

    Runnable r = new Runnable() {
        public void run() {


            if (firsttime == false) {
                try {
                    Thread.sleep(10000);
                    Log.e("r", "delay");

                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                firsttime = true;
                fd.tx = fd.newx;
                fd.ty = fd.newy;
                fd.tz = fd.newz;
                Log.e(Double.toString(fd.tx) + "=fd.tx "
                        + Double.toString(fd.ty) + "=ty "
                        + Double.toString(fd.tz) + "=tz", "i want to sleep");
                Log.e("firsttime=", String.valueOf(firsttime));
            } else if (firsttime == true) {
                if (Math.abs(fd.tx - fd.newx) > 5
                        || Math.abs(fd.ty - fd.newy) > 5
                        || Math.abs(fd.tz - fd.newz) > 5) {
                    record = true;
                    Log.e("你跌倒了", "record=" + String.valueOf(record));

                    Intent intent = new Intent();
                    intent.setClass(EmergencyDetection_mainActivity.this,
                            EmergencyWarningActivity.class);
                    startActivity(intent);
                    sm.unregisterListener(EmergencyDetection_mainActivity.this);

                } else {
                    Log.e("你沒有跌倒", "*****");
                }

                fd.tx = fd.newx;
                fd.ty = fd.newy;
                fd.tz = fd.newz;
                hand.postDelayed(this, period);
            }

        }

    };

    // (��ܳq����T��)
    public void showNotification() {
        barManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        barMsg = new Notification(R.drawable.icon, "跌倒偵測中",
                System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, EmergencyDetection_mainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        barMsg.setLatestEventInfo(EmergencyDetection_mainActivity.this,
                "跌倒偵測中", "", contentIntent);
        barManager.notify(0, barMsg);

    }

    // (��ܳq����T��)

    void period() {
        spenttime = System.currentTimeMillis();
        long second = ((spenttime - starttime) / 1000) % 60;
        Log.e("" + count, "");
        if (second < 5) {
            Ax += Math.abs(deg[0]);
            Ay += Math.abs(deg[1]);
            Az += Math.abs(deg[2]);
        } else if (second == 5) {
            switch (count) {
                case 1:
                    count = 2;
                    starttime = System.currentTimeMillis();
                    break;
                case 2:
                    deSMA = ((Ax + Ay + Az) / 2);
                    Log.e(Ax + " " + Ay + " " + Az, "deSMA=" + deSMA + " ,count="
                            + count);
                    SimpleDateFormat nowdate = new SimpleDateFormat("yyyy/MM/dd");
                    nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    SimpleDateFormat nowtime = new SimpleDateFormat("hh:mm:ss");
                    date = String.valueOf(nowdate.format(new java.util.Date()));
                    time = String.valueOf(nowtime.format(new java.util.Date()));
                    fd.Ax = String.valueOf(Ax);
                    fd.Ay = String.valueOf(Ay);
                    fd.Az = String.valueOf(Az);
                    fd.SVM = String.valueOf(SVM);
                    fd.deSVM = String.valueOf(deSVM);
                    fd.deSMA = String.valueOf(deSMA);
                    fd.Date = date;
                    fd.Time = time;


                   boolean insertChecker = source.insertFallData("Fall", fd);
                    Log.e("FallData", "insertFallChecker = " + insertChecker);

                    file = new File("/sdcard/ElderCare/database/", "elderlycare.db");
                    // 5.���e��

                    if (deSMA > 3000 && deSVM > 6.5) { // �i�Jĵ�i�����åB�o�eĵ�T
                        Intent intent = new Intent();
                        intent.setClass(EmergencyDetection_mainActivity.this,
                                EmergencyWarningActivity.class);
                        startActivity(intent);
                        sm.unregisterListener(EmergencyDetection_mainActivity.this);
                    }
                    if (deSMA > 1500 && deSVM > 6.4) { // �i�Jĵ�i�����åB�o�eĵ�T
                        Intent intent = new Intent();
                        intent.setClass(EmergencyDetection_mainActivity.this,
                                EmergencyWarningActivity.class);
                        startActivity(intent);
                        sm.unregisterListener(EmergencyDetection_mainActivity.this);
                    }

                    Ax = 0;
                    Ay = 0;
                    Az = 0;
                    deSMA = 0;
                    deSVM = 0;
                    SVM = 0;
                    starttime = System.currentTimeMillis();
                    count = 1;
                    break;

                // myDropboxTool.sentFile(SAVE_DIR, file);//�W���ɮ�output
            }

        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                ConfirmExit();

                return true;
            case KeyEvent.KEYCODE_HOME:
                ConfirmExit();
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void ConfirmExit() {
        AlertDialog.Builder ad = new AlertDialog.Builder(
                EmergencyDetection_mainActivity.this);

        ad.setTitle("離開");
        ad.setMessage("確定要離開?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub
                barManager.cancelAll();
                sm.unregisterListener(EmergencyDetection_mainActivity.this);
                EmergencyDetection_mainActivity.this.finish();


            }
        });
        ad.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        ad.show();
    }


    // copy file when first time open APP
    public void copyDBtoSDCard() {
        try {
            String tSDCardPath = android.os.Environment
                    .getExternalStorageDirectory().getAbsolutePath();

            // File tDataPath = new File(tSDCardPath+"/mydb/");
            File tDataPath = new File(Var.DBPATH);

            String tDBFilePath = tDataPath + Var.DBNAME;
            File tFile = new File(tDBFilePath);

            if (tDataPath.exists() == false) {
                Log.e("CopyDBException", "tDataPath start");
                tDataPath.mkdirs();
                Log.e("CopyDBException", "tDataPath success");
            }

            if (tFile.exists() == false) {
                InputStream tISStream = this.getResources().openRawResource(
                        R.raw.elderlycare);
                FileOutputStream tOutStream = new FileOutputStream(tDBFilePath);
                byte[] tBuffer = new byte[5120];
                int tCount = 0;
                while ((tCount = tISStream.read(tBuffer)) > 0) {
                    tOutStream.write(tBuffer, 0, tCount);
                }
                tOutStream.close();
                tISStream.close();
            }
        } catch (Exception e) {
            Log.e("CopyDBException", e.getMessage());
        }
    }
}