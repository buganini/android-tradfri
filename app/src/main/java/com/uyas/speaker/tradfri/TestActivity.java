package com.uyas.speaker.tradfri;

import android.net.nsd.NsdServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class TestActivity extends AppCompatActivity {
    private final static String TAG = "TestActivity";

    private final static String GATEWAY_CODE = "rld8JzckBgU530KB";

    private TextView hostinfo;
    private ListView devices;
    private List<Device> mDevices;

    private Discovery mDiscovery;
    private Gateway mGateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        hostinfo = findViewById(R.id.hostinfo);
        devices = findViewById(R.id.devices);
        devices.setAdapter(mAdapter);

        mDiscovery = new Discovery(this, new Discovery.Listener() {
            @Override
            public void onFound(NsdServiceInfo serviceInfo) {
                String host = serviceInfo.getHost().getHostAddress();
                int port = serviceInfo.getPort();
                hostinfo.setText(host+":"+port);
                mGateway = new Gateway(TestActivity.this, host, port, GATEWAY_CODE, new Gateway.Callback() {
                    @Override
                    public void onDevicesListUpdated() {
                        mDevices = mGateway.getDevices();
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDiscovery.start();
    }

    BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if(mDevices == null){
                return 0;
            }
            return mDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        class ViewHolder {
            TextView path;
            Button toggle;
            Device device;

            ViewHolder(View view){
                path = view.findViewById(R.id.path);
                toggle = view.findViewById(R.id.toggle);
                toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        device.toggle();
                    }
                });
            }

            void bind(Device d){
                device = d;
                path.setText(d.getName());
                if(d.supportLightControl()){
                    toggle.setText(d.getState() ? "Off" : "On");
                }
                toggle.setVisibility(d.supportLightControl() ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder vh;
            if(view == null){
                LayoutInflater layoutInflater = LayoutInflater.from(TestActivity.this);
                view = layoutInflater.inflate(R.layout.item_device, viewGroup, false);
                vh = new ViewHolder(view);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }
            Device device = (Device) getItem(i);
            vh.bind(device);
            return view;
        }
    };
}
