package com.example.deviceinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * @author Antonio Pietroluongo
 */
public class AppsFragment extends Fragment implements AdapterCallback {
    private boolean isTaskCompleted;
    private Context context;
    private CustomAdapter customAdapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private String path;

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null
                                && intent.getData() != null
                                && path != null)
                            write(intent.getData());
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);
        setHasOptionsMenu(true);
        context = requireActivity();
        recyclerView = view.findViewById(R.id.recyclerview_apps);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        customAdapter = new CustomAdapter(context, AppsFragment.this, new ArrayList<>());
        progressBar = view.findViewById(R.id.progressbar_apps);
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Looper.prepare();
            customAdapter.setData(getData());
            customAdapter.notifyDataSetChanged();
            handler.post(() -> {
                recyclerView.setAdapter(customAdapter);
                isTaskCompleted = true;
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            });
        });
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.search);
        if (!menuItem.isVisible())
            menuItem.setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private List<Item> getData() {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
        List<Item> items = new ArrayList<>();
        String version;
        for (ApplicationInfo app : installedApplications) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                String publicSourceDir = app.publicSourceDir;
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(app.packageName, 0);
                    version = packageInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    version = "0";
                    e.printStackTrace();
                }
                items.add(new Item(R.layout.three_textviews_layout, String.valueOf(packageManager.getApplicationLabel(app)),
                        app.packageName, version, publicSourceDir, packageManager.getApplicationIcon(app)));
            }
        }
        items.sort((o1, o2) -> {
            int v = o1.getStr1().compareToIgnoreCase(o2.getStr1());
            if (v == 0) return o1.getStr2().compareToIgnoreCase(o2.getStr2());
            return v;
        });
        return items;
    }

    private void write(Uri uri) {
        boolean b = false;
        try (InputStream in = new FileInputStream(path);
             OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                outputStream.write(buffer, 0, lengthRead);
            }
            b = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, b ? getString(R.string.saved)
                : getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isTaskCompleted) {
            customAdapter.setData(getData());
            customAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMethodCallback(Item item) {
        path = item.getStr4();
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.android.package-archive");
        intent.putExtra(Intent.EXTRA_TITLE,item.getStr1() + ".apk");
        activityResultLauncher.launch(intent);
    }
}
