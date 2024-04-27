package com.example.deviceinfo;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;


/**
 * @author Antonio Pietroluongo
 */
public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Display display;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = this.getDisplay();
        } else {
            WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            display = windowManager.getDefaultDisplay();
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);
        float xdpi = displayMetrics.xdpi;
        float ydpi = displayMetrics.ydpi;
        float scaledDensity = displayMetrics.scaledDensity;
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        int dpi = displayMetrics.densityDpi;
        double size = (Math.sqrt((width / xdpi) * (width / xdpi) + (height / ydpi) * (height / ydpi)));
        RecyclerView recyclerView = findViewById(R.id.recyclerview_display);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        CustomAdapter customAdapter = new CustomAdapter(Arrays.asList(
                new Item(R.layout.two_textviews_layout, getString(R.string.size), getString(R.string.inches, String.valueOf(Math.round(size * 10) / 10.0))),
                new Item(R.layout.two_textviews_layout, getString(R.string.resolution), width + " x " + height),
                new Item(R.layout.two_textviews_layout, getString(R.string.xdpi), Math.round(xdpi) + ""),
                new Item(R.layout.two_textviews_layout, getString(R.string.ydpi), Math.round(ydpi) + ""),
                new Item(R.layout.two_textviews_layout, getString(R.string.font_scale), scaledDensity + ""),
                new Item(R.layout.two_textviews_layout, getString(R.string.density), dpi + " dpi"),
                new Item(R.layout.two_textviews_layout, getString(R.string.refresh_rate), (int) display.getRefreshRate() + " Hz")));
        recyclerView.setAdapter(customAdapter);
    }
}
