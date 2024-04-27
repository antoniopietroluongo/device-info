package com.example.deviceinfo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author Antonio Pietroluongo
 */
public class BatteryActivity extends AppCompatActivity {
    private CustomAdapter customAdapter;
    private Timer timer;
    private TimerTask timerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        RecyclerView recyclerView = findViewById(R.id.recyclerview_battery);
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
        BatteryManager batteryManager = (BatteryManager) getApplicationContext()
                .getSystemService(Context.BATTERY_SERVICE);
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        return Arrays.asList(
                new Item(R.layout.two_textviews_layout, getString(R.string.battery_level), batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) + "%"),
                new Item(R.layout.two_textviews_layout, getString(R.string.battery_power_source), pluggedFromIntent(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1))),
                new Item(R.layout.two_textviews_layout, getString(R.string.battery_status), statusFromIntent(intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1))),
                new Item(R.layout.two_textviews_layout, getString(R.string.battery_current_charge), batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER) / 1000 + " mAh"),
                new Item(R.layout.two_textviews_layout, getString(R.string.battery_health), healthFromIntent(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1))),
                new Item(R.layout.two_textviews_layout, getString(R.string.battery_technology), technology),
                new Item(R.layout.two_textviews_layout, getString(R.string.battery_temperature), intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0 + " °C"),
                new Item(R.layout.two_textviews_layout, getString(R.string.battery_voltage), intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) / 1000.0 + " V")
        );
    }

    private String healthFromIntent(int health) {
        return switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD -> getString(R.string.battery_health_cold);
            case BatteryManager.BATTERY_HEALTH_DEAD -> getString(R.string.battery_health_dead);
            case BatteryManager.BATTERY_HEALTH_GOOD -> getString(R.string.battery_health_good);
            case BatteryManager.BATTERY_HEALTH_OVERHEAT -> getString(R.string.battery_health_overheat);
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> getString(R.string.battery_health_over_voltage);
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> getString(R.string.battery_health_unspecified_failure);
            case BatteryManager.BATTERY_HEALTH_UNKNOWN -> getString(R.string.battery_health_unknown);
            default -> "Unexpected value: " + health;
        };
    }

    private String pluggedFromIntent(int plugged) {
        return switch (plugged) {
            case 0 -> getString(R.string.battery);
            case 1 -> getString(R.string.battery_plugged_ac_charger);
            case 2 -> getString(R.string.battery_plugged_usb_port);
            case 4 -> getString(R.string.battery_plugged_wireless);
            default -> "Unexpected value: " + plugged;
        };
    }

    private String statusFromIntent(int status) {
        return switch (status) {
            case BatteryManager.BATTERY_STATUS_UNKNOWN -> getString(R.string.battery_status_unknown);
            case BatteryManager.BATTERY_STATUS_CHARGING -> getString(R.string.battery_status_charging);
            case BatteryManager.BATTERY_STATUS_DISCHARGING -> getString(R.string.battery_status_discharging);
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING -> getString(R.string.battery_status_not_charging);
            case BatteryManager.BATTERY_STATUS_FULL -> getString(R.string.battery_status_full);
            default -> "Unexpected value: " + status;
        };
    }
}
