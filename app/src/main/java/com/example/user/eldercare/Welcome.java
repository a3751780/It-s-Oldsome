package com.example.user.eldercare;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class Welcome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 3000);//3秒跳轉
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int GOTO_MAIN_ACTIVITY = 0;

    private Handler mHandler = new Handler(){

        public void handleMessage(android.os.Message msg) {



            switch (msg.what) {

                case GOTO_MAIN_ACTIVITY:

                    Intent intent = new Intent();

                    intent.setClass(Welcome.this, MainActivity.class);

                    startActivity(intent);

                    finish();

                    break;



                default:

                    break;

            }

        };

    };




}
