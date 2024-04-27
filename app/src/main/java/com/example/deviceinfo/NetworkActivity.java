package com.example.deviceinfo;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * @author Antonio Pietroluongo
 */
public class NetworkActivity extends AppCompatActivity {
    private CustomAdapter customAdapter;
    private RecyclerView recyclerView;
    private TextView textView;
    private static final int[] channels = {-1, 2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447, 2452, 2457, 2462, 2467, 2472, 2484};
    private String unavailable;
    private Timer timer;
    private TimerTask timerTask;

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        unavailable = getString(R.string.unavailable);
        RelativeLayout relativeLayout = findViewById(R.id.relativelayout_public_ip);
        textView = findViewById(R.id.textview_public_ip_2);
        relativeLayout.setOnLongClickListener(view -> {
            ClipboardManager clipboardManager = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(ClipData.newPlainText("Public IP", textView.getText()));
            Toast.makeText(view.getContext(), R.string.copied, Toast.LENGTH_LONG).show();
            return true;
        });
        recyclerView = findViewById(R.id.recyclerview_network);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        customAdapter = new CustomAdapter(getData());
        recyclerView.setAdapter(customAdapter);
        reqPermission(this);
    }

    private void reqPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerTask.cancel();
        timer.cancel();
        timer.purge();
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (isWifiEnabled()) {
                        if (recyclerView.getVisibility() != View.VISIBLE) {
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        customAdapter.setData(getData());
                        customAdapter.notifyDataSetChanged();
                    } else {
                        if (recyclerView.getVisibility() != View.GONE) {
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 1, 2000);
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            String publicIP;
            try {
                publicIP = fetchIP();
            } catch (Exception e) {
                publicIP = unavailable;
            }
            String s = publicIP;
            handler.post(() -> textView.setText(s));
        });
    }

    private boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    private List<Item> getData() {
        String ssid = null;
        String bssid = null;
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ssid = wifiInfo.getSSID().replace("\"", "");
            bssid = wifiInfo.getBSSID();
        }
        int frequency = wifiInfo.getFrequency();
        int channel = Arrays.binarySearch(channels, frequency);
        int linkSpeed = wifiInfo.getLinkSpeed();
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }
        String localIP;
        try {
            localIP = InetAddress.getByAddress(BigInteger.valueOf(ipAddress).toByteArray()).getHostAddress();
        } catch (UnknownHostException e) {
            localIP = unavailable;
        }
        return Arrays.asList(
                new Item(R.layout.two_textviews_layout, "SSID", ssid == null ? unavailable : ssid),
                new Item(R.layout.two_textviews_layout, "BSSID", bssid == null ? unavailable : bssid),
                channel <= 0 ? new Item(R.layout.two_textviews_layout, getString(R.string.channel), unavailable)
                        : new Item(R.layout.two_textviews_layout, getString(R.string.channel), channel + ""),
                new Item(R.layout.two_textviews_layout, getString(R.string.frequency), frequency != -1 ? frequency + " MHz" : unavailable),
                new Item(R.layout.two_textviews_layout, getString(R.string.speed), linkSpeed != -1 ? linkSpeed + " Mbps" : unavailable),
                new Item(R.layout.two_textviews_layout, getString(R.string.rssi), wifiInfo.getRssi() + " dBm"),
                wifiManager.is5GHzBandSupported() ? new Item(R.layout.two_textviews_layout, getString(R.string._5_ghz_band), getString(R.string.supported))
                        : new Item(R.layout.two_textviews_layout, getString(R.string._5_ghz_band), getString(R.string.not_supported)),
                wifiManager.isP2pSupported() ? new Item(R.layout.two_textviews_layout, "Wi-Fi Direct", getString(R.string.supported))
                        : new Item(R.layout.two_textviews_layout, "Wi-Fi Direct", getString(R.string.not_supported)),
                new Item(R.layout.two_textviews_layout, getString(R.string.local_ip), localIP)
        );
    }

    private String fetchIP() throws Exception {
        URL url = new URL("https://antoniopietroluongo.altervista.org/checkip/index.php");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine).append("\n");
        }
        in.close();
        connection.disconnect();
        String result = sb.toString().split("\n")[5];
        return result.substring(0, result.length() - 4);
    }
}
