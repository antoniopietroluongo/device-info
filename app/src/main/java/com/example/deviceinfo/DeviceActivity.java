package com.example.deviceinfo;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.time.Duration;
import java.util.List;
import java.util.Locale;


/**
 * Created by Antonio Pietroluongo on 1/1/2024.
 */
public class DeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_device);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        CustomAdapter customAdapter = new CustomAdapter(List.of(
                new Item(R.layout.two_line, getString(R.string.display), Build.DISPLAY),
                new Item(R.layout.two_line, getString(R.string.product), Build.PRODUCT),
                new Item(R.layout.two_line, getString(R.string.device), Build.DEVICE),
                new Item(R.layout.two_line, getString(R.string.board), Build.BOARD),
                new Item(R.layout.two_line, getString(R.string.manufacturer), Build.MANUFACTURER),
                new Item(R.layout.two_line, getString(R.string.brand), Build.BRAND),
                new Item(R.layout.two_line, getString(R.string.model), Build.MODEL),
                new Item(R.layout.two_line, getString(R.string.fingerprint), Build.FINGERPRINT),
                new Item(R.layout.two_line, getString(R.string.host), Build.HOST),
                new Item(R.layout.two_line, getString(R.string.language), Locale.getDefault().getDisplayLanguage()),
                new Item(R.layout.two_line, getString(R.string.elapsed_time), elapsedTime())));
        recyclerView.setAdapter(customAdapter);
    }

    private String elapsedTime() {
        long milliseconds = SystemClock.elapsedRealtime();
        Duration d = Duration.ofMillis(milliseconds);
        long val = Math.abs(d.toMinutes());
        int hours = (int) (val / 60);
        int minutes = (int) (val % 60);
        int days = hours / 24;
        hours = hours % 24;
        return ((days == 0 ? ""
                : days == 1 ? "1 " + getString(R.string.day)
                : days + " " + getString(R.string.days)) +
                " " +
                (hours == 0 ? ""
                        : hours == 1 ? "1 " + getString(R.string.hour)
                        : hours + " " + getString(R.string.hours)) +
                " " +
                (minutes == 0 ? "0 " + getString(R.string.minutes)
                        : minutes == 1 ? "1 " + getString(R.string.minute)
                        : minutes + " " + getString(R.string.minutes))).trim();
    }
}