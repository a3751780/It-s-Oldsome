package com.example.user.eldercare;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
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
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class clothFragment extends Fragment {

    int nowyear=0,nowmonth=0,nowday=0,nowminute=0,nowhour=0,nowsecond=0;
    private final static String MSG_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    boolean x=true;
    TextView t1,tem;
    private BandClient client = null;
    Calendar cal = Calendar.getInstance();
    String dat;
    ImageButton b3;
    double temhigh=32.0;
    double temlow=26.0;
    Button b2;
    SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
    String date = " ";
    String time = " ";
    String[] Date;
    String [] Id;
    EditText et,et2;
    SimpleDateFormat nowtime = new SimpleDateFormat("HH:mm:ss");

    private DBRoulette myDropboxTool ;

    TemData temdata=new TemData();
    MyDBHelper source;
    private final String SAVE_DIR = "/sdcard/ElderCare/database/";
    File file1;
    File file2;


    public clothFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cloth, container, false);
        source = new MyDBHelper(getActivity(), Var.DBPATH + Var.DBNAME, null, 0);
        t1=(TextView)rootView.findViewById(R.id.t1);
        tem=(TextView)rootView.findViewById(R.id.tem);
        tem.setText("");
        b2=(Button)rootView.findViewById(R.id.b2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog set = new Dialog(getActivity());
                set.setTitle("重新設定門檻值");
                set.setContentView(R.layout.dialog_layout2);

                et = (EditText) set.findViewById(R.id.temhigh);
                et2 = (EditText) set.findViewById(R.id.temlow);
                b3 = (ImageButton) set.findViewById(R.id.b3);


                b3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String temh, teml;

                        temh = et.getText().toString();
                        teml = et2.getText().toString();


                        temhigh = Double.valueOf(temh);
                        temlow = Double.valueOf(teml);
                        set.dismiss();

                    }
                });

                set.show();
            }


        });


        new appTask().execute();
        copyDBtoSDCard();
        myDropboxTool  = new DBRoulette(getActivity());

        return rootView ;

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }


    public void cloth(String string){
        file1 = new File("/sdcard/ElderCare/database/","elderlycare.db");



    try {
        if(x==true) {
        if ((Double.parseDouble(string)) >temhigh) {

            x=false;
            send();

            Id=source.QueryT("TemData");

            Toast.makeText(getActivity(), "衣服穿太多囉", Toast.LENGTH_LONG).show();
            Log.e("添衣", "00");
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


            temdata.ID=x2;
            temdata.time=nowhour + "點:" + nowminute + "分:" + nowsecond+"秒";
            temdata.tem=string;
            temdata.Date=dat;

            Toast.makeText(getActivity(), string+temdata.time+ temdata.Date, Toast.LENGTH_LONG).show();

            boolean tem=source.insertTemData("TemData", temdata);
            Log.e("添衣", "insertTemData = " + tem); //更新

         /*   if(tem==true){
                myDropboxTool.sentFile(SAVE_DIR, file1);//上傳檔案output
                myDropboxTool.sentFile(SAVE_DIR, file2);//上傳檔案data

            }*/


            Log.e("簡訊", "sended");
            Toast.makeText(getActivity(),"已傳送",Toast.LENGTH_LONG).show();

        }

        else if ((Double.parseDouble(string)) <temlow) {
            x=false;
            send();
            Id=source.QueryT("TemData");
            Toast.makeText(getActivity(), "多加衣服喔", Toast.LENGTH_LONG).show();
            Log.e("添衣", "00");

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

            temdata.ID=x2;
            temdata.time=nowhour + "點:" + nowminute + "分:" + nowsecond+"秒";
            temdata.tem=string;
            temdata.Date=dat;

            boolean tem=source.insertTemData("TemData", temdata);
            Log.e("添衣", "insertTemData = " + tem); //更新
            Toast.makeText(getActivity(),dat,Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(),nowhour + "點:" + nowminute + "分:" + nowsecond+"秒",Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(),string,Toast.LENGTH_LONG).show();

          /*  if(tem==true){
                myDropboxTool.sentFile(SAVE_DIR, file1);//上傳檔案output
                myDropboxTool.sentFile(SAVE_DIR, file2);//上傳檔案data
            }*/


            Log.e("簡訊", "sended");
            Toast.makeText(getActivity(),"已傳送",Toast.LENGTH_LONG).show();

        }


    }

    }
    catch (Exception ex) {

    }


    }
    private class appTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            try {
                if (getConnectedBandClient()) {

                    appendToUI("Band is connected.\n");

                    client.getSensorManager().registerSkinTemperatureEventListener(mSkin);


                }
                else {

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
                t1.setText(string);
            }
        });
    }

    private void appendToUIs(final String string) {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tem.setText(string);

            }
        });
    }

    private void send(){

        SmsManager smsManager = SmsManager.getDefault();
        try{
            smsManager.sendTextMessage("0952521648",
                    null,
                    "請注意!201-1老人有體溫過高的趨勢!", PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, new Intent(), 0), null);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private BandSkinTemperatureEventListener mSkin=new BandSkinTemperatureEventListener() {
        @Override
        public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent bandSkinTemperatureEvent) {

            if (bandSkinTemperatureEvent != null) {

                appendToUIs(String.format("%.1f 度", bandSkinTemperatureEvent.getTemperature()));

                cloth(String.format("%.1f", bandSkinTemperatureEvent.getTemperature()));
                Toast.makeText(getActivity(),String.format("%.1f", bandSkinTemperatureEvent.getTemperature()), Toast.LENGTH_LONG).show();


            }
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

}