package com.uyas.speaker.tradfri.sample;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.uyas.speaker.tradfri.Gateway;
import com.uyas.speaker.tradfri.Tradfri;

import java.util.List;

public class SetupGateway {
    private Activity mActivity;

    public SetupGateway(Activity activity, Tradfri tradfri){
        mActivity = activity;
        final List<Gateway> gateways = tradfri.getUnboundGateways();
        String names[] = new String[gateways.size()];
        for (int i = 0; i < gateways.size(); i++) {
            names[i] = gateways.get(i).getName();
        }

        new AlertDialog.Builder(activity)
                .setItems(names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        promptCode(gateways.get(which));
                    }
                }).show();
    }

    private void promptCode(final Gateway gateway){
        final EditText input = new EditText(mActivity);
        new AlertDialog.Builder(mActivity)
                .setTitle("Enter code for "+gateway.getName())
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String code = input.getText().toString();
                        gateway.setCode(code);
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

}
