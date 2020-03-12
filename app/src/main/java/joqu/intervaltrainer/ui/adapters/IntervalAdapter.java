package joqu.intervaltrainer.ui.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import joqu.intervaltrainer.R;
import joqu.intervaltrainer.Util;
import joqu.intervaltrainer.model.entities.Interval;
import joqu.intervaltrainer.model.entities.IntervalData;
import joqu.intervaltrainer.model.entities.Template;

// TODO: Populate ViewHolder with elements
// TODO: Add content of Intervallist viewholder
public class IntervalAdapter extends RecyclerView.Adapter<IntervalAdapter.IntervalListHolder> {
    private final LayoutInflater mLayoutInflater;
    private List<Interval> mIntervalList = Collections.emptyList();
    private Template mTemplate;
    public class IntervalListHolder extends RecyclerView.ViewHolder {
        // Viewholder items go here
       private final TextView intervalListItem_time;
        private final TextView intervalListItem_type;
       //private final TextView intervalListItem_date;

        public IntervalListHolder(@NonNull View itemView) {
            super(itemView);
            // Viewholder items go here
           intervalListItem_time = itemView.findViewById(R.id.IntervalItem_Time);
           intervalListItem_type = itemView.findViewById(R.id.IntervalItem_type);
        }
    }

    public IntervalAdapter(Context mContext) {
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public IntervalListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Get interval list recycler
        View itemView = mLayoutInflater.inflate(R.layout.recycler_interval, viewGroup, false);
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
    public void onBindViewHolder(@NonNull IntervalListHolder holder, int i) {
        // Bind data to the Holder
        // Get interval from list and add it data to the to the view
        if (mIntervalList != null && mTemplate != null){
            // bind all data columns to view elements
            String time = Util.millisToTimeFormat(mIntervalList.get(i).time,"mm:ss");
            holder.intervalListItem_time.setText(time);
            String type = IntervalData.getType(mIntervalList.get(i).type);
            holder.intervalListItem_type.setText(type);
            //mViewHolder.intervalListItem_name.setText(mIntervalList.get(i).started);
        }

    }

    @Override
    public int getItemCount() {
        return mIntervalList.size();
    }


}
