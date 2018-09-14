package plm.busarrivalannouncementsystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private BluetoothAdapter mBluetoothAdapter;
    private String message;
    private DotProgressBar dotProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_bluetooth_module);
        displayDrawer();
        dotProgressBar = findViewById(R.id.dot_progress_bar);

        if (bluetoothEnabled()) {
            bTconnect();
            if(bTconnect()){
                dotProgressBar.setVisibility(View.INVISIBLE);
                try
                {
                    message = "Connected";
                    outputStream.write(message.getBytes()); //transmits the value of command to the bluetooth module
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean bluetoothEnabled() {
        boolean found = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
            found = false;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }

        }
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();

        if(bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean bTconnect()
    {
        boolean connected = true;

        try
        {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();

            Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            connected = false;
        }

        if(connected)
        {
            try
            {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        return connected;
    }


    @Override
    public void onClick(View view) {

    }
    @Override
    protected void onStart()
    {
        super.onStart();
    }
}
