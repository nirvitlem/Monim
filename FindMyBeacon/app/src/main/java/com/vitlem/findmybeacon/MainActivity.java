package com.vitlem.findmybeacon;

import android.Manifest;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.UUID;

import static android.support.v4.app.NotificationCompat.DEFAULT_SOUND;
import static android.support.v4.app.NotificationCompat.DEFAULT_VIBRATE;

public class MainActivity extends AppCompatActivity {

    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanFilter mScanFilter;
    private ScanSettings mScanSettings;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_FINE_LOCATION = 0;
    private static ListView lv;
    private static Hashtable<String,String> hashReg  = new Hashtable<String,String>();
    public final static String EXTRA_MESSAGE = "Udid";
    static Context context;
    DateFormat df = DateFormat.getDateTimeInstance (DateFormat.SHORT, DateFormat.SHORT, new Locale("en", "EN"));
    NotificationCompat.Builder builder;
    NotificationManager mNotificationManager ;
    private int index=0;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    //private static final int REQUEST_COARSE_LOCATION=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        ReadRegList();
        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
        loadPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_FINE_LOCATION);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final Button Scanbutton = (Button) findViewById(R.id.ScanB);
        Scanbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ScanNow();
            }
        });

        final Button Findbutton = (Button) findViewById(R.id.FindS);
        Findbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FindNow();
            }
        });

        final Button Stopbutton = (Button) findViewById(R.id.ScanS);
        Stopbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBluetoothLeScanner.stopScan(mScanCallback);
                mBluetoothLeScanner.stopScan(mScanCallbackWithFind);
            }
        });

        final Button ManageButton = (Button) findViewById(R.id.ManageS);
        ManageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MangeBeacon.class);
                startActivity(i);
            }
        });

        lv = (ListView) findViewById(R.id.ListR);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) lv.getItemAtPosition(position);

                //try {
                //hashReg.put(itemValue.split("_")[1].toString(), itemValue.split("_")[2].toString() );
                Log.d("hashReg.put", itemValue.split("_")[1].toString());
                sendMessage(view, itemValue.split("_")[1].toString() + "_" +itemValue.split("_")[2].toString(),AddBeaconInfo.class);
                // }catch (Exception e)
                // {
                //   Toast.makeText(getApplicationContext(),
                //            e.getMessage(), Toast.LENGTH_LONG)
                //             .show();
                //  }
                // Show Alert
                //Toast.makeText(getApplicationContext(),
                //        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                //        .show();

            }

        });
    }

    public static UUID asUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    private void setScanFilter(String stringUdid) {
        ScanFilter.Builder mBuilder = new ScanFilter.Builder();
        // Empty data
        byte[] manData = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
// Data Mask
        byte[] mask = new byte[]{0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0};
// Copy UUID into data array and remove all "-"
        System.arraycopy(hexStringToByteArray(stringUdid.replace("-", "")), 0, manData, 2, 16);
// Add data array to filters
        mBuilder.setManufacturerData(76, manData, mask).build();
        mScanFilter = mBuilder.build();
    }

    private void setScanSettings() {
        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
       // mBuilder.setReportDelay(200);
       // mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        mScanSettings = mBuilder.build();
    }

    protected ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord mScanRecord = result.getScanRecord();
            try {

               listItems.add(0, mScanRecord.getDeviceName() + "_" + BeaconInfo(mScanRecord.getBytes()) + "_"
                        + getDistance(calculateDistance(mScanRecord.getTxPowerLevel(), result.getRssi()),mScanRecord.getDeviceName()) + " " +df.format(new Date()));

                adapter.addAll(listItems);
                adapter.notifyDataSetChanged();
                // adapter.add(mScanRecord.getDeviceName() + "_"  + BeaconInfo(mScanRecord.getBytes())+ "_"
                //         + getDistance(calculateDistance(mScanRecord.getTxPowerLevel(),result.getRssi())));


                Log.d("mScanCallback", mScanRecord.getDeviceName());
                byte[] manufacturerData = mScanRecord.getManufacturerSpecificData(224);
                Log.d("mScanCallback", String.valueOf(result.getRssi()));
                int mRssi = result.getRssi();
            } catch (Exception e) {
                Log.d("mScanCallback", e.getMessage());
            }

        }

    };

    protected ScanCallback mScanCallbackWithFind = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord mScanRecord = result.getScanRecord();
            try {
                int i = Integer.valueOf(hashReg.get(BeaconInfo(mScanRecord.getBytes()).toString()).split(";")[1]);
                String t = (hashReg.get(BeaconInfo(mScanRecord.getBytes()).toString())).split(";")[0] + " " + df.format(new Date()) + " "
                        + getDistance(calculateDistance(mScanRecord.getTxPowerLevel(), result.getRssi()),
                        (hashReg.get(BeaconInfo(mScanRecord.getBytes()).toString())).split(";")[0]);

                if (t != null) {
                   // listItems.remove(i);
                   /* if (listItems.size()<=i)
                    {*/
                        Log.d("mScanCallbackWithFind", "add item " + i + " " + t);
                        listItems.add(0, t);
                   /* }else {
                        //listItems.add(i, t);
                        Log.d("mScanCallbackWithFind", "replace item " + i + " " + t);
                        listItems.set(i, t);
                    }*/
                    //listItems.add( t);
                    //adapter.add(t);
                } else {
                  /*  listItems.add(0, mScanRecord.getDeviceName() + "_" + BeaconInfo(mScanRecord.getBytes()) + "_"
                            + getDistance(calculateDistance(mScanRecord.getTxPowerLevel(), result.getRssi())) + " " + df.format(new Date()));*/
                    //adapter.add(mScanRecord.getDeviceName() + "_" + BeaconInfo(mScanRecord.getBytes()) + "_"
                    //   + getDistance(calculateDistance(mScanRecord.getTxPowerLevel(), result.getRssi())));
                }
                adapter.addAll(listItems);
                adapter.notifyDataSetChanged();
                Log.d("mScanCallback", mScanRecord.getDeviceName());
                byte[] manufacturerData = mScanRecord.getManufacturerSpecificData(224);
                Log.d("mScanCallback", String.valueOf(result.getRssi()));
                int mRssi = result.getRssi();
            }catch (Exception e)
            {
                Log.d("mScanCallbackWithFind", "Error " + e.getMessage());
            }
        }
    };



    public double calculateDistance(int txPower, double rssi) {
       /* if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;


       }*/
        return (Math.pow(10d,((double)txPower-rssi)/(10*2)))/1000;
    }

    private String getDistance(double accuracy,String s) {
        try {
            if (accuracy == -1.0) {
                return "Unknown " + accuracy;
            } else if (accuracy < 1) {

                return "Immediate";
            } else if (accuracy < 3) {

                return "Near " + accuracy;
            } else {
                popup(s + accuracy);
                return "Far " + accuracy;
            }
        } catch (Exception e) {
            Log.d("getDistance", e.getMessage());
        }
        return String.valueOf(accuracy);
    }

    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        }
    }

    private void popup(String s)
    {
        builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(s)
                .setSmallIcon(R.drawable.ic_action_name)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        mNotificationManager.notify(index++, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // granted
                } else {
                    // no granted
                }
                return;
            }

        }

    }

    private void ScanNow() {
        listItems.clear();
        adapter.notifyDataSetChanged();
        Log.d("ScanB", "ScanNow");
        //loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
       // loadPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_FINE_LOCATION);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeScanner.startScan(mScanCallback);
        //mBluetoothLeScanner.startScan(Arrays.asList(mScanFilter), mScanSettings, mScanCallback);
    }

    private void FindNow() {
        listItems.clear();
        adapter.notifyDataSetChanged();
        Log.d("FindB", "FindNow");
        ReadRegList();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        for (Enumeration<String> s = hashReg.keys()
             ; s.hasMoreElements();
             setScanFilter(s.nextElement().toString().split("_")[0].toString()));
        setScanSettings();

        //mBluetoothLeScanner.startScan(mScanCallback);
        mBluetoothLeScanner.startScan(Arrays.asList(mScanFilter), mScanSettings, mScanCallbackWithFind);
    }

    static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private String BeaconInfo(final byte[] scanRecord) {

        int startByte = 2;

        boolean patternFound = false;
        while (startByte <= 5)

        {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound)

        {
            //Convert to hex String
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);

            //Here is your UUID
            String uuid = hexString.substring(0, 8) + "-" +
                    hexString.substring(8, 12) + "-" +
                    hexString.substring(12, 16) + "-" +
                    hexString.substring(16, 20) + "-" +
                    hexString.substring(20, 32);

           // String uuid = hexString.substring(0, 32);

            //Here is your Major value
            int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

            //Here is your Minor value
            int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);
            return uuid +"_" + major  + minor;
        }
        return "N/A";
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public void sendMessage(View view,String message,Class c) {
        Intent intent = new Intent(this, c);
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private void ReadRegList()
    {
        hashReg = SaveReadFromLocal.LoadAll(context);
    }
}
