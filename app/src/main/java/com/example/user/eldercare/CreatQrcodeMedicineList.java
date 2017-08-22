package com.example.user.eldercare;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreatQrcodeMedicineList extends Activity implements SurfaceHolder.Callback{


    private String TAG1 = "HIPPO";
    private SurfaceView mSurfaceView01;
    private SurfaceHolder mSurfaceHolder01;
    private  EditText mEditText01;
    private Button mButton01;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_qrcode);

        mEditText01 = (EditText) findViewById(R.id.myEditText1);
        mEditText01.setCursorVisible(false);
        mEditText01
                .setText("Acetaminophan,cetirizine,Pseudoephedrine,Acetaminophan,cetirizine,Pseudoephedrine," +
                        "Acetaminophan,cetirizine,Pseudoephedrine,Acetaminophan,cetirizine,Pseudoephedrine");
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
        mSurfaceHolder01.addCallback(CreatQrcodeMedicineList.this);



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
                    AndroidQREncode(mEditText01.getText().toString(), 10);
                }
            }
        });




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
                drawQRCode(bEncoding, getResources().getColor(R.color.white));
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
        mCanvas01.drawColor(getResources().getColor(R.color.white));

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
            Toast.makeText(CreatQrcodeMedicineList.this, str, Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(CreatQrcodeMedicineList.this, str, Toast.LENGTH_SHORT)
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
