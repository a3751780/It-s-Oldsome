package com.example.user.eldercare;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.user.Dropbox.DBRoulette;
import com.example.user.database.MyDBHelper;
import com.example.user.database.Var;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.SampleRate;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class SleepDetection extends Activity implements SensorEventListener {

    private BandClient client = null;
    SleepData sleepData = new SleepData();
    private static final String TAG = "Sleepdetection";  //睡眠狀態檢測主要活動
    private TextView time;
    private ImageButton button;
    public String save_A;

    private SensorManager sensorManager;   //感應器管理器

    private long lastUpdate = -1;   //最後更新
    // for calculate MAD
    float  Xvalue[] = new float[999];
    float  Yvalue[] = new float[999];
    //    float  Zvalue[] = new float[999];
    float  value_A[] = null;
    float Xsum = 0, Ysum = 0;
    //    Zsum = 0;
    public int n=0;
    public int TemperMinute=-1;
    // for calculate PD
    public float TemperAT=0;
    public float TemperAL=0;
    //	public float TemperAZ=0;
    // for roll over interval
    int startTime=0;//開始睡覺時間
    int DeepSleepduration=0, Lightsleepduration=0;   //深、淺睡眠時間
    int lastRolloverTime=-1;  //最後翻轉時間
    int SleepTimeBetweenRollOver = 0;   //睡眠時間之間翻轉
    int nowSleepTime=0;  //現在睡眠時間
    // for dropbox upload
    private final String SAVE_DIR = "/sdcard/ElderCare/database/"; //儲存手機資料庫
    private DBRoulette myDropboxTool ;
    //upload file to dropbox
    File file1;
    File file2;
    //sensor
    float x=0,y=0,z=0;
    //DB
    MyDBHelper source;
    String DateArray[];
    boolean check1=false, check2=false, check3=false;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sleepdetection_activity);

        new appTask().execute();
        button=(ImageButton)findViewById(R.id.button);
        time=(TextView)findViewById(R.id.tv1);
        myDropboxTool  = new DBRoulette(this);

        //if the first time open this app, copy DB from res/raw/testdb.db to Var.DBPATH + Var.DBNAME
        copyDBtoSDCard();
        //load DB from var path DB路徑+DB名稱
        source = new MyDBHelper(this, Var.DBPATH + Var.DBNAME, null, 0);

        setBrightnessLevel(50); //設置亮度級別
        //sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        //SetSensor();
//	        setAirplaneMode(this, true);//開啟飛航模式
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
//	            	setAirplaneMode(SleepStatusDetection_mainActivity.this, false);//關閉飛航模式
                AlertDialog.Builder ad=new AlertDialog.Builder(SleepDetection.this); //對話方塊生成器
                ad.setTitle("離開");
                ad.setMessage("確定要離開?");
                ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//退出按鈕
                    public void onClick(DialogInterface dialog, int i) {
                        //如果不做任何事情，會直接關閉對話框
                        // TODO Auto-generated method stub
                        ConnectivityManager conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);//先取得此service
                        NetworkInfo networInfo = conManager.getActiveNetworkInfo();       //在取得相關資訊
                        if (networInfo == null || !networInfo.isAvailable()){ //判斷是否有網路
                            Toast.makeText(SleepDetection.this, "請先關閉飛航模式，開啟網路。", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }else{
                            setBrightnessLevel(180);
                            try{
                                SleepTimeBetweenRollOver = nowSleepTime - lastRolloverTime;//此區間值是翻身區間時間
                                Log.e("lastRolloverTime","nowSleepTime = " + nowSleepTime);
                                Log.e("lastRolloverTime","lastRolloverTime = " + lastRolloverTime);
                                if(SleepTimeBetweenRollOver > 20) // deep sleep門檻值
                                {
                                    DeepSleepduration += SleepTimeBetweenRollOver; // total deep sleep time(不停累加深眠時間)
                                }
                                else
                                {
                                    Lightsleepduration += SleepTimeBetweenRollOver; // total light sleep time
                                }
                                Log.e(TAG,"DeepSleepduration = " + DeepSleepduration);
                                Log.e(TAG,"Lightsleepduration = " + Lightsleepduration);
                                sleepData.rolloverCount = "1";
                                sleepData.totalsleep = String.valueOf(DeepSleepduration + Lightsleepduration);  //將要存入DB的參數
                                sleepData.deepsleep = String.valueOf(DeepSleepduration);

                                if(DeepSleepduration + Lightsleepduration != 0)
                                    sleepData.sleepQuality = String.valueOf((float)DeepSleepduration/(DeepSleepduration + Lightsleepduration));//計算睡眠品質
                                else
                                    sleepData.sleepQuality = "0";

                                boolean insertLastChecker = source.insertLastSleepData("LastSleepData", sleepData); //將sleepData透過insertLastSleepData包裝過去
                                Log.e("Last Sleep Data","insertLastChecker = " + insertLastChecker); //更新
                                checklastweek();
                                boolean updateChecker = source.updateDB(sleepData, "SleepDirect", sleepData.Date, sleepData.Time);
                                Log.e("AWAKE","updateChecker = " + updateChecker);

                               // sensorManager.unregisterListener(SleepDetection.this);
                   /*   if(insertLastChecker==true ) {
                                myDropboxTool.sentFile(SAVE_DIR, file1);//上傳檔案output
                                myDropboxTool.sentFile(SAVE_DIR, file2);//上傳檔案data
                                }*/
                                Intent intent = new Intent();
                                intent.setClass(SleepDetection.this,MainActivity.class);
                                startActivity(intent);
                                SleepDetection.this.finish();//關閉activity
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
                ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        //不退出不用執行任何操作
                    }
                });
                ad.show();//示對話框


            }
        });
        Log.i("tag",   "usedMemory: " + Debug.getNativeHeapSize() / 1048576L);

    }

//手環部分

    private class appTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            try {
                if (getConnectedBandClient()) {

                    client.getSensorManager().registerAccelerometerEventListener(acc, SampleRate.MS128);

                } else {

                }

            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case DEVICE_ERROR:
                        exceptionMessage = "Please make sure bluetooth is on and the band is in range.";
                        break;
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.";
                        break;
                    case BAND_FULL_ERROR:
                        exceptionMessage = "Band is full. Please use Microsoft Health to remove a tile.";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage();
                        break;
                }


            } catch (Exception e) {

            }
            return null;
        }
    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {

                return false;
            }
            client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }


        return ConnectionState.CONNECTED == client.connect().await();
    }



    private BandAccelerometerEventListener acc = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(BandAccelerometerEvent event) {
            if (event != null) {

                SimpleDateFormat nowdate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//		DateFormat nowdate = null;
                //==GMT標準時間往後加八小時
                nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                //==取得目前時間
                String sensor_date = nowdate.format(new java.util.Date());


                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(5);  //取小數到第五位

                //  value_A[0] = event.getAccelerationX();
                // value_A[1] = event.getAccelerationY();
                //  value_A[2] = event.getAccelerationZ();
                x=event.getAccelerationX();
                y=event.getAccelerationY();
                z=event.getAccelerationZ();


                String value_a1 = nf.format(x);
                String value_a2 = nf.format(y);
                String value_a3 = nf.format(z);

                //String value_a3=nf.format(value_A[2]);
                //lab_Z.setText("A_Z：" + String.valueOf(value_a3));
                save_A = value_a1 + "/" + value_a2 + "/" + value_a3;

                //    SleepData sleepData = new SleepData();

                long curTime = System.currentTimeMillis();//timer for saving
                if ((curTime - lastUpdate) > 1000) {
                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;
                    //savedata(sensor_date, value_a1, value_a2);
                    SleepData.ID_SleepData = String.valueOf(1);
                    SleepData.sensor_date = sensor_date;
                    SleepData.sensor_x = value_a1;
                    SleepData.sensor_y = value_a2;
//				SleepData.sensor_z = value_a3;

                    // Do INSERT 原始資料
                    //boolean insertChecker = source.insertSleepData("SleepData", sleepData);
                    //file2 = new File("/sdcard/ElderCare/database/","elderlycare.db");

                    MADB(x,y,z);
                }

            }
        }
    };

    //判斷睡眠指標
    void checklastweek(){
        String deep="0", sleep="0";
        DateArray = source.queryFirstArray();
        String nowDay = DateArray[DateArray.length-1];
        String[] DayArray = nowDay.split("/");
        Calendar ca = Calendar.getInstance();//得到一個Calendar的實例
        SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
        ca.set(Integer.valueOf(DayArray[0]), Integer.valueOf(DayArray[1])-1, Integer.valueOf(DayArray[2]));
        sleepData = source.QueryRecordDate(String.valueOf(sf.format(ca.getTime())));
        if(sleepData.insomnia.equals("1")){
            check1=true;
            Log.e(TAG, "check1"+String.valueOf(check1));
        }
        else{
            check1=false;
        }
        String nowdeepsleep=sleepData.deepsleep;
        Log.e("sleepData.totalsleep", sleepData.totalsleep);
        if(Integer.valueOf(sleepData.totalsleep)<360){  //曉魚六曉十
            sleepData.sleeptime="1";
            sleepData.insomnia="1";  //insomnia=1為失眠,0沒失眠
        }else{
            sleepData.sleeptime="0";
            sleepData.insomnia="0";}
        boolean isinsomnia =false;

        if(sleepData.insomnia.equals("1"))
            isinsomnia=true;
        boolean updateChecker = source.updatesleeptime(sleepData, "LastSleepData",nowDay);
        Log.e("LastSleepData", "updateChecker = " + updateChecker);
        /**************************************************************/
        if(sleepData.insomnia.equals("1"))
            isinsomnia=true;
        ca.add(Calendar.DATE, -1);//把之前的時間查詢下來
        sleepData = source.QueryRecordDate(String.valueOf(sf.format(ca.getTime()))); //上次時間=這次時間所做的事情
        if(sleepData.Date.equals(sf.format(ca.getTime()))){
            if(sleepData.insomnia.equals("1")){
                check2=true;
                Log.e(TAG, "check2"+String.valueOf(check2));
            }else{
                check2=false;
            }
            Log.e(TAG, sleepData.deepsleep +">"+ nowdeepsleep);
            if(Integer.parseInt(sleepData.deepsleep) > Integer.parseInt(nowdeepsleep)){
                sleepData.deeptime="1";  //深眠減少
                sleepData.insomnia="1";
            }else{
                sleepData.deeptime="0";  //深眠沒減少
                if(isinsomnia==true)
                    sleepData.insomnia="1";
            }
        }else{
            sleepData.deeptime="0";
            if(isinsomnia==true)
                sleepData.insomnia="1";
        }


        updateChecker = source.updatedeeptime(sleepData, "LastSleepData",nowDay);
        Log.e("LastSleepData", "updateChecker = " + updateChecker);

        /**************************************************************/
        if(sleepData.insomnia.equals("1"))
            isinsomnia=true;

        ca.add(Calendar.DATE, -1);
        sleepData = source.QueryRecordDate(String.valueOf(sf.format(ca.getTime())));
        if(sleepData.Date.equals(sf.format(ca.getTime()))){
            if(sleepData.insomnia.equals("1")){
                check3=true;
                Log.e(TAG, "check3"+String.valueOf(check3));
            }else{
                check3=false;  //沒打勾
            }
            if(check1==true && check2==true && check3==true){
                sleepData.weektime="1";  //連續三天獲三天以上失眠
                sleepData.insomnia="1";  //失眠一次
            }else{
                sleepData.weektime="0";
                if(isinsomnia==true)
                    sleepData.insomnia="1";

            }
        }else{
            sleepData.weektime="0";
            if(isinsomnia==true)
                sleepData.insomnia="1";
        }
        updateChecker = source.updateweektime(sleepData, "LastSleepData",nowDay);
        Log.e("LastSleepData", "updateChecker = " + updateChecker);
    }

    //螢幕亮度調整    *螢幕亮度的值為0-255
    protected void setBrightnessLevel(final int brightness_level) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf((brightness_level) * (1f / 255f));
        getWindow().setAttributes(lp);
    }
    //飛航模式
    public static boolean isAirplaneModeOn(Context context){
        return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }


    public void MADB(float x,float y,float z){//Mean of Absolute Difference

        int nowyear=0,nowmonth=0,nowday=0,nowminute=0,nowhour=0,nowsecond=0;
        String time = " ";
        String date = " ";

        SimpleDateFormat nowdate = new SimpleDateFormat("yyyy/MM/dd");
        nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        date = String.valueOf(nowdate.format(new java.util.Date()));
        String[] Date = date.split("/");

        nowyear=Integer.valueOf(Date[0]);
        nowmonth=Integer.valueOf(Date[1]);
        nowday=Integer.valueOf(Date[2]);


        SimpleDateFormat nowtime = new SimpleDateFormat("HH:mm:ss");
        //DateFormat nowtime = null;
        //==GMT標準時間往後加八小時
        nowtime.setTimeZone(TimeZone.getTimeZone("GMT+8"));//==取得目前時間
        time = String.valueOf(nowtime.format(new java.util.Date()));
        String[] Time = time.split(":");


        nowhour=Integer.valueOf(Time[0]);//hour 是24小時制
        nowminute=Integer.valueOf(Time[1]);
        nowsecond = Integer.valueOf(Time[2]);

        int starthour=0;
        if(nowhour>12)
            starthour=nowhour-12;
        else if(nowhour<12)
            starthour=nowhour;
        if(TemperMinute==-1)
        {
            startTime = nowminute+starthour*60;
            nowSleepTime = startTime;
        }
        else
            nowSleepTime = nowminute+starthour*60;


        float Xaverage = 0, Yaverage = 0 , Zaverage=0;
        int count=0;
        if(TemperMinute==nowminute){//過去時間==現在時間
            Xvalue[n] = x;//直接傳X值進來
            Yvalue[n] =y;//直接傳Y值進來
//    			Zvalue[n] = value[2];//直接傳Y值進來
            Xsum += Xvalue[n];//算加總
            Ysum += Yvalue[n];
//	    		Zsum += Zvalue[n];
            //Log.e("SumLog",Xsum+" "+Ysum);
            n++;
        }
        else{
            TemperMinute = nowminute;
            if(n>0)//下一個分鐘時
            {
                Xaverage = Xsum/n;
                Yaverage = Ysum/n;
//    			Zaverage = Zsum/n;
                Xsum = 0;
                Ysum = 0;
//		    	Zsum = 0;
                //Log.e("DifferenceLog","Xaverage = " + Xaverage +", Yaverage = "+ Yaverage);
                float X = 0;
                float Y = 0;
                float Z = 0;
                for(int i=0; i<n; i++)
                {
                    X += Math.abs(Xvalue[i]-Xaverage);
                    Y += Math.abs(Yvalue[i]-Yaverage);
//		    		Z += Math.abs(Zvalue[i]-Yaverage);
                }
                float AT=X/n;
                float AL=Y/n;
//	    		float AZ=Z/n;
                float PD = (float) (Math.pow((AT-TemperAT),2) + Math.pow((AL-TemperAL),2));
//	    				+ Math.pow((AZ-TemperAZ),2));

                AT=(float) ((AT-0.0066756685)/(4.9795303-0.0066756685));
                AL=(float) ((AL-0.0054766703)/(5.07229-0.0054766703));

                TemperAT=AT;
                TemperAL=AL;
//	    		TemperAZ=AZ;
                n = 0;

                float Quality = 0;

                //if(AT>0.207 && AL>0.09018 && PD>0.0239 ){//門檻值
                if(AT>0.040283569 && AL>0.016717279 && PD>0.002467704 ){//標準化門檻值
                    //0.09, 0.09, 0.005 原本
                    //0.2, 0.1, 0.08 露兩個翻身
                    //0.2, 0.029, 0.04
//		    		&& AZ>0.029
                    //AT>0.32 && AL>0.029 && PD>0.095 8/27
                    //AT>0.46 && AL>0.128 && PD>0.37 8/28 排除抓癢
//		    		AT>0.207 && AL>0.107 && PD>0.099 8/30
//		    		AT>0.207 && AL>0.107 && PD>0.0239 9_21
                    if(lastRolloverTime == -1){
                        lastRolloverTime = nowSleepTime;  // set last roll over time if first roll over
                        Log.e("lastRolloverTime","Time = " + lastRolloverTime);
                    }
                    else
                    {
                        SleepTimeBetweenRollOver = nowSleepTime - lastRolloverTime;//此區間值是翻身區間時間
                        Log.e("lastRolloverTime","nowSleepTime = " + nowSleepTime);
                        Log.e("lastRolloverTime","lastRolloverTime = " + lastRolloverTime);
                        if(SleepTimeBetweenRollOver > 20) // deep sleep門檻值
                        {
                            DeepSleepduration += SleepTimeBetweenRollOver; // total deep sleep time(不停累加深眠時間)
                        }
                        else
                        {
                            Lightsleepduration += SleepTimeBetweenRollOver; // total light sleep time
                        }
                        Log.e(TAG,"DeepSleepduration = " + DeepSleepduration);
                        Log.e(TAG,"Lightsleepduration = " + Lightsleepduration);
                        lastRolloverTime = nowSleepTime;  // update the last roll over time
                    }
                    count++; // 翻身次數有翻身++

                }
                int checkIfZero = DeepSleepduration + Lightsleepduration;
                if(checkIfZero != 0)
                    Quality = (float)DeepSleepduration/(checkIfZero);//計算睡眠品質
                else
                    Quality = 0;


                // compute the sleep quality by (deep sleep time)/(total sleep time)
                Log.e("Sleep Quality","Quality = " + Quality);
                //saveLogin(nowyear,nowmonth,nowday,nowhour,nowminute, AT, AL, PD, count, Quality);

                sleepData.ID = String.valueOf(1);
                Calendar ca = Calendar.getInstance();//得到一個Calendar的實例
                SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
                ca.set(nowyear, nowmonth-1, nowday);
                sleepData.Date = String.valueOf(sf.format(ca.getTime()));
                Log.e(TAG, sleepData.Date);
                sleepData.Time = nowhour + ":" + nowminute + ":" + nowsecond;
                sleepData.AT = String.valueOf(AT);
                sleepData.AL = String.valueOf(AL);
//	    		sleepData.AZ = String.valueOf(AZ);
                sleepData.PD = String.valueOf(PD);
                sleepData.rolloverCount = String.valueOf(count);
                sleepData.sleepQuality = String.valueOf(Quality);
                sleepData.totalsleep = String.valueOf(DeepSleepduration + Lightsleepduration);
                sleepData.deepsleep = String.valueOf(DeepSleepduration);
                //取得震動服務
                //Vibrator myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
                //myVibrator.vibrate(1000);
                Toast.makeText(this,"rolloverCount: "+String.valueOf(count),Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"AT: "+String.valueOf(AT),Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"AL: "+String.valueOf(AL),Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"PD: "+String.valueOf(PD),Toast.LENGTH_SHORT).show();
                // Do INSERT
                boolean insertChecker = source.insertSleepDirect("SleepDirect", sleepData);//加AZ到DB
                Log.e("Sleep Quality","insertChecker = " + insertChecker);

                file1 = new File("/sdcard/ElderCare/database/","elderlycare.db");
            }
        }



    }
    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onPause(){

        super.onPause();
//	    sensorManager.unregisterListener(this);
    }
    @Override
    protected void onResume() {//Return to your app after user authorization
        super.onResume();

        // ...

        myDropboxTool.doOnResume();

        // ...
    }
    @Override
    public void onDestroy(){
        super.onDestroy();

    }
    //copy file when first time open APP
    public void copyDBtoSDCard() {
        try {

            String tSDCardPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

            //File tDataPath = new File(tSDCardPath+"/mydb/");
            File tDataPath = new File(Var.DBPATH);

            String tDBFilePath = tDataPath + Var.DBNAME;
            File tFile = new File(tDBFilePath);

            if (tDataPath.exists() == false) {
                Log.e("CopyDBException","tDataPath start");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();//按返回鍵，則執行退出確認


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void ConfirmExit(){//退出確認
        AlertDialog.Builder ad=new AlertDialog.Builder(SleepDetection.this);
        ad.setTitle("離開");
        ad.setMessage("確定要離開?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {//退出按鈕
            public void onClick(DialogInterface dialog, int i) {
                // TODO Auto-generated method stub
               // sensorManager.unregisterListener(SleepDetection.this);
                Intent intent = new Intent();
                intent.setClass(SleepDetection.this,MainActivity.class);
                startActivity(intent);
                SleepDetection.this.finish();//關閉activity
            }
        });
        ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                //不退出不用執行任何操作
            }
        });
        ad.show();//示對話框
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sleep_status_detection_main, menu);
        return true;
    }

}
