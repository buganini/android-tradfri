package com.uyas.speaker.tradfri.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.uyas.speaker.tradfri.Device;
import com.uyas.speaker.tradfri.R;
import com.uyas.speaker.tradfri.Tradfri;

import java.util.List;

public class TestActivity extends AppCompatActivity {
    private final static String TAG = "TestActivity";

    private ListView devices;
    private List<Device> mDevices;

    private Tradfri mTradfri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SetupGateway(TestActivity.this, mTradfri);
            }
        });

        devices = findViewById(R.id.devices);
        devices.setAdapter(mAdapter);
        devices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new SetupDevice(TestActivity.this, mDevices.get(i));
                return true;
            }
        });

        mTradfri = new Tradfri(getApplicationContext(), new Tradfri.Listener() {
            @Override
            public void onRefresh() {
                mDevices = mTradfri.getDevices();
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTradfri.discovery();
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
