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

    // https://github.com/ggravlingen/pytradfri/issues/132

    public final static int MIN_BRIGHTNESS = 1;
    public final static int MAX_BRIGHTNESS = 254;

    public final static int MIN_SPECTRUM = 250;
    public final static int MAX_SPECTRUM = 454;

    public final static int MIN_HUE = 0;
    public final static int MAX_HUE = 65535;

    public final static int MIN_SATURATION = 0;
    public final static int MAX_SATURATION = 65279;

    private Gateway mGateway;
    private String mPath;

    private String mName;
    private String mProductName;

    private boolean mReady = false;
    private boolean mSupportLightControl = false;
    private boolean mSupportSpectrum = false;
    private boolean mSupportColor = false;

    private Boolean mState;
    private int mBrightness;
    private int mSpectrum;
    private int mHue;
    private int mSaturation;

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

    public boolean supportSpectrum(){
        return mSupportSpectrum;
    }

    public boolean supportColor(){
        return mSupportColor;
    }

    public void observe(){
        getClient().observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                Log.e(TAG, mPath+"="+response.getResponseText());
                try {
                    JSONObject data = new JSONObject(response.getResponseText());

                    JSONObject device_info = data.getJSONObject(ATTR_DEVICE_INFO);
                    mProductName = device_info.getString(ATTR_DEVICE_PRODUCT);

                    mName = data.getString(ATTR_NAME);
                    if(data.has(ATTR_LIGHT_CONTROL)){
                        mSupportLightControl = true;
                        JSONArray lca = data.getJSONArray(ATTR_LIGHT_CONTROL);
                        JSONObject lc = lca.getJSONObject(0);
                        mState = lc.getInt(ATTR_DEVICE_STATE) != 0;
                        mBrightness = lc.getInt(ATTR_LIGHT_DIMMER);
                        if(lc.has(ATTR_LIGHT_MIREDS)){
                            mSupportSpectrum = true;
                            mSpectrum = lc.getInt(ATTR_LIGHT_MIREDS);
                        }
                        if(lc.has(ATTR_LIGHT_COLOR_HUE) && lc.has(ATTR_LIGHT_COLOR_SATURATION)){
                            mSupportColor = true;
                            mHue = lc.getInt(ATTR_LIGHT_COLOR_HUE);
                            mSaturation = lc.getInt(ATTR_LIGHT_COLOR_SATURATION);
                        }
                    }
                    mReady = true;
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

    public boolean isReady(){
        return mReady;
    }

    public Boolean getState(){
        return mState;
    }

    public int getBrightness(){
        return mBrightness;
    }

    public void setBrightness(int brightness){
        if(!supportLightControl()){
            return;
        }
        if(mState == null){
            return;
        }
        brightness = Math.min(brightness, MAX_BRIGHTNESS);
        brightness = Math.max(brightness, MIN_BRIGHTNESS);
        JSONObject data = new JSONObject();
        JSONArray lca = new JSONArray();
        JSONObject lc = new JSONObject();
        try {
            lc.put(ATTR_LIGHT_DIMMER, brightness);
            lca.put(lc);
            data.put(ATTR_LIGHT_CONTROL, lca);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        getClient().put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                Log.e(TAG, "SetBrightness: "+response.getResponseText());
            }

            @Override
            public void onError() {

            }
        }, data.toString(), MediaTypeRegistry.APPLICATION_JSON);
    }

    public int getSpectrum(){
        return mSpectrum;
    }

    public void setSpectrum(int spectrum){
        if(!supportSpectrum()){
            return;
        }
        if(mState == null){
            return;
        }
        spectrum = Math.min(spectrum, MAX_SPECTRUM);
        spectrum = Math.max(spectrum, MIN_SPECTRUM);
        JSONObject data = new JSONObject();
        JSONArray lca = new JSONArray();
        JSONObject lc = new JSONObject();
        try {
            lc.put(ATTR_LIGHT_MIREDS, spectrum);
            lca.put(lc);
            data.put(ATTR_LIGHT_CONTROL, lca);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        getClient().put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                Log.e(TAG, "setSpectrum: "+response.getResponseText());
            }

            @Override
            public void onError() {

            }
        }, data.toString(), MediaTypeRegistry.APPLICATION_JSON);
    }

    public int getHue(){
        return mHue;
    }

    public void setHue(int hue){
        if(!supportColor()){
            return;
        }
        if(mState == null){
            return;
        }
        hue = Math.min(hue, MAX_HUE);
        hue = Math.max(hue, MIN_HUE);
        JSONObject data = new JSONObject();
        JSONArray lca = new JSONArray();
        JSONObject lc = new JSONObject();
        try {
            lc.put(ATTR_LIGHT_COLOR_HUE, hue);
            lc.put(ATTR_LIGHT_COLOR_SATURATION, mSaturation);
            lca.put(lc);
            data.put(ATTR_LIGHT_CONTROL, lca);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        getClient().put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                Log.e(TAG, "setHue: "+response.getResponseText());
            }

            @Override
            public void onError() {

            }
        }, data.toString(), MediaTypeRegistry.APPLICATION_JSON);
    }

    public int getSaturation(){
        return mSaturation;
    }

    public void setSaturation(int saturation){
        if(!supportColor()){
            return;
        }
        if(mState == null){
            return;
        }
        saturation = Math.min(saturation, MAX_SATURATION);
        saturation = Math.max(saturation, MIN_SATURATION);
        JSONObject data = new JSONObject();
        JSONArray lca = new JSONArray();
        JSONObject lc = new JSONObject();
        try {
            lc.put(ATTR_LIGHT_COLOR_HUE, mHue);
            lc.put(ATTR_LIGHT_COLOR_SATURATION, saturation);
            lca.put(lc);
            data.put(ATTR_LIGHT_CONTROL, lca);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        getClient().put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                Log.e(TAG, "setSaturation: "+response.getResponseText());
            }

            @Override
            public void onError() {

            }
        }, data.toString(), MediaTypeRegistry.APPLICATION_JSON);
    }

    public void setName(String name){
        if(name==null || name.isEmpty()){
            name = mProductName;
        }
        JSONObject data = new JSONObject();
        try {
            data.put(ATTR_NAME, name);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        getClient().put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                Log.e(TAG, "SetName: "+response.getResponseText());
            }

            @Override
            public void onError() {

            }
        }, data.toString(), MediaTypeRegistry.APPLICATION_JSON);
    }

    public void toggle(){
        if(mState == null){
            return;
        }
        if(mState) {
            turnOff();
        } else {
            turnOn();
        }
    }

    public void turnOn(){
        if(!supportLightControl()){
            return;
        }
        if(mState == null){
            return;
        }
        JSONObject data = new JSONObject();
        JSONArray lca = new JSONArray();
        JSONObject lc = new JSONObject();
        try {
            lc.put(ATTR_DEVICE_STATE, 1);
            lca.put(lc);
            data.put(ATTR_LIGHT_CONTROL, lca);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        getClient().put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                Log.e(TAG, "TurnOn: "+response.getResponseText());
            }

            @Override
            public void onError() {

            }
        }, data.toString(), MediaTypeRegistry.APPLICATION_JSON);
    }

    public void turnOff(){
        if(!supportLightControl()){
            return;
        }
        if(mState == null){
            return;
        }
        JSONObject data = new JSONObject();
        JSONArray lca = new JSONArray();
        JSONObject lc = new JSONObject();
        try {
            lc.put(ATTR_DEVICE_STATE, 0);
            lca.put(lc);
            data.put(ATTR_LIGHT_CONTROL, lca);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        getClient().put(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse response) {
                Log.e(TAG, "TurnOff: "+response.getResponseText());
            }

            @Override
            public void onError() {

            }
        }, data.toString(), MediaTypeRegistry.APPLICATION_JSON);
    }
}
