package br.com.jortec.mide.service;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import br.com.jortec.mide.LoginSegundoActivity;

/**
 * Created by Jorliano on 12/03/2016.
 */


public class LocalizacaoGps implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    Context context = null;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String  longitude;
    private String latitude;


    public LocalizacaoGps(Context context) {
        this.context = context;

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            startLocationUpdate();
        }

        callConection();

    }

    private synchronized void callConection() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(8000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdate() {
        initLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.i("LOG", "logitude isConnected  ");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("LOG", "onConection " + bundle);

        Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if (l != null) {
            Log.i("LOG", "logitude " + l.getLongitude());
            Log.i("LOG", "latitude " + l.getLatitude());

            longitude = String.valueOf(l.getLongitude());
            latitude =  String.valueOf(l.getLatitude());
        }

        startLocationUpdate();


    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("LOG", "logitude chamado" );
        Log.i("LOG", "logitude " + location.getLongitude());
        Log.i("LOG", "latitude " + location.getLongitude());
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}