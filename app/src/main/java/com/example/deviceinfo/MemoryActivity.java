package com.example.deviceinfo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author Antonio Pietroluongo
 */
public class MemoryActivity extends AppCompatActivity {
    private CustomAdapter customAdapter;
    private Timer timer;
    private TimerTask timerTask;
    private int totalMem;
    private double tBytes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_memory);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        customAdapter = new CustomAdapter(getData());
        recyclerView.setAdapter(customAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                    ActivityManager.MemoryInfo memoryInfo = getMemoryInfo();
                    long availableMemory = memoryInfo.availMem;
                    int availableMem = (int) (availableMemory * Math.pow(1024, -2));
                    int usedMemory = totalMem - availableMem;
                    StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
                    double availBytes = statFs.getAvailableBytes() * Math.pow(1024, -3);
                    Formatter formatterAvailBytes = new Formatter().format("%.2f GB", availBytes);
                    Formatter formatterUsedSpace = new Formatter().format("%.2f GB", tBytes - availBytes);
                    customAdapter.getData().get(1).setStr2(availableMem + " MB");
                    customAdapter.getData().get(2).setStr2(usedMemory + " MB");
                    customAdapter.getData().get(4).setStr2(formatterAvailBytes.toString());
                    customAdapter.getData().get(5).setStr2(formatterUsedSpace.toString());
                    customAdapter.notifyItemChanged(1);
                    customAdapter.notifyItemChanged(2);
                    customAdapter.notifyItemChanged(4);
                    customAdapter.notifyItemChanged(5);
                });
            }
        };
        timer.schedule(timerTask, 1, 2000);
    }

    private ActivityManager.MemoryInfo getMemoryInfo() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    private List<Item> getData() {
        ActivityManager.MemoryInfo memoryInfo = getMemoryInfo();
        long totalMemory = memoryInfo.totalMem;
        long availableMemory = memoryInfo.availMem;
        int availableMem = (int) (availableMemory * Math.pow(1024, -2));
        totalMem = (int) (totalMemory * Math.pow(1024, -2));
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long totBytes = statFs.getTotalBytes();
        tBytes = totBytes * Math.pow(1024, -3);
        double availBytes = statFs.getAvailableBytes() * Math.pow(1024, -3);
        Formatter formatterTotalBytes = new Formatter().format("%.2f GB", tBytes);
        Formatter formatterAvailBytes = new Formatter().format("%.2f GB", availBytes);
        Formatter formatterUsedSpace = new Formatter().format("%.2f GB", tBytes - availBytes);
        return Arrays.asList(
                new Item(R.layout.two_textviews_layout, getString(R.string.total_memory), totalMem + " MB"),
                new Item(R.layout.two_textviews_layout, getString(R.string.available_memory), availableMem + " MB"),
                new Item(R.layout.two_textviews_layout, getString(R.string.used_memory), totalMem - availableMem + " MB"),
                new Item(R.layout.two_textviews_layout, getString(R.string.total_space), formatterTotalBytes.toString()),
                new Item(R.layout.two_textviews_layout, getString(R.string.available_space), formatterAvailBytes.toString()),
                new Item(R.layout.two_textviews_layout, getString(R.string.used_space), formatterUsedSpace.toString()),
                new Item(R.layout.two_textviews_layout, getString(R.string.usb_host), getPackageManager().hasSystemFeature(PackageManager.FEATURE_USB_HOST) ? getString(R.string.supported)
                                : getString(R.string.not_supported))
        );
    }
}
