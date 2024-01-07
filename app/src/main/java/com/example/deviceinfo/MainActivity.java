package com.example.deviceinfo;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;


/**
 * Created by Antonio Pietroluongo on 1/1/2024.
 */
public class MainActivity extends AppCompatActivity {
    private boolean backPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_main);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        CustomAdapter customAdapter = new CustomAdapter(this, List.of(
                new Item(R.layout.one_line, getString(R.string.processor)),
                new Item(R.layout.one_line, getString(R.string.memory)),
                new Item(R.layout.one_line, getString(R.string.display)),
                new Item(R.layout.one_line, getString(R.string.battery)),
                new Item(R.layout.one_line, getString(R.string.network)),
                new Item(R.layout.one_line, getString(R.string.device)),
                new Item(R.layout.one_line, getString(R.string.apps))));
        recyclerView.setAdapter(customAdapter);
    }

    @Override
    public void onBackPressed() {
        if (backPressedOnce)
            super.onBackPressed();
        backPressedOnce = true;
        Toast.makeText(MainActivity.this, getString(R.string.touch_again_to_exit), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> backPressedOnce = false, 2000);
    }

}