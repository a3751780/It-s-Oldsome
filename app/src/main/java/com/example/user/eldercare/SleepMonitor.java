package com.example.user.eldercare;
import com.example.user.database.MyDBHelper;
import com.example.user.database.Var;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class SleepMonitor extends Activity {
    private static final String TAG = "MonitorActivity";
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private Button startBtn;
    private TextView Quality,DeepSleep, TotalSleep;
    private CheckBox CheckBox1, CheckBox2, CheckBox3;
    private Spinner DateSpinner;
    private WebView PieChart;
    MyDBHelper source;
    private final String SAVE_DIR = "/sdcard/ElderCare/database/";
    SleepRecord SR = new SleepRecord();
    String Date[];
    private ArrayAdapter<String> adapter;

    File file1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleepmonitor);
        findViewById();

        source = new MyDBHelper(this, Var.DBPATH + Var.DBNAME, null, 0);
        try {
            download();
            Date = source.QueryDate("LastSleepData");
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, Date);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            DateSpinner.setOnItemSelectedListener( new  SpinnerSelectedListener());
            DateSpinner.setAdapter(adapter);


        } catch (Exception ex) {
            Log.e(TAG,ex.toString());
            Quality.setText(" ");
            DeepSleep.setText(" ");
            TotalSleep.setText(" ");
            Toast.makeText(SleepMonitor.this, "沒有記錄 ",Toast.LENGTH_SHORT).show();
        }
    }

    class  SpinnerSelectedListener  implements  OnItemSelectedListener{

        public  void  onItemSelected(AdapterView<?> arg0, View arg1,  int  index,
                                     long  arg3) {
            Log.e("*index*",String.valueOf(index));
            try {
                SR = source.QueryRecordDate_C(Date[index]);
                Log.e(TAG, SR.DeepSleep+", "+SR.TotalSleep);
                PieChart();
            } catch (Exception ex) {
                Log.e(TAG,ex.toString());
            }
            Quality.setText(SR.Quality);
            DeepSleep.setText(SR.DeepSleep+" 分鐘");
            TotalSleep.setText(SR.TotalSleep+" 分鐘");

            if(SR.sleeptime.equals("1"))
                CheckBox1.setChecked(true);
            else
                CheckBox1.setChecked(false);
            if(SR.weektime.equals("1"))
                CheckBox3.setChecked(true);
            else
                CheckBox3.setChecked(false);
            if(SR.deeptime.equals("1"))
                CheckBox2.setChecked(true);
            else
                CheckBox2.setChecked(false);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }

    public void PieChart(){
        String Deep = "%E6%B7%B1%E7%9C%A0%E6%9C%9F";
        String Light = "%E6%B7%BA%E7%9C%A0%E6%9C%9F";
        String Title = "%E6%B7%B1%E7%9C%A0%E6%9C%9F%E5%8F%8A%E6%B7%BA%E7%9C%A0%E6%9C%9F%E6%AF%94%E4%BE%8B%E5%9C%93%E9%A4%85%E5%9C%96";
        int deep = (int)Math.round((Double.valueOf(SR.DeepSleep)/Double.valueOf(SR.TotalSleep))*100);
        int light = 100-deep;
        Log.e(TAG, deep+", "+light);
        String DEEP = String.valueOf(deep);
        String LIGHT = String.valueOf(light);
        String myURL = "http://chart.apis.google.com/chart?cht=p3&chd=t:"+DEEP+","+LIGHT+"&chs=175x85&chl="+Deep+"|"+Light
                +"&chtt="+Title;

        Log.e("URL", myURL);
        PieChart.getSettings().setJavaScriptEnabled(true);
        PieChart.loadUrl(myURL);
    }

    void findViewById() {
        Quality = (TextView) findViewById(R.id.quality);
        DeepSleep = (TextView) findViewById(R.id.deepsleep);
        TotalSleep = (TextView) findViewById(R.id.totalsleep);
        PieChart= (WebView) findViewById(R.id.webView1);
        CheckBox1 = (CheckBox) findViewById(R.id.checkBox1);
        CheckBox2 = (CheckBox) findViewById(R.id.checkBox2);
        CheckBox3 = (CheckBox) findViewById(R.id.checkBox3);
        DateSpinner = (Spinner) findViewById(R.id.datespinner);
    }

    public void download(){

        if(check_SD())
        {
            String url = "";
            url = "https://dl.dropboxusercontent.com/s/engb8pvsmv98raj/elderlycare.db?token_hash=AAHkoouz32RL8zkbasiZ0TvWaEWwUVD-zm_TsfeppBB4dA&dl=1";
            File myTempFile = new File("/sdcard/ElderCare/database/elderlycare.db");
            getNotificationSound(url,"elderlycare.db");
            Log.e(TAG, "下載成功");
            Toast.makeText(this,"下載成功",Toast.LENGTH_SHORT).show();

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
                            Log.e(TAG, "stream is null");
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
                        Log.e(TAG, e.toString());
                    }

                } catch (MalformedURLException e) {
                    Log.e(TAG, e.toString());

                }

            }
        });

        threadgetFile.start();

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            SleepMonitor.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void onResume()
    {
        super.onResume();
        Log.v(TAG,"onResume");
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

}

