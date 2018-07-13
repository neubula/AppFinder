package com.neubula.appfinder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by quadri on 31/12/15.
 */
public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> implements Filterable {

    private LayoutInflater inflater = null;
    Context context = null;private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
//    PackageManager pm;

    private List<ApplicationInfo> mOriginalValues; // Original Values
    private List<ApplicationInfo> mDisplayedValues;    // Values to be displayed

    public AppListAdapter(Context context, List<ApplicationInfo> packages) {
        this.context = context;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        this.mDisplayedValues = packages;
//        this.pm = context.getPackageManager();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_app_list, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final ApplicationInfo packageInfo = mDisplayedValues.get(position);

        if (packageInfo.packageName != null) {
            holder.appDetail.setText(packageInfo.className);
            holder.udf1.setText(ApplicationInfo.getCategoryTitle(context, packageInfo.category));
            holder.udf2.setText(Integer.valueOf(packageInfo.minSdkVersion).toString());
            holder.udf3.setText(Integer.valueOf(packageInfo.flags).toString());
            holder.udf4.setText(packageInfo.backupAgentName);
            Drawable icon = null;
            String lable = null;
            try {
                icon = context.getPackageManager().getApplicationIcon(packageInfo.packageName);
                lable = context.getPackageManager().getApplicationLabel(packageInfo).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.appIcon.setImageDrawable(icon);
            holder.appName.setText(lable);

            holder.appIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse(packageInfo.packageName));
                    context.startActivity(intent);
                }
            });
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                if (packageInfo != null) {
                    if (context.getPackageManager().getLaunchIntentForPackage(packageInfo.packageName) != null) {
                        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "App will not start. No name.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "App will not start. No package.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mDisplayedValues.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                mDisplayedValues = (List<ApplicationInfo>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<ApplicationInfo> FilteredArrList = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = context.getPackageManager().getApplicationLabel(mOriginalValues.get(i)).toString();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(mOriginalValues.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.appIcon) ImageView appIcon;
        @BindView(R.id.appName) TextView appName;
        @BindView(R.id.appDetail) TextView appDetail;
        @BindView(R.id.udf1) TextView udf1;
        @BindView(R.id.udf2) TextView udf2;
        @BindView(R.id.udf3) TextView udf3;
        @BindView(R.id.udf4) TextView udf4;
        @BindView(R.id.appParent) RelativeLayout appParent;

        public String mBoundString;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }
}
