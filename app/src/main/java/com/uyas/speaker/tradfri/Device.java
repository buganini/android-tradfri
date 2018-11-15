package com.uyas.speaker.tradfri;

import android.util.Log;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.uyas.speaker.tradfri.Const.*;

public class Device {
    private final static String TAG = "Device";

    private Gateway mGateway;
    private String mPath;

    private String mName;
    private Boolean mState;
    private boolean mSupportLightControl = false;

    public Device(Gateway gateway, String path){
        mGateway = gateway;
        mPath = path;
    }

    private CoapClient mClient;
    private CoapClient getClient(){
        if(mClient==null){
            mClient = new CoapClient(String.format(Locale.ENGLISH, "coap://%s:%d%s", mGateway.getHost(), mGateway.getPort(), mPath));
            mClient.setEndpoint(mGateway.getDTLSEndpoint()).setTimeout(0).useCONs();
        }
        return mClient;
    }

    public String getName(){
        if(mName==null){
            return mPath;
        }
        return mName;
    }

    public String getPath(){
        return mPath;
    }

    public boolean supportLightControl(){
        return mSupportLightControl;
    }

    public void observe(){
        getClient().observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                Log.e(TAG, mPath+"="+response.getResponseText());
                try {
                    JSONObject data = new JSONObject(response.getResponseText());
                    mName = data.getString(ATTR_NAME);
                    if(data.has(ATTR_LIGHT_CONTROL)){
                        mSupportLightControl = true;
                        JSONArray lca = data.getJSONArray(ATTR_LIGHT_CONTROL);
                        JSONObject lc = lca.getJSONObject(0);
                        mState = lc.getInt(ATTR_DEVICE_STATE) != 0;
                    }
                    mGateway.deviceListUpdated();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    public Boolean getState(){
        return mState;
    }

    public void toggle(){
        if(mState == null){
            return;
        }
        JSONObject data = new JSONObject();
        JSONArray lca = new JSONArray();
        JSONObject lc = new JSONObject();
        try {
            if(mState){
                lc.put(ATTR_DEVICE_STATE, 0);
            }else{
                lc.put(ATTR_DEVICE_STATE, 1);
            }
            lca.put(lc);
            data.put(ATTR_LIGHT_CONTROL, lca);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        getClient().put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                Log.e(TAG, "Toggle: "+response.getResponseText());
            }

            @Override
            public void onError() {

            }
        }, data.toString(), MediaTypeRegistry.APPLICATION_JSON);
    }
}
