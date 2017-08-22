package com.example.user.eldercare;

import com.microsoft.band.BandClient;

public class Model {

    private static Model INSTANCE = new Model();

    public static Model getInstance() {
        return INSTANCE;
    }

    private BandClient mClient;

    public BandClient getClient() {
        return mClient;
    }

    public void setClient(BandClient client) {
        mClient = client;
    }

    public boolean isConnected() {
        return (mClient != null) && mClient.isConnected();
    }
}

