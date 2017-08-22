package com.example.user.eldercare;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.user.Dropbox.DBRoulette;
import com.example.user.database.MyDBHelper;
import com.example.user.database.Var;

@SuppressLint("NewApi")
public class EmergencyWarningActivity extends Activity implements
        LocationListener {

    private static final String TAG = "EmergencyWarning ";
    String time = " ";
    int nowyear=0,nowmonth=0,nowday=0,nowminute=0,nowhour=0,nowsecond=0;
    String[] Date;
    String [] Id;
    Calendar cal = Calendar.getInstance();
    String dat;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat nowtime = new SimpleDateFormat("HH:mm:ss");
    String date1 = " ";
    FallData FD=new FallData();
    wifidata wd=new wifidata();
    LocationManager LM;
    PendingIntent pendingIntent;
    Intent ent = new Intent();
    public static SoundPool soundPool;
    Button bcancel;
    SmsManager smsManager;
    Handler mHandler;
    boolean record = false;
    WifiManager wifi;
    // DBHelper source;
    String mssid1;
    int size = 0;
    List<ScanResult> results;
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;

    // for dropbox upload
    private final String SAVE_DIR = "/sdcard/ElderCare/database/"; // 儲存手機資料庫
    private DBRoulette myDropboxTool;
    // is save to sdcard
    boolean isSave = false;
    // upload file to dropbox
    File file1;
    File file2;
    // DB
    MyDBHelper source;
    String NowDate; // 現在日期
    DataObject date = new DataObject();

    private static final Map<String, String> wifichannel = new HashMap<String, String>();
    static {
        wifichannel.put("2412", "2.4G Ch01");
        wifichannel.put("2417", "2.4G Ch02");
        wifichannel.put("2422", "2.4G Ch03");
        wifichannel.put("2427", "2.4G Ch04");
        wifichannel.put("2432", "2.4G Ch05");
        wifichannel.put("2437", "2.4G Ch06");
        wifichannel.put("2442", "2.4G Ch07");
        wifichannel.put("2447", "2.4G Ch08");
        wifichannel.put("2452", "2.4G Ch09");
        wifichannel.put("2457", "2.4G Ch10");
        wifichannel.put("2462", "2.4G Ch11");
        wifichannel.put("2467", "2.4G Ch12");
        wifichannel.put("2472", "2.4G Ch13");
        wifichannel.put("2484", "2.4G Ch14");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_warning);
        // source = new DBHelper(this);
        copyDBtoSDCard();
        file1 = new File("/sdcard/ElderCare/database/","elderlycare.db");
        myDropboxTool = new DBRoulette(this);
        source = new MyDBHelper(this, Var.DBPATH + Var.DBNAME, null, 0);

        bcancel = (Button) findViewById(R.id.cancel);
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(this, R.raw.warning, 1);
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
                soundPool.play(1, 1, 1, 0, -1, 1);
            }
        });

        LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.e("oncreate", "is in");
      //  mHandler = new Handler();
      //  mHandler.post(runnable);

        smsManager = SmsManager.getDefault();
        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);

        bcancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                makesure();
                ent.setClass(EmergencyWarningActivity.this,
                        EmergencyDetection_mainActivity.class);
                soundPool.release();
                startActivity(ent);
            }
        });

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (wifi.isWifiEnabled() == false) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Remind");
            dialog.setMessage("Your Wi-Fi is not enabled, enable?");
            dialog.setIcon(android.R.drawable.ic_dialog_info);
            dialog.setCancelable(false);

            dialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            wifi.setWifiEnabled(true);
                            Toast.makeText(getApplicationContext(),
                                    "wifi is disabled..making it enabled",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
            dialog.show();
        }

        this.adapter = new SimpleAdapter(EmergencyWarningActivity.this,
                arraylist, R.layout.activity_list, new String[] { "ssid",
                "power", "freq" }, new int[] { R.id.ssid, R.id.power,
                R.id.freq });

        results = wifi.getScanResults();

        size = results.size();
        try {
            size = size - 1;
            while (size >= 0) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put("ssid", results.get(size).SSID);
                item.put("power", new String(results.get(size).level + " dBm"));
                String wifichn = wifichannel.containsKey(new String(""
                        + results.get(size).frequency)) ? wifichannel
                        .get(new String("" + results.get(size).frequency))
                        : "5G";
                item.put("freq", wifichn);
                arraylist.add(item);
                size--;
                adapter.notifyDataSetChanged();
            }

            Collections.sort(arraylist,
                    new Comparator<HashMap<String, String>>() {

                        @Override
                        public int compare(HashMap<String, String> lhs,

                                           HashMap<String, String> rhs) {
                            // TODO Auto-generated method stub
                            return ((String) lhs.get("power"))
                                    .compareTo((String) rhs.get("power"));
                        }

                    });
            Id= source.QueryL("Wifid");
            dat=String.valueOf(sf.format(cal.getTime()));
            send();
            sf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            date1 = String.valueOf(sf.format(new java.util.Date()));
            Date = date1.split("/");
            nowyear=Integer.valueOf(Date[0]);
            nowmonth=Integer.valueOf(Date[1]);
            nowday=Integer.valueOf(Date[2]);
            nowtime.setTimeZone(TimeZone.getTimeZone("GMT+8"));//==取得目前時間
            time = String.valueOf(nowtime.format(new java.util.Date()));
            String[] Time = time.split(":");
            nowhour=Integer.valueOf(Time[0]);//hour 是24小時制
            nowminute=Integer.valueOf(Time[1]);
            nowsecond = Integer.valueOf(Time[2]);
            mssid1 = arraylist.get(0).get("ssid");// 基地台名稱


            int x= Id.length-1;
            int x1=Integer.valueOf(Id[x])+1;
            String x2=String.valueOf(x1);



            Toast.makeText(this,mssid1,Toast.LENGTH_LONG).show();


            wd.mssid = String.valueOf(mssid1); // 將要存入DB的參數

            wd.wifi_ID=x2;
            wd.date=dat;
            wd.time=nowhour + "點:" + nowminute + "分:" + nowsecond+"秒";

            //FD.mssid=String.valueOf(mssid1);

           boolean insertLastChecker = source.insertWifiData("Wifid", wd);
            Log.e("Wifi Data", "insertLastChecker = " + insertLastChecker); // 更新



         /*   if(insertLastChecker==true) {
                myDropboxTool.sentFile(SAVE_DIR, file1);// 上傳檔案output
                myDropboxTool.sentFile(SAVE_DIR, file2);// 上傳檔案data
            }*/
            
            Intent intent = new Intent();
            intent.setClass(EmergencyWarningActivity.this,
                    EmergencyDetection_mainActivity.class);
            startActivity(intent);
            EmergencyWarningActivity.this.finish();// 關閉activity

        } catch (Exception e) {
            // TODO: handle exception
        }

        Toast.makeText(EmergencyWarningActivity.this, "Scanning..." + size,
                Toast.LENGTH_LONG).show();
        Toast.makeText(EmergencyWarningActivity.this, mssid1, Toast.LENGTH_LONG)
                .show();

        wifi.startScan();
        
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



    final Runnable runnable = new Runnable() {
        public void run() { // TODO Auto-generated method stub
            // 需要背景作的事
            try {
                Thread.sleep(10000);

                Log.e("delay", "delay 10 sec");
                // send();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    };



    /*
     * LocationListener GPSll = new LocationListener() {
     *
     * @Override public void onStatusChanged(String provider, int status, Bundle
     * extras) { // TODO Auto-generated method stub }
     *
     * @Override public void onProviderEnabled(String provider) { // TODO
     * Auto-generated method stub }
     *
     * @Override public void onProviderDisabled(String provider) { // TODO
     * Auto-generated method stub }
     *
     * @Override public void onLocationChanged(Location location) { // TODO
     * Auto-generated method stub LM.removeUpdates(NWll); update(location);
     * Log.e("gps", "url=" + fd.slat + "," + fd.slon); Log.e("weblink", "url=" +
     * fd.slat + "," + fd.slon); if (record == false) { mHandler = new
     * Handler(); mHandler.post(runnable); record = true; } // send(); } };
     * LocationListener NWll = new LocationListener() {
     *
     * @Override public void onStatusChanged(String provider, int status, Bundle
     * extras) { // TODO Auto-generated method stub
     *
     * }
     *
     * @Override public void onProviderEnabled(String provider) { // TODO
     * Auto-generated method stub
     *
     * }
     *
     * @Override public void onProviderDisabled(String provider) { // TODO
     * Auto-generated method stub
     *
     * }
     *
     * @Override public void onLocationChanged(Location location) { // TODO
     * Auto-generated method stub LM.removeUpdates(GPSll); update(location);
     * Log.e("network", "url=" + fd.slat + "," + fd.slon); if (record == false)
     * { mHandler = new Handler(); mHandler.post(runnable); record = true; }
     * send(); } };
     */

    private void send(){

        SmsManager smsManager = SmsManager.getDefault();
        try{
            smsManager.sendTextMessage("0952521648",
                    null,
                    "老人跌倒了", PendingIntent.getBroadcast(this.getApplicationContext(), 0, new Intent(), 0), null);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    // 如果GPS開啟

    public void makesure() {
        AlertDialog.Builder ad = new AlertDialog.Builder(
                EmergencyWarningActivity.this);
        ad.setTitle("取消警訊");
        ad.setMessage("確定取消警訊?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {// 退出按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub\
                Intent intent = new Intent();
                // 跟老師確認要回到哪一個葉面
                intent.setClass(EmergencyWarningActivity.this,
                       MainActivity.class);
                EmergencyWarningActivity.this.finish();// 關閉activity
            }
        });
        ad.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                // 不退出不用執行任何操作
            }
        });

        ad.show();// 示對話框

    }

    @Override
    protected void onResume() {
        super.onResume();
        // LM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50000, 0,
        // NWll);
        // LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0,
        // GPSll);
        myDropboxTool.doOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // LM.removeUpdates(NWll);
        // LM.removeUpdates(GPSll);
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

}
