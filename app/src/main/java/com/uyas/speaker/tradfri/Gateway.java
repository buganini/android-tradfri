package com.uyas.speaker.tradfri;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.LinkedList;
import java.util.List;

import static com.uyas.speaker.tradfri.Const.*;

public class Gateway {
    public interface Callback {
        void onDevicesListUpdated();
    }

    private final static String TAG = "Gateway";

    private static final String TRUST_STORE_PASSWORD = "rootPass";
    private static final String KEY_STORE_PASSWORD = "endPass";
    private static final String KEY_STORE_LOCATION = "certs/keyStore.p12";
    private static final String TRUST_STORE_LOCATION = "certs/trustStore.p12";

    private static final String KEY_USER = "dtls_user";
    private static final String KEY_PSK = "dtls_psk";

    String mUser;
    Context mContext;
    String mName;
    String mHost;
    int mPort;
    String mCode;
    String mPSK;
    Callback mCallback;

    private final List<Device> mDevices = new LinkedList<>();

    public Gateway(Context context, String name, String host, int port, Callback callback){
        mContext = context;
        mName = name;
        mHost = host;
        mPort = port;
        mCallback = callback;

        String code = PreferenceManager.getDefaultSharedPreferences(context).getString(getKey(), null);
        setCode(code);
    }

    public String getKey(){
        return "gw/"+mName;
    }

    public void setCode(String code){
        mCode = code;

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putString(getKey(), mCode);
        editor.apply();

        getPSK();
    }

    public boolean hasCode(){
        return mCode != null;
    }

    public String getName(){
        return mName;
    }

    public String getHost(){
        return mHost;
    }

    public int getPort(){
        return mPort;
    }

    CoapEndpoint mDTLSEndpoint;
    CoapEndpoint getDTLSEndpoint(){
        if(mDTLSEndpoint == null){
            mDTLSEndpoint = getDTLSEndpoint(mUser, mPSK);
        }
        return mDTLSEndpoint;
    }

    private CoapEndpoint getDTLSEndpoint(String user, String key){
        DTLSConnector dtlsConnector = null;

        InputStream inTrust = null;
        InputStream in = null;
        try {
            // load key store
            KeyStore keyStore = KeyStore.getInstance("pkcs12");
            in = mContext.getAssets().open(KEY_STORE_LOCATION);
            keyStore.load(in, KEY_STORE_PASSWORD.toCharArray());
            in.close();

            // load trust store
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            inTrust = mContext.getAssets().open(TRUST_STORE_LOCATION);
            trustStore.load(inTrust, TRUST_STORE_PASSWORD.toCharArray());

            // You can load multiple certificates if needed
            Certificate[] trustedCertificates = new Certificate[1];
            trustedCertificates[0] = trustStore.getCertificate("root");

            DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(new InetSocketAddress(0));
            builder.setPskStore(new StaticPskStore(user, key.getBytes()));
            builder.setIdentity((PrivateKey)keyStore.getKey("client", KEY_STORE_PASSWORD.toCharArray()),
                    keyStore.getCertificateChain("client"), true);
            builder.setTrustStore(trustedCertificates);
            dtlsConnector = new DTLSConnector(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inTrust != null) {
                    inTrust.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new CoapEndpoint(dtlsConnector, NetworkConfig.getStandard());
    }

    private void getPSK(){
        if(mCode == null){
            return;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        mUser = sp.getString(KEY_USER, null);
        mPSK = sp.getString(KEY_PSK, null);
        if(mUser != null && mPSK != null){
            Log.e(TAG, "user="+mUser);
            Log.e(TAG, "psk="+mPSK);
            getEndpoints();
            return;
        }
        try {
            mUser = String.valueOf(System.currentTimeMillis());

            CoapClient client = new CoapClient(String.format("coap://%s:%d/%s/%s", mHost, mPort, ROOT_GATEWAY, ATTR_AUTH));
            client.setEndpoint(getDTLSEndpoint("Client_identity", mCode)).setTimeout(0).useCONs();
            JSONObject authData = new JSONObject();
            authData.put(ATTR_IDENTITY, mUser);
            Log.e(TAG, "data="+authData.toString());
            client.post(new CoapHandler(){

                @Override
                public void onLoad(CoapResponse response) {
                    try {
                        Log.e(TAG, response.getResponseText());
                        JSONObject data = new JSONObject(response.getResponseText());
                        mPSK = data.getString(ATTR_PSK);
                        Log.e(TAG, "user="+mUser);
                        Log.e(TAG, "psk="+mPSK);

                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
                        editor.putString(KEY_USER, mUser);
                        editor.putString(KEY_PSK, mPSK);
                        editor.apply();

                        getEndpoints();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError() {

                }
            }, authData.toString(), MediaTypeRegistry.APPLICATION_JSON);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getEndpoints(){
        CoapClient client = new CoapClient(String.format("coap://%s:%d/.well-known/core", mHost, mPort));
        client.setEndpoint(getDTLSEndpoint()).setTimeout(0).useCONs();
        client.get(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                Log.e(TAG, "endpoints res="+response.getResponseText());
                String devicePrefix = String.format("/%s/", ROOT_DEVICES);
                for(String entry : response.getResponseText().split(",")){
                    String s = entry.split(";")[0];
                    String path = s.substring(2, s.length()-1);
                    Log.e(TAG, "s="+s+" path="+path);
                    if(path.startsWith(devicePrefix)){
                        Device device = new Device(Gateway.this, path);
                        device.observe();
                        mDevices.add(device);
                    }
                }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onDevicesListUpdated();
                    }
                });
            }

            @Override
            public void onError() {

            }
        });
    }

    public List<Device> getDevices(){
        return mDevices;
    }

    void deviceListUpdated(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onDevicesListUpdated();
            }
        });
    }
}
