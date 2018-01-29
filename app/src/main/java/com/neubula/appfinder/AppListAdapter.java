package com.neubula.appfinder;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quadri on 31/12/15.
 */
public class AppListAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater inflater = null;
    Context context = null;

    private List<ApplicationInfo> mOriginalValues; // Original Values
    private List<ApplicationInfo> mDisplayedValues;    // Values to be displayed

    public AppListAdapter(Context context, List<ApplicationInfo> packages) {
        this.context = context;
        this.mDisplayedValues = packages;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDisplayedValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mDisplayedValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        final ViewHolder holder;
        final ApplicationInfo packageInfo = mDisplayedValues.get(position);

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.adapter_app_list, null);

            holder.appIcon = (ImageView) view.findViewById(R.id.appIcon);
            holder.appName = (TextView) view.findViewById(R.id.appName);
            holder.appDetail = (TextView) view.findViewById(R.id.appDetail);
            holder.appParent = (RelativeLayout) view.findViewById(R.id.appParent);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (packageInfo.packageName != null) {
            holder.appDetail.setText(packageInfo.packageName);
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
        }

        /*view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });*/

        return view;
    }

    class ViewHolder {
        ImageView appIcon;
        TextView appName, appDetail;
        RelativeLayout appParent;
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
                List<ApplicationInfo> FilteredArrList = new ArrayList<ApplicationInfo>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<ApplicationInfo>(mDisplayedValues); // saves the original data in mOriginalValues
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
}
