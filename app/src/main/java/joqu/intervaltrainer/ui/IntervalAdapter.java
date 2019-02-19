package joqu.intervaltrainer.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import joqu.intervaltrainer.R;
import joqu.intervaltrainer.model.Interval;
import joqu.intervaltrainer.model.Template;

// TODO: Populate ViewHolder with elements
// TODO: Add content of Intervallist viewholder
public class IntervalAdapter extends RecyclerView.Adapter<IntervalAdapter.IntervalListHolder> {
    private final LayoutInflater mLayoutInflater;
    private List<Interval> mIntervalList = Collections.emptyList();
    private Template mTemplate;
    public class IntervalListHolder extends RecyclerView.ViewHolder {
        // Viewholder items go here
       // private final TextView intervalListItem_name;
       // private final TextView intervalListItem_date;

        public IntervalListHolder(@NonNull View itemView) {
            super(itemView);
            // Viewholder items go here
           // intervalListItem_name = itemView.findViewById(R.id.intervalListItem_name);
           // intervalListItem_date = itemView.findViewById(R.id.intervalListItem_date);
        }
    }

    public IntervalAdapter(Context mContext) {
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public IntervalListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Get interval list recycler
        View itemView = mLayoutInflater.inflate(R.layout.recycler_interval_data, viewGroup, false);
        return new IntervalListHolder(itemView);
    }

    // Sets data to be presented in the viewholder
    public void setIntervalList(List<Interval> mIntervallist, Template mTemplate){
        // Sets the cached data to be presented and notify that there's been a change
        this.mIntervalList = mIntervallist;
        this.mTemplate = mTemplate;
        notifyDataSetChanged();
    }

    // Binds data to the holder
    @Override
    public void onBindViewHolder(@NonNull IntervalListHolder mViewHolder, int i) {
        // Bind data to the Holder
        // Get interval from list and add it data to the to the view
        if (mIntervalList != null && mTemplate != null){
            // bind all data columns to view elements
            //mViewHolder.intervalListItem_name.setText(mTemplate.description);
            //mViewHolder.intervalListItem_name.setText(mIntervalList.get(i).started);
        }

    }

    @Override
    public int getItemCount() {
        return mIntervalList.size();
    }


}
