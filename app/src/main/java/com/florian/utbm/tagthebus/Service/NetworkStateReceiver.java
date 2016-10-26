package com.florian.utbm.tagthebus.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Fonctino responsable to call the NetworkStateReceiverListenener when there is a change on the connection
 * Created by Florian on 26/10/2016.
 */

public class NetworkStateReceiver extends BroadcastReceiver {
    /**
     * List all the NetworkStateReceiverListener who want to be call were there is a change
     */
    protected List<NetworkStateReceiverListener> listeners;
    /**
     * true there is a connection
     * false there is no connection
     */
    protected Boolean connected;

    /**
     * default constructor
     */
    public NetworkStateReceiver() {
        listeners = new ArrayList<NetworkStateReceiverListener>();
        connected = null;
    }

    /**
     * Fonction call when there is a Broadcast
     * @param context
     * @param intent
     */
    public void onReceive(Context context, Intent intent) {
        if(intent == null || intent.getExtras() == null)
            return;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();

        if(ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            connected = false;
        }
        notifyStateToAll();
    }

    /**
     * notify all the listenner
     */
    private void notifyStateToAll() {
        for(NetworkStateReceiverListener listener : listeners)
            notifyState(listener);
    }

    /**
     * notify the state to ONE listenner
     * @param listener
     */
    private void notifyState(NetworkStateReceiverListener listener) {
        if(connected == null || listener == null)
            return;

        if(connected == true)
            listener.networkAvailable();
        else
            listener.networkUnavailable();
    }


    public void addListener(NetworkStateReceiverListener l) {
        listeners.add(l);
        notifyState(l);
    }


    public void removeListener(NetworkStateReceiverListener l) {
        listeners.remove(l);
    }


}