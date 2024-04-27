package com.example.deviceinfo;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author Antonio Pietroluongo
 */
public class CPUActivity extends AppCompatActivity {
    private CustomAdapter customAdapter;
    private Timer timer;
    private TimerTask timerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_cpu);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        customAdapter = new CustomAdapter(getData());
        recyclerView.setAdapter(customAdapter);
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
                    customAdapter.setData(getData());
                    customAdapter.notifyDataSetChanged();

                });
            }
        };
        timer.schedule(timerTask, 1, 2000);
    }

    private List<Item> getData() {
        List<Item> items = new ArrayList<>();
        int numProcessors = Runtime.getRuntime().availableProcessors();
        int minFreq = 0;
        int maxFreq = 0;
        items.add(new Item(R.layout.two_textviews_layout, getString(R.string.processor_name), Build.HARDWARE));
        items.add(new Item(R.layout.two_textviews_layout, getString(R.string.cores), numProcessors + ""));
        items.add(null);
        for (int i = 0; i < numProcessors; i++) {
            int curFreq = readCpuFreqAsInt("/sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq") / 1000;
            int min = readCpuFreqAsInt("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_min_freq") / 1000;
            int max = readCpuFreqAsInt("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq") / 1000;
            if (min < minFreq || minFreq == 0) {
                minFreq = min;
            }
            if (max > maxFreq || maxFreq == 0) {
                maxFreq = max;
            }
            items.add(new Item(R.layout.two_textviews_layout, "Core " + (i + 1), curFreq + " MHz"));
        }
        items.set(2, new Item(R.layout.two_textviews_layout, getString(R.string.clock), minFreq + " MHz - " + maxFreq + " MHz"));
        items.add(new Item(R.layout.two_textviews_layout, getString(R.string.supported_32_bit_abis), String.join(",", Build.SUPPORTED_32_BIT_ABIS)));
        items.add(new Item(R.layout.two_textviews_layout, getString(R.string.supported_64_bit_abis), String.join(",", Build.SUPPORTED_64_BIT_ABIS)));
        items.add(new Item(R.layout.two_textviews_layout, getString(R.string.supported_abis), String.join(",", Build.SUPPORTED_ABIS)));
        return items;
    }

    private int readCpuFreqAsInt(String path) throws NullPointerException {
        File file = new File(path);
        try (Scanner in = new Scanner(file)){
            if (!file.canRead()) {
                throw new AccessDeniedException("File can't be read.");
            }
            StringBuilder sb = new StringBuilder();
            while (in.hasNextLine()) {
                sb.append(in.nextLine());
            }
            return Integer.parseInt(sb.toString());
        } catch (FileNotFoundException | AccessDeniedException e) {
            return 0;
        }
    }
}
