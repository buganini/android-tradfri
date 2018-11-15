package com.uyas.speaker.tradfri;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class Tradfri {
    private final static String TAG = "Tradfri";

    public interface Listener {
        void onRefresh();
    }

    private final static String SERVICE_TYPE = "_coap._udp.";

    Context mContext;
    NsdManager mNsdManager;
    Listener mListener;
    Handler mHandler = new Handler(Looper.getMainLooper());
    final List<Gateway> mGateways = new LinkedList<>();
    SharedPreferences mSharedPreferences;

    public Tradfri(Context context, Listener listener){
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        mListener = listener;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void discovery(){
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            }
        }, 5000);
    }

    NsdManager.DiscoveryListener mDiscoveryListener = new NsdManager.DiscoveryListener() {

        // Called as soon as service discovery begins.
        @Override
        public void onDiscoveryStarted(String regType) {
            Log.d(TAG, "Service discovery started");
        }

        @Override
        public void onServiceFound(NsdServiceInfo service) {
            // A service was found! Do something with it.
            Log.d(TAG, "Service discovery success" + service);
            if (service.getServiceType().equals(SERVICE_TYPE)) {
                mNsdManager.resolveService(service, mResolveListener);
                Log.d(TAG, "Service Name: " + service.getServiceName());
            }
        }

        @Override
        public void onServiceLost(NsdServiceInfo service) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            Log.e(TAG, "service lost: " + service);
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.i(TAG, "Discovery stopped: " + serviceType);
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            mNsdManager.stopServiceDiscovery(this);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            mNsdManager.stopServiceDiscovery(this);
        }
    };

    NsdManager.ResolveListener mResolveListener = new NsdManager.ResolveListener() {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.e(TAG, "Resolve failed" + errorCode);
        }
        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.e(TAG, "Resolve Succeeded. " + serviceInfo);
            String name = serviceInfo.getServiceName();
            String host = serviceInfo.getHost().getHostAddress();
            int port = serviceInfo.getPort();
            Gateway gateway = new Gateway(mContext, name, host, port, new Gateway.Callback() {
                @Override
                public void onDevicesListUpdated() {
                    mListener.onRefresh();
                }
            });
            if(!mGateways.contains(gateway)){
                gateway.init();
                mGateways.add(gateway);
            }
        }
    };

    public Device getDeviceByName(String name){
        for(Gateway gw : mGateways){
            for(Device d: gw.getDevices()){
                if(d.getName().equals(name)){
                    return d;
                }
            }
        }
        return null;
    }


    public List<Device> getDevices(){
        List<Device> devices = new LinkedList<>();
        for(Gateway gw : mGateways){
            devices.addAll(gw.getDevices());
        }
        return devices;
    }

    public List<Gateway> getUnboundGateways(){
        List<Gateway> gateways = new LinkedList<>();
        for(Gateway gw : mGateways){
            if(!gw.isReady()){
                gateways.add(gw);
            }
        }
        return gateways;
    }
}
