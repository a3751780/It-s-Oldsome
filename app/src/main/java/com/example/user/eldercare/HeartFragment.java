package com.example.user.eldercare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.Dropbox.DBRoulette;
import com.example.user.database.MyDBHelper;
import com.example.user.database.Var;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class HeartFragment extends Fragment {
    int nowyear=0,nowmonth=0,nowday=0,nowminute=0,nowhour=0,nowsecond=0;
    private final static String MSG_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    Calendar cal = Calendar.getInstance();
    String dat;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat nowtime = new SimpleDateFormat("HH:mm:ss");
    String date = " ";
    String time = " ";
    EditText et,et2;
    String[] Date;
    String[] Id;
    Button b2;
    ImageButton b3;
    int temhigh=100;
    int temlow=55;
    private BandClient client = null;
    TextView tv1;
    TextView tv9;
    TextView heart,heartq;
    boolean b=true;
    HeartData heartData=new HeartData();
    MyDBHelper source;
    private final String SAVE_DIR = "/sdcard/ElderCare/database/";
    File file1;
    File file2;
    private DBRoulette myDropboxTool ;

    public HeartFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        copyDBtoSDCard();
        final View rootView = inflater.inflate(R.layout.fragment_heart, container, false);

        tv9=(TextView)rootView.findViewById(R.id.textView9);
        b2=(Button)rootView.findViewById(R.id.b2);
        tv1=(TextView)rootView.findViewById(R.id.tv1);
        heart=(TextView)rootView.findViewById(R.id.heart);
        heartq=(TextView)rootView.findViewById(R.id.heartq);


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog set=new Dialog(getActivity());
                set.setTitle("重新設定門檻值");
                set.setContentView(R.layout.dialog_layout);

                et=(EditText)set.findViewById(R.id.temhigh);
                et2=(EditText)set.findViewById(R.id.temlow);
                b3=(ImageButton)set.findViewById(R.id.b3);


                b3.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){

                        String temh,teml;

                        temh=et.getText().toString();
                        teml=et2.getText().toString();



                        temhigh=Integer.valueOf(temh);
                        temlow=Integer.valueOf(teml);
                        set.dismiss();

                    }
                });

                set.show();
            }


        });

        heart.setText("");
        heartq.setText("");

        source = new MyDBHelper(getActivity(), Var.DBPATH + Var.DBNAME, null, 0);

        myDropboxTool  = new DBRoulette(getActivity());

        new appTask().execute();


        return rootView;
    }

    public void Heartdown(String string){
        file1 = new File("/sdcard/ElderCare/database/","elderlycare.db");

        try {

    if(b==true) {

    if ((Integer.parseInt(string)) > temhigh) {


        b=false;
        send();

        Toast.makeText(getActivity(), "你好像有點喘", Toast.LENGTH_LONG).show();
        Log.e("心律不整", "00");
        Id = source.QueryH("Heartdata");
        dat=String.valueOf(sf.format(cal.getTime()));

        sf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        date = String.valueOf(sf.format(new java.util.Date()));
        Date = date.split("/");
        nowyear=Integer.valueOf(Date[0]);
        nowmonth=Integer.valueOf(Date[1]);
        nowday=Integer.valueOf(Date[2]);


        nowtime.setTimeZone(TimeZone.getTimeZone("GMT+8"));//==取得目前時間
        time = String.valueOf(nowtime.format(new java.util.Date()));
        String[] Time = time.split(":");
        nowhour=Integer.valueOf(Time[0]);//hour 是24小時制
        nowminute=Integer.valueOf(Time[1]);
        nowsecond = Integer.valueOf(Time[2]);

        int x= Id.length-1;
        int x1=Integer.valueOf(Id[x])+1;
        String x2=String.valueOf(x1);
        heartData.ID=x2;
        heartData.Heart=string;
        heartData.Date=dat;
        heartData.Time=nowhour + "點:" + nowminute + "分:" + nowsecond+"秒";
        boolean heart=source.insertHeartData("Heartdata", heartData);
        Log.e("心律不整", "insertHeartData = " + heart); //更新
        Toast.makeText(getActivity(),dat,Toast.LENGTH_LONG).show();
        Toast.makeText(getActivity(),nowhour + "點:" + nowminute + "分:" + nowsecond+"秒",Toast.LENGTH_LONG).show();
        Toast.makeText(getActivity(),string,Toast.LENGTH_LONG).show();

        boolean updateHeart=source.updateHeart( heartData,"Heartdata",heartData.Date,heartData.Time);
        Log.e("心律不整", "updateHeart = " + updateHeart); //更新

    /*    if(heart==true){
         myDropboxTool.sentFile(SAVE_DIR, file1);//上傳檔案output
         myDropboxTool.sentFile(SAVE_DIR, file2);//上傳檔案data
        }*/
     


    } else if ((Integer.parseInt(string)) < temlow) {

        b=false;
        send();
        Toast.makeText(getActivity(), "你心率不太穩定喔", Toast.LENGTH_LONG).show();
        Log.e("心律不整", "00");
        Id = source.QueryH("Heartdata");
        dat=String.valueOf(sf.format(cal.getTime()));

        sf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        date = String.valueOf(sf.format(new java.util.Date()));
        Date = date.split("/");
        nowyear=Integer.valueOf(Date[0]);
        nowmonth=Integer.valueOf(Date[1]);
        nowday=Integer.valueOf(Date[2]);


        nowtime.setTimeZone(TimeZone.getTimeZone("GMT+8"));//==取得目前時間
        time = String.valueOf(nowtime.format(new java.util.Date()));
        String[] Time = time.split(":");
        nowhour=Integer.valueOf(Time[0]);//hour 是24小時制
        nowminute=Integer.valueOf(Time[1]);
        nowsecond = Integer.valueOf(Time[2]);
        int x= Id.length-1;
        int x1=Integer.valueOf(Id[x])+1;
        String x2=String.valueOf(x1);

        heartData.ID=x2;
        heartData.Heart=string;
        heartData.Date=dat;
        heartData.Time=nowhour + "點:" + nowminute + "分:" + nowsecond+"秒";
        boolean heart=source.insertHeartData("Heartdata", heartData);
        Log.e("心律不整", "insertHeartData = " + heart); //更新
        Toast.makeText(getActivity(),dat,Toast.LENGTH_LONG).show();
        Toast.makeText(getActivity(),nowhour + "點:" + nowminute + "分:" + nowsecond+"秒",Toast.LENGTH_LONG).show();
        Toast.makeText(getActivity(),string,Toast.LENGTH_LONG).show();

        boolean updateHeart=source.updateHeart( heartData,"Heartdata",heartData.Date,heartData.Time);
        Log.e("心律不整", "updateHeart = " + updateHeart); //更新

      /*  if(heart==true){
            myDropboxTool.sentFile(SAVE_DIR, file1);//上傳檔案output
            myDropboxTool.sentFile(SAVE_DIR, file2);//上傳檔案data
        }*/
        
    }
        //tv9.setText(nowday);
        }

        }
        catch(Exception ex){

        }

    }
    private class appTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            try {
                if (getConnectedBandClient()) {
                    appendToUI("Band is connected.\n");


                    if(client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        client.getSensorManager().registerHeartRateEventListener(mHeart);

                    } else {

                        client.getSensorManager().requestHeartRateConsent(getActivity(),mHeartRateConsentListener);
                    }

                } else {
                    appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
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
                appendToUI(exceptionMessage);

            } catch (Exception e) {
                appendToUI(e.getMessage());
            }
            return null;
        }
    }

    private void appendToUI(final String string) {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv1.setText(string);
            }
        });
    }

    private void appendToUIh(final String string,final String s1) {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                heart.setText(string);
                heartq.setText(s1);
            }
        });
    }

    private BandHeartRateEventListener mHeart=new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(BandHeartRateEvent bandHeartRateEvent) {
            if(bandHeartRateEvent != null){
                appendToUIh(String.valueOf(bandHeartRateEvent.getHeartRate()), bandHeartRateEvent.getQuality().toString());

                Heartdown(String.valueOf(bandHeartRateEvent.getHeartRate()));

            }
        }
    };
    private void send(){

        SmsManager smsManager = SmsManager.getDefault();
        try{
            smsManager.sendTextMessage("0952521648",
                    null,
                    "注意注意!203-1老人心跳異常，跳太快了!", PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, new Intent(), 0), null);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            try {
                Bundle msg = intent.getExtras();
                Object[] pdus = (Object[]) msg.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < messages.length; i++) {
                    if (intent.getAction().equals(MSG_RECEIVED)) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        String m = messages[i].getMessageBody().toString();
                        if (messages[i].getDisplayOriginatingAddress().contains(
                                "0939104951")) {
                            String array[] = m.split(":");
                            String loc[] = array[1].split(",");
                            //  mTextHeartRate[] = loc[0];
                            // mTextHeartRateQuality[] = loc[1];
                            //showmap();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    };

    private HeartRateConsentListener mHeartRateConsentListener = new HeartRateConsentListener() {
        @Override
        public void userAccepted(boolean b) {

        }
    };
    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                appendToUI("Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(getActivity().getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        appendToUI("Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }

    public void notification(){


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }
    @Override
    public void onResume() {//Return to your app after user authorization
        super.onResume();

        // ...

        myDropboxTool.doOnResume();

        // ...
    }
    @Override
    public void onDetach() {
        super.onDetach();

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
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
}
