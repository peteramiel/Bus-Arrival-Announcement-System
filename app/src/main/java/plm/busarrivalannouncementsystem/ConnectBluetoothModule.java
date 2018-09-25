package plm.busarrivalannouncementsystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
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
import java.io.InputStream;
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
    private InputStream inputStream;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    private BluetoothAdapter mBluetoothAdapter;
    private String message;
    private DotProgressBar dotProgressBar;
    private EditText messageEditText;
    private TextView receivedMessageTextView;
    Thread workerThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_bluetooth_module);
        displayDrawer();
        Log.d(TAG, "onCreate: true");
        dotProgressBar = findViewById(R.id.dot_progress_bar);
        messageEditText = findViewById(R.id.sendMessageEditText);
        receivedMessageTextView= findViewById(R.id.receivedMessageTextView);
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
                inputStream = socket.getInputStream(); //gets the input stream of the socket
                Log.d(TAG, "socket: get input output stream");
                beginListenForData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "bluetoothConnect:"+connected);
        return connected;

    }
    void beginListenForData()
    {
        Log.d(TAG, "beginListenForData: start");
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                Log.d(TAG, "beginListenForData: start thread");
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        Log.d(TAG, "beginListenForData: isn't interrupted");
                        int bytesAvailable = inputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            Log.d(TAG, "beginListenForData: readData");
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            receivedMessageTextView.setText(data);
                                            Log.d(TAG, "beginListenForData: set Text");
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
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
