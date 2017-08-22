package com.example.user.eldercare;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;

import com.microsoft.band.BandException;

import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.SampleRate;


import com.microsoft.band.sensors.HeartRateConsentListener;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectFragment extends Fragment implements FragmentListener {


    private TextView txtStatus;
    private TextView tem,heart,heartq;
    private BandClient client = null;
    float x;
    private ImageButton ib2;

    public ConnectFragment() {

    }

    public final void onFragmentSelected() {
        if (isVisible()) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);

        txtStatus = (TextView) rootView.findViewById(R.id.txtStatus);
        tem = (TextView) rootView.findViewById(R.id.tem);
        heart=(TextView)rootView.findViewById(R.id.heart);
        heartq=(TextView)rootView.findViewById(R.id.heartq);
        ib2=(ImageButton)rootView.findViewById(R.id.ib2);

        txtStatus.setText("");
        tem.setText("");
        heart.setText("");
        heartq.setText("");

        new appTask().execute();

        ib2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.setClass(getActivity(), CheckDateActivity.class);
                startActivity(i);

            }

        });
        return rootView;
    }

    @Override

    public void onResume() {
        super.onResume();
        txtStatus.setText("");
        tem.setText("");
        heart.setText("");
        heartq.setText("");
    }
    @Override

    public void onPause() {
        super.onPause();

        if (client != null) {
            try {
                client.getSensorManager().unregisterAccelerometerEventListeners();
                client.getSensorManager().unregisterHeartRateEventListeners();
            } catch (BandIOException e) {
                appendToUI(e.getMessage());
            }
        }
    }


    private class appTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            try {
                if (getConnectedBandClient()) {
                    appendToUI("Band is connected.\n");
                    client.getSensorManager().registerAccelerometerEventListener(mAccelerometerEventListener, SampleRate.MS128);
                    client.getSensorManager().registerSkinTemperatureEventListener(mSkin);

                    if(client.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        client.getSensorManager().registerHeartRateEventListener(mHeart);
                    } else {
// user has not consented yet, request it

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
                txtStatus.setText(string);
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
    private void appendToUIh(final String string,final String s1) {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                heart.setText(string);
                heartq.setText(s1);

            }
        });
    }

    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {
            if (event != null) {
                appendToUI(String.format(" X = %.3f \n Y = %.3f\n Z = %.3f", event.getAccelerationX(),
                        event.getAccelerationY(), event.getAccelerationZ()));
                x =event.getAccelerationX();


            }
        }
    };

    private BandSkinTemperatureEventListener mSkin=new BandSkinTemperatureEventListener() {
        @Override
        public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent bandSkinTemperatureEvent) {
            if (bandSkinTemperatureEvent != null) {
                appendToUIs(String.format("溫度=%.1f", bandSkinTemperatureEvent.getTemperature()));

            }
        }
    };



    private BandHeartRateEventListener mHeart=new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(BandHeartRateEvent bandHeartRateEvent) {
            if(bandHeartRateEvent != null){
                appendToUIh(String.valueOf(bandHeartRateEvent.getHeartRate()), bandHeartRateEvent.getQuality().toString());
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

    //
    // If there are multiple bands, the "choose band" button is enabled and
    // launches a dialog where we can select the band to use.
    //
   /* private OnClickListener a= new OnClickListener() {
        @Override
        public void onClick(View button) {

            txtStatus.setText("");
            tem.setText("");

            new appTask().execute();
        }
    };*/

}