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
 * Created by Antonio Pietroluongo on 1/1/2024.
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
        customAdapter = new CustomAdapter(cpuData());
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
        task();
    }

    public void task() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    customAdapter.setData(cpuData());
                    customAdapter.notifyDataSetChanged();

                });
            }
        };
        timer.schedule(timerTask, 1, 2000);
    }

    private List<Item> cpuData() {
        List<Item> data = new ArrayList<>();
        int numProcessors = Runtime.getRuntime().availableProcessors();
        int minFreq = 0;
        int maxFreq = 0;
        data.add(new Item(R.layout.two_line, getString(R.string.processor_name), Build.HARDWARE));
        data.add(new Item(R.layout.two_line, getString(R.string.cores), numProcessors + ""));
        data.add(null);
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
            data.add(new Item(R.layout.two_line, "Core " + (i + 1), curFreq + " MHz"));
        }
        data.set(2, new Item(R.layout.two_line, getString(R.string.clock), minFreq + " MHz - " + maxFreq + " MHz"));
        data.add(new Item(R.layout.two_line, getString(R.string.supported_32_bit_abis), String.join(",", Build.SUPPORTED_32_BIT_ABIS)));
        data.add(new Item(R.layout.two_line, getString(R.string.supported_64_bit_abis), String.join(",", Build.SUPPORTED_64_BIT_ABIS)));
        data.add(new Item(R.layout.two_line, getString(R.string.supported_abis), String.join(",", Build.SUPPORTED_ABIS)));
        return data;
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
