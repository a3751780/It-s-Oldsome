package com.example.user.eldercare;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.database.MyDBHelper;
import com.example.user.database.Var;

public class CheckDateActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = "BackOfDateActivity";
    TextView t2, t4, t5, t6;
    Button b1;
    MyDBHelper source;
    DataObject date = new DataObject();
    private final String SAVE_DIR = "/sdcard/ElderCare/database/";

    private Button mButton01;
    private String TAG1 = "HIPPO";
    private SurfaceView mSurfaceView01;
    private SurfaceHolder mSurfaceHolder01;
    private  EditText mEditText01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_date);
        source = new MyDBHelper(this, Var.DBPATH + Var.DBNAME, null, 0);
        findViewById();

        try {
            download();
            date = source.QueryDate();
            t2.setText(String.valueOf(date.Year) + "/"
                    + String.valueOf(date.MonthOfYear + 1) + "/"
                    + String.valueOf(date.DayOfMonth));
            t4.setText(String.valueOf(date.NextYear) + "/"
                    + String.valueOf(date.NextMonthOfYear) + "/"
                    + String.valueOf(date.NextDayOfMonth));

            onTimeSet(date.MonthOfYear, date.DayOfMonth);
            CalDistance();
        } catch (Exception ex) {

        }




		/*mButton01 = (Button) findViewById(R.id.myButton1);
		mButton01.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

					Intent intent = new Intent();
		           	intent.setClass(CheckDateActivity.this,CreatQrcodeMedicineList.class);
		           	startActivity(intent);
		           	CheckDateActivity.this.finish();

			}
		});*/
        mEditText01 = (EditText) findViewById(R.id.myEditText1);
        mEditText01.setCursorVisible(false);
        mEditText01
                .setText("Acetaminophan,cetirizine,Pseudoephedrine,Acetaminophan,cetirizine,Pseudoephedrine");
        mEditText01.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });


		/* 取得螢幕解析像素 */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

		/* 以SurfaceView作為相機Preview之用 */
        mSurfaceView01 = (SurfaceView) findViewById(R.id.mSurfaceView1);

		/* 繫結SurfaceView，取得SurfaceHolder物件 */
        mSurfaceHolder01 = mSurfaceView01.getHolder();

		/* Activity必須實作SurfaceHolder.Callback */
        mSurfaceHolder01.addCallback(CheckDateActivity.this);



		/* 產生QRCode的按鈕事件處理 */
        mButton01 = (Button)findViewById(R.id.myButton1);
        mButton01.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                // TODO Auto-generated method stub
                if(mEditText01.getText().toString()!="")
                {
				/* 傳入setQrcodeVersionz範圍1-40，取值越大尺寸越大，儲存訊息越多 */
                    AndroidQREncode(mEditText01.getText().toString(), 5);
                }
            }
        });



    }

    public void CalDistance() {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Calendar c0 = Calendar.getInstance();
            Calendar c1 = Calendar.getInstance();

            c1.setTimeInMillis(System.currentTimeMillis());
            Date dt = null;
            dt = sdf.parse(String.valueOf(date.NextYear) + "/"
                    + String.valueOf(date.NextMonthOfYear) + "/"
                    + String.valueOf(date.NextDayOfMonth));
            c0.setTime(dt);// 或是設定指定時間

            int d1 = c1.get(Calendar.DAY_OF_YEAR);
            int d2 = c0.get(Calendar.DAY_OF_YEAR);

            int dayDiff = d2 - d1;

            String text = String.valueOf(dayDiff) + "天";
            t6.setText(String.valueOf(text));

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void onTimeSet(int monthOfDay, int DayOfhour) {
        // TODO Auto-generated method stub
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_MONTH, DayOfhour);
        calendar.set(Calendar.MONTH, monthOfDay);

        // Intent intent = new Intent("AlarmReceiver");
        Intent intent;
        PendingIntent pendingIntent = null;
        try {
            intent = new Intent(CheckDateActivity.this, AlarReceiver.class);
            intent.setClass(this, AlarReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        } catch (Exception e) {
            Log.e(TAG, "error");
        }

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // /*設置鬧鐘 5秒後回應*/
        // am.set(AlarmManager.RTC_WAKEUP,
        // Calendar.getInstance().getTimeInMillis()+1000*5, pendingIntent);
		/* 設置週期 */
        // int internal = Integer.valueOf(date.Internal);
        // am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+internal*24*60*60*1000,(long)(24*60*60*1000),
        // pendingIntent);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                5 * 1000, pendingIntent);// 每5秒執行一次
        // am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() 4 * 1000 ,
        // pendingIntent); //4秒後執行
        Log.e(TAG, "收到訊息");
        // Log.e(TAG, String.valueOf(System.currentTimeMillis())+", "+internal);
    }

    public void findViewById() {
        t2 = (TextView) this.findViewById(R.id.textView1);
        t4 = (TextView) this.findViewById(R.id.textView4);
        t5 = (TextView) this.findViewById(R.id.textView5);
        t6 = (TextView) this.findViewById(R.id.textView6);
        b1 = (Button) this.findViewById(R.id.button1);
    }

    public void download() {

        if (check_SD()) {
            String url = "";
            url = "https://dl.dropboxusercontent.com/s/engb8pvsmv98raj/elderlycare.db?token_hash=AAHkoouz32RL8zkbasiZ0TvWaEWwUVD-zm_TsfeppBB4dA&dl=1";
            File myTempFile = new File(
                    "/sdcard/ElderCare/database/elderlycare.db");
            getNotificationSound(url, "elderlycare.db");
            Log.e(TAG, "下載成功");
            Toast.makeText(this, "下載成功", Toast.LENGTH_SHORT).show();

        }
    }

    public void getNotificationSound(final String url, final String filename) {
        Thread threadgetFile = new Thread(new Runnable() {
            public void run() {
                try {
                    URL myURL = new URL(url);
                    try {
                        URLConnection conn = myURL.openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        if (is == null) {
                            Log.e(TAG, "stream is null");
                        }
                        File myTempFile = new File(
                                "/sdcard/ElderCare/database/" + filename);
                        FileOutputStream fos = new FileOutputStream(myTempFile);
                        byte buf[] = new byte[128];
                        do {
                            int numread = is.read(buf);
                            if (numread <= 0) {
                                break;
                            }
                            fos.write(buf, 0, numread);
                        } while (true);
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

    public boolean check_SD() {
        String strNotificationSoundFolder = Environment
                .getExternalStorageDirectory().getParent()
                + "/"
                + Environment.getExternalStorageDirectory().getName()
                + "/ElderCare/database/";

        File vPath = new File(strNotificationSoundFolder);
        if (!vPath.exists()) {
            if (vPath.mkdirs()) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_date, menu);
        return true;
    }

    /* 自訂產生QR Code的函數 */
    public  void AndroidQREncode(String strEncoding, int qrcodeVersion) {
        try {
			/* 建構QRCode編碼物件 */
            com.swetake.util.Qrcode testQrcode = new com.swetake.util.Qrcode();
			/* L','M','Q','H' */
            testQrcode.setQrcodeErrorCorrect('M');
			/* "N","A" or other */
            testQrcode.setQrcodeEncodeMode('B');
			/* 0-20 */
            testQrcode.setQrcodeVersion(qrcodeVersion);

            // getBytes
            byte[] bytesEncoding = strEncoding.getBytes("utf-8");

            if (bytesEncoding.length > 0 && bytesEncoding.length < 1000) {
				/* 將字串透過calQrcode函數轉換成boolean陣列 */
                boolean[][] bEncoding = testQrcode.calQrcode(bytesEncoding);
				/* 依據編碼後的boolean陣列，繪圖 */
                drawQRCode(bEncoding, getResources().getColor(R.color.black));
            }
        } catch (Exception e) {
            Log.i("HIPPO", Integer.toString(mEditText01.getText().length()));
            e.printStackTrace();
        }
    }

    /* 在SurfaceView上繪製QRCode圖片 */
    public  void drawQRCode(boolean[][] bRect, int colorFill) {
		/* test Canvas */
        int intPadding = 20;

		/* 欲在SurfaceView上繪圖，需先lock鎖定SurfaceHolder */
        Canvas mCanvas01 = mSurfaceHolder01.lockCanvas();

		/* 設定畫布繪製顏色 */
        mCanvas01.drawColor(getResources().getColor(R.color.yellow));

		/* 建立畫筆 */
        Paint mPaint01 = new Paint();

		/* 設定畫筆顏色及樣式 */
        mPaint01.setStyle(Paint.Style.FILL);
        mPaint01.setColor(colorFill);
        mPaint01.setStrokeWidth(1.0F);

		/* 逐一載入2維boolean陣列 */
        for (int i = 0; i < bRect.length; i++) {
            for (int j = 0; j < bRect.length; j++) {
                if (bRect[j][i]) {
					/* 依據陣列值，繪出條碼方塊 */
                    mCanvas01.drawRect(new Rect(intPadding + j * 6 + 10,
                            intPadding + i * 6 + 10, intPadding + j * 6 + 10 + 6,
                            intPadding + i * 6 + 10 + 6), mPaint01);
                }
            }
        }
        mSurfaceHolder01.unlockCanvasAndPost(mCanvas01);
    }

    public void mMakeTextToast(String str, boolean isLong) {
        if (isLong == true) {
            Toast.makeText(CheckDateActivity.this, str, Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(CheckDateActivity.this, str, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w,
                               int h) {
        // TODO Auto-generated method stub
        Log.i(TAG1, "Surface Changed");
    }

    public void surfaceCreated(SurfaceHolder surfaceholder) {
        // TODO Auto-generated method stub
        Log.i(TAG1, "Surface Changed");
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        // TODO Auto-generated method stub
        Log.i(TAG1, "Surface Destroyed");
    }




}