package com.uyas.speaker.tradfri.sample;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.uyas.speaker.tradfri.Device;
import com.uyas.speaker.tradfri.Gateway;
import com.uyas.speaker.tradfri.Tradfri;

import java.util.List;

public class SetupDevice {
    private Activity mActivity;

    public SetupDevice(Activity activity, final Device device){
        mActivity = activity;
        final EditText input = new EditText(mActivity);
        input.setText(device.getName());
        new AlertDialog.Builder(mActivity)
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String name = input.getText().toString();
                        device.setName(name);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

}
