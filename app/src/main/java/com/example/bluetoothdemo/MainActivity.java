package com.example.bluetoothdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String Tag = "Main Activity";

    BluetoothAdapter bluetoothAdapter;
    Button b1;
    static ImageView iv;
    static TextView tv;
    ConnectivityManager connectivityManager;

    BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,bluetoothAdapter.ERROR);

                switch (state)
                {
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(getApplicationContext(),"Blutooth Enabled",Toast.LENGTH_SHORT).show();
                        Log.d(Tag,"State ON");
                        break;

                    case BluetoothAdapter.STATE_OFF:
                         Log.d(Tag,"State OFF");
                         break;

                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(Tag,"State Turning ON");
                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(Tag,"State Turning OFF");
                        break;
                }
            }
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED))
            {
                final  int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,bluetoothAdapter.ERROR);

                switch (state)
                {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Toast.makeText(getApplicationContext(),"Device Discovery Enabled",Toast.LENGTH_SHORT).show();
                        break;

                    //There are four more states
                }
            }
//            if(action.equals(BluetoothDevice.ACTION_FOUND))
//            {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                Toast.makeText(getApplicationContext(),"Vamsi says : "+device.getName(),Toast.LENGTH_SHORT).show();
//            }
            }
    };

    BroadcastReceiver receiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Toast.makeText(MainActivity.this,"Vamsi says:"+deviceName,Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        },3);

        b1 = findViewById(R.id.onoff);
        iv = findViewById(R.id.imv);
        tv = findViewById(R.id.tv);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        connectivityManager =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDisableBT();
            }
        });

    }

    private void enableDisableBT() {
        if(!bluetoothAdapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);

            IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiver1,intentFilter);

            b1.setText("OFF");
        }
        if(bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.disable();

            IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiver1,intentFilter);

            Toast.makeText(getApplicationContext(),"Bluetooth Disabled",Toast.LENGTH_SHORT).show();

            b1.setText("ON");
        }

    }

    public void makeDiscoverable(View view) {
        Toast.makeText(getApplicationContext(),"Making device discoverable for 300 secs",Toast.LENGTH_LONG).show();

        Intent discoverableintent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableintent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        startActivity(discoverableintent);

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(broadcastReceiver1,intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver1);

        bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(receiver2);
    }


    public void pairedDevices(View view) {
        if(bluetoothAdapter != null)
        {
            String str = "Paired Devices are:";
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0)
            {
                for(BluetoothDevice device : pairedDevices)
                {
                    str = str + device.getName() + "\n";
                }
                Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
            }

        }
    }


    public void discoverDevices(View view) {

        if(bluetoothAdapter != null)
        {
            if(bluetoothAdapter.isDiscovering())
            {
                bluetoothAdapter.cancelDiscovery();
            }

            bluetoothAdapter.startDiscovery();

//            checkPermissions();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver2,filter);
        }
    }

    public void NetworkStatus(View view) {

        String imagepath = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/63/The_noted_Telugu_film_actor_Shri_Pavan_Kalyan_addressing_at_the_closing_ceremony_and_award_function_of_the_18th_International_Children%E2%80%99s_Film_Festival_India%2C_in_Hyderabad_on_November_20%2C_2013_%28cropped%29.jpg/220px-thumbnail.jpg";
        String textpath = "https://www.dropbox.com/s/m83h320c153o0iw/myfile.txt?dl=1";

        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if(info != null && info.isConnected())
        {
            if(info.getType() == ConnectivityManager.TYPE_WIFI)
            {
                Toast.makeText(this,"WI FI", Toast.LENGTH_SHORT).show();
                new MyImageTask().execute(imagepath);
                new MyTextTask().execute(textpath);
            }
            if(info.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                Toast.makeText(this,"Mobile", Toast.LENGTH_SHORT).show();
            }
        }

    }
}

class MyImageTask extends AsyncTask<String,Void, Bitmap>
{

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(strings[0]);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setRequestMethod("GET");


            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            Log.d(".imageTask","Connection Started");

            int code = httpURLConnection.getResponseCode();

            if(code == HttpURLConnection.HTTP_OK)
            {
                Log.d(".ImgTask","Image Downloading");
                InputStream inputStream =httpURLConnection.getInputStream();
                if(inputStream != null)
                {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap!=null)
        {
            Log.d(".imv","Setting the Bitmap");
            MainActivity.iv.setImageBitmap(bitmap);
            MainActivity.iv.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }
}

class MyTextTask extends AsyncTask<String, Void,String> {
    @Override
    protected String doInBackground(String... strings) {
        return downLoadText(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("TAG", s);
        MainActivity.tv.setText(s);
    }

    String downLoadText(String path) {
        String text = null;
        try {
            URL url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setReadTimeout(3000);
            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            int code = httpURLConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                if (inputStream != null) {
                    BufferedReader br =  new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    text = sb.toString();
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;

    }
}