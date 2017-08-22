package com.example.user.eldercare;


import android.os.Bundle;

import android.os.Environment;
import android.support.v4.app.FragmentActivity;

import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.example.user.database.MyDBHelper;
import com.example.user.database.Var;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends FragmentActivity implements
        RadioGroup.OnCheckedChangeListener {
    FragmentTabHost mTabHost;
    MyDBHelper source;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        ((RadioGroup) findViewById(R.id.tab_radiogroup))
                .setOnCheckedChangeListener(this);
        mTabHost.addTab(mTabHost.newTabSpec("one")
                .setIndicator("老人資料")
                , ConnectFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("two")
                .setIndicator("睡眠偵測")
                ,SleepFragment.class,null);

        mTabHost.addTab(mTabHost.newTabSpec("three")
                .setIndicator("心律偵測")
                , HeartFragment.class, null);


        mTabHost.addTab(mTabHost.newTabSpec("four")
                .setIndicator("添衣提醒")
                ,clothFragment.class,null);

        mTabHost.addTab(mTabHost.newTabSpec("five")
                .setIndicator("室內定位")
                ,WifiFragment.class,null);
        source = new MyDBHelper(this, Var.DBPATH + Var.DBNAME, null, 0);
        copyDBtoSDCard();
        download();


    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.radio_button_info:
                mTabHost.setCurrentTabByTag("one");
                break;
            case R.id.radio_button_sleep:
                mTabHost.setCurrentTabByTag("two");
                break;
            case R.id.radio_button_heart:
                mTabHost.setCurrentTabByTag("three");
                break;
            case R.id.radio_button_cloth:
                mTabHost.setCurrentTabByTag("four");
                break;
            case R.id.radio_button_loca:
                mTabHost.setCurrentTabByTag("five");
                break;

        }

    }

    public void download(){

        if(check_SD())
        {

            String url = "";
            url = "https://dl.dropboxusercontent.com/s/engb8pvsmv98raj/elderlycare.db?token_hash=AAHkoouz32RL8zkbasiZ0TvWaEWwUVD-zm_TsfeppBB4dA&dl=1";
            File myTempFile = new File("/sdcard/ElderCare/database/elderlycare.db");
            getNotificationSound(url, "elderlycare.db");

            Toast.makeText(this, "下載成功", Toast.LENGTH_SHORT).show();

        }
    }
    public boolean check_SD(){
        String strNotificationSoundFolder =
                Environment.getExternalStorageDirectory().getParent() + "/" +
                        Environment.getExternalStorageDirectory().getName() +  "/ElderCare/database/";

        File vPath = new File( strNotificationSoundFolder);
        if( !vPath.exists() ){
            if (vPath.mkdirs()){
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }
    public void getNotificationSound(final String url , final String filename){
        Thread threadgetFile = new Thread(new Runnable() {
            public void run() {
                try {
                    URL myURL = new URL(url);
                    try {
                        URLConnection conn = myURL.openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        if (is == null){

                        }
                        File myTempFile = new File("/sdcard/ElderCare/database/" + filename );
                        FileOutputStream fos = new FileOutputStream(myTempFile);
                        byte buf[] = new byte[128];
                        do{
                            int numread = is.read(buf);
                            if (numread<=0){break;}
                            fos.write(buf,0,numread);
                        }while(true);
                    } catch (IOException e) {

                    }

                } catch (MalformedURLException e) {


                }

            }
        });

        threadgetFile.start();

    }
    public void copyDBtoSDCard() {
        try {

            String tSDCardPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

            //File tDataPath = new File(tSDCardPath+"/mydb/");
            File tDataPath = new File(Var.DBPATH);

            String tDBFilePath = tDataPath + Var.DBNAME;
            File tFile = new File(tDBFilePath);

            if (tDataPath.exists() == false) {
                Log.e("CopyDBException", "tDataPath start");
                tDataPath.mkdirs();
                Log.e("CopyDBException","tDataPath success");
            }

            if (tFile.exists() == false)
            {
                InputStream tISStream = this.getResources().openRawResource(R.raw.elderlycare);
                FileOutputStream tOutStream = new FileOutputStream(tDBFilePath);
                byte[] tBuffer = new byte[5120];
                int tCount = 0;
                while ((tCount = tISStream.read(tBuffer)) > 0)
                {
                    tOutStream.write(tBuffer, 0, tCount);
                }
                tOutStream.close();
                tISStream.close();
            }
        }
        catch (Exception e)
        {
            Log.e("CopyDBException",e.getMessage());
        }
    }

}

