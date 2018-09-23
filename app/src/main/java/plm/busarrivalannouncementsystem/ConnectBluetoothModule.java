package plm.busarrivalannouncementsystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.github.silvestrpredko.dotprogressbar.DotProgressBarBuilder;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class ConnectBluetoothModule extends BaseActivity implements View.OnClickListener {

    private final String DEVICE_ADDRESS = "00:18:E4:34:E4:0F"; //MAC Address of Bluetooth Module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private String TAG = "ConnectBluetoothModule";
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private BluetoothAdapter mBluetoothAdapter;
    private String message;
    private DotProgressBar dotProgressBar;
    private EditText messageEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_bluetooth_module);
        displayDrawer();
        Log.d(TAG, "onCreate: true");
        dotProgressBar = findViewById(R.id.dot_progress_bar);
        messageEditText = findViewById(R.id.sendMessageEditText);
        findViewById(R.id.sendMessageButton).setOnClickListener(this);
//        bluetoothEnabled();
        if (bluetoothEnabled()) {
            Log.d(TAG, "bluetoothEnabled: true");

            if (bTconnect()) {
                Log.d(TAG, "bTconnect: true;");
                dotProgressBar.setVisibility(View.GONE);

            }
        }
    }

    public boolean bluetoothEnabled() {
        boolean found = false;
        Log.d(TAG, "bluetoothEnabled: starting");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(TAG, "bluetoothAdapter: null");
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
            found = false;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "bluetoothAdapter: disabled");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();

        if (bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Log.d(TAG, "bondedDevices: empty");
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
        }else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device = iterator;
                    Log.d(TAG, "finding device: true");
                    found = true;
                    break;
                }
                Log.d(TAG, "bluetoothDevices: "+iterator.getAddress());
            }
        }
        Log.d(TAG, "bluetoothEnabled: "+found);
        return found;
    }

    public boolean bTconnect() {
        boolean connected = true;

        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();
            Log.d(TAG, "createRfcommSocket: true");
            Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }

        if (connected) {
            try {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "bluetoothConnect:"+connected);
        return connected;

    }

    private void sendMessage(){

        try {
            message = messageEditText.getText().toString();
            Log.d(TAG, "message: connected");
            outputStream.write(message.getBytes()); //transmits the value of command to the bluetooth module
            Log.d(TAG, "sending message: "+message);
            Toast.makeText(getApplicationContext(),message.getBytes().toString(),Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        int i=view.getId();
        if (i==R.id.sendMessageButton){
            sendMessage();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
        super.onBackPressed();
    }
}
