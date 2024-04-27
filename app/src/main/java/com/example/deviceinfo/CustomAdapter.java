package com.example.deviceinfo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;


import java.util.ArrayList;
import java.util.List;


/**
 * @author Antonio Pietroluongo
 */
public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private AdapterCallback adapterCallback;
    private Context context;
    private List<Item> data;
    private List<Item> mData;
    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> filtered = new ArrayList<>();
            if (constraint == null || constraint.length() == 0)
                filtered.addAll(mData);
            else {
                String s = constraint.toString().toLowerCase().trim();
                for (Item item : mData) {
                    if (item.getStr1().toLowerCase().contains(s)) {
                        filtered.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filtered;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data.clear();
            ((List<?>) results.values).stream().filter(o -> o instanceof Item)
                    .forEach(o -> data.add((Item) o));
            notifyDataSetChanged();
        }
    };


    public CustomAdapter(List<Item> data) {
        this.data = data;
        this.mData = new ArrayList<>(data);
    }

    public CustomAdapter(Context context, List<Item> data) {
        this.context = context;
        this.data = data;
        this.mData = new ArrayList<>(data);
    }

    public CustomAdapter(Context context, AdapterCallback adapterCallback, List<Item> data) {
        this.context = context;
        this.adapterCallback = adapterCallback;
        this.data = data;
        this.mData = new ArrayList<>(data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView1;
        private TextView textView2;
        private TextView textView3;
        private ImageView imageView;


        public ViewHolder(View view, int viewType) {
            super(view);
            if (viewType == R.layout.one_textview_layout) {
                textView1 = view.findViewById(R.id.text_view);
            } else if (viewType == R.layout.two_textviews_layout) {
                textView1 = view.findViewById(R.id.text_1);
                textView2 = view.findViewById(R.id.text_2);
            } else if (viewType == R.layout.three_textviews_layout) {
                textView1 = view.findViewById(R.id.text_view1);
                textView2 = view.findViewById(R.id.text_view2);
                textView3 = view.findViewById(R.id.text_view3);
                imageView = view.findViewById(R.id.image_view);
            }
        }

        public TextView getTextView1() {
            return textView1;
        }

        public void setTextView1(TextView textView1) {
            this.textView1 = textView1;
        }

        public TextView getTextView2() {
            return textView2;
        }

        public void setTextView2(TextView textView2) {
            this.textView2 = textView2;
        }

        public TextView getTextView3() {
            return textView3;
        }

        public void setTextView3(TextView textView3) {
            this.textView3 = textView3;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }
    }

    public void setData(List<Item> data) {
        this.data = data;
        this.mData = new ArrayList<>(data);
    }

    public List<Item> getData() {
        return data;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == R.layout.one_textview_layout) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.one_textview_layout, viewGroup, false);
            return new ViewHolder(view, viewType);
        } else if (viewType == R.layout.two_textviews_layout) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.two_textviews_layout, viewGroup, false);
            return new ViewHolder(view, viewType);
        } else if (viewType == R.layout.three_textviews_layout) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.three_textviews_layout, viewGroup, false);
            return new ViewHolder(view, viewType);
        } else {
            throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == R.layout.one_textview_layout) {
            ViewHolder mViewHolder = (ViewHolder) viewHolder;
            Item item = data.get(position);
            mViewHolder.getTextView1().setText(item.getStr1());
            mViewHolder.itemView.setOnClickListener(v -> {
                int i = mViewHolder.getAdapterPosition();
                Intent intent = switch (i) {
                    case 0 -> new Intent(context, CPUActivity.class);
                    case 1 -> new Intent(context, MemoryActivity.class);
                    case 2 -> new Intent(context, DisplayActivity.class);
                    case 3 -> new Intent(context, BatteryActivity.class);
                    case 4 -> new Intent(context, NetworkActivity.class);
                    case 5 -> new Intent(context, DeviceActivity.class);
                    default -> new Intent(context, AppsActivity.class);
                };
                context.startActivity(intent);
            });
        } else if (viewHolder.getItemViewType() == R.layout.two_textviews_layout) {
            ViewHolder mViewHolder = (ViewHolder) viewHolder;
            Item item = data.get(position);
            mViewHolder.getTextView1().setText(item.getStr1());
            mViewHolder.getTextView2().setText(item.getStr2());
            mViewHolder.itemView.setOnLongClickListener(view -> {
                ClipboardManager clipboardManager = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(mViewHolder.textView1.getText(), mViewHolder.textView2.getText());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(view.getContext(), R.string.copied, Toast.LENGTH_LONG).show();
                return true;
            });
        } else if (viewHolder.getItemViewType() == R.layout.three_textviews_layout) {
            ViewHolder mViewHolder = (ViewHolder) viewHolder;
            Item item = data.get(position);
            mViewHolder.getTextView1().setText(item.getStr1());
            mViewHolder.getTextView2().setText(item.getStr2());
            mViewHolder.getTextView3().setText(item.getStr3());
            mViewHolder.getImageView().setImageDrawable(item.getIcon());
            mViewHolder.itemView.setOnClickListener(v -> {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_content);
                LinearLayout open = bottomSheetDialog.findViewById(R.id.linear_layout_open);
                LinearLayout extract = bottomSheetDialog.findViewById(R.id.linear_layout_extract);
                LinearLayout uninstall = bottomSheetDialog.findViewById(R.id.linear_layout_uninstall);
                LinearLayout manage = bottomSheetDialog.findViewById(R.id.linear_layout_manage);
                bottomSheetDialog.show();
                if (open != null) {
                    open.setOnClickListener(view -> {
                        Intent intent = context.getPackageManager().getLaunchIntentForPackage(item.getStr2());
                        if (intent != null)
                            context.startActivity(intent);
                        bottomSheetDialog.dismiss();
                    });
                }
                if (extract != null) {
                    extract.setOnClickListener(view -> {
                        adapterCallback.onMethodCallback(item);
                        bottomSheetDialog.dismiss();
                    });
                }
                if (uninstall != null) {
                    uninstall.setOnClickListener(view -> {
                        Intent intent = new Intent(Intent.ACTION_DELETE);
                        Uri uri = Uri.fromParts("package", String.valueOf(item.getStr2()), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                        bottomSheetDialog.dismiss();
                    });
                }
                if (manage != null) {
                    manage.setOnClickListener(view -> {
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", String.valueOf(item.getStr2()), null);
                            intent.setData(uri);
                            context.startActivity(intent);
                            bottomSheetDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        } else {
            throw new IllegalStateException("Unexpected value: " + viewHolder.getItemViewType());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }
}
