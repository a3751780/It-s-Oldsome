package com.example.user.eldercare;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.example.user.database.MyDBHelper;
import com.example.user.database.Var;


public class SleepFragment extends Fragment {


    MyDBHelper source;
    private final String SAVE_DIR = "/sdcard/ElderCare/database/";
    ImageButton ib;
    DataObject dateo =new DataObject();

    public SleepFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.fragment_sleep, container, false);

        ib=(ImageButton)rootview.findViewById(R.id.ib1);


       // source = new MyDBHelper(getActivity(), Var.DBPATH + Var.DBNAME, null, 0);
       // copyDBtoSDCard();


      /*  try{
            download();
            dateo = source.QueryDate();
            //		if(){
            //			notification();
            //		}
        }catch(Exception ex){
            //Log.e(TAG,ex.toString());
        }*/
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.setClass(getActivity(), SleepDetection.class);
                startActivity(i);

            }
        });

        // Inflate the layout for this fragment
        return rootview;

    }


    public void download(){

        if(check_SD())
        {

            String url = "";
            url = "https://dl.dropboxusercontent.com/s/engb8pvsmv98raj/elderlycare.db?token_hash=AAHkoouz32RL8zkbasiZ0TvWaEWwUVD-zm_TsfeppBB4dA&dl=1";
            File myTempFile = new File("/sdcard/ElderCare/database/elderlycare.db");
            getNotificationSound(url, "elderlycare.db");

            Toast.makeText(getActivity(), "下載成功", Toast.LENGTH_SHORT).show();

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
            Log.e("CopyDBException", e.getMessage());
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
