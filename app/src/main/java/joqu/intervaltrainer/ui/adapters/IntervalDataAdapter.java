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

public class IntervalDataAdapter extends RecyclerView.Adapter<IntervalDataAdapter.IntervalDataViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private List<IntervalData> mIntervalData = Collections.emptyList();
    private List<Interval> mIntervalTypes = Collections.emptyList();
    public class IntervalDataViewHolder extends RecyclerView.ViewHolder {
        // Interval related items
        private TextView intervalDataItem_time,
                intervalDataItem_type,
                intervalDataItem_speed,
                intervalDataItem_pace,
                intervalDataItem_distance;

        public IntervalDataViewHolder(@NonNull View itemView) {
            super(itemView);

            // Interval related items
            intervalDataItem_time = itemView.findViewById(R.id.IntervalDataItem_Time);
            intervalDataItem_type = itemView.findViewById(R.id.IntervalDataItem_type);
            intervalDataItem_speed = itemView.findViewById(R.id.IntervalDataItem_speed);
            intervalDataItem_pace = itemView.findViewById(R.id.IntervalDataItem_pace);
        }
    }

    public IntervalDataAdapter(Context mContext) {
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public IntervalDataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Get the  Recycler View from layout and add it to a new Holder
        View itemView = mLayoutInflater.inflate(R.layout.recycler_interval_data, viewGroup, false);
        return new IntervalDataViewHolder(itemView);
    }

    public void setIntervalData(List<IntervalData> intervalData, List<Interval> intervalTypes){
        // Sets the cached data to be presented and notify that there's been a change
        mIntervalData = intervalData;
        mIntervalTypes = intervalTypes;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull IntervalDataViewHolder holder, int i) {
        // Bind data to the Holder
        // Get session from list and add it data to the to the view
        if (mIntervalData != null){
            IntervalData intervalData = mIntervalData.get(i);
            // Set interval activity type gyplh
            int typeID = mIntervalTypes.get(intervalData.step).type;
            String type = intervalData.getType(typeID);
            holder.intervalDataItem_type.setText(type);

           // set distance,speed,pace etc.
            long time = Long.valueOf(intervalData.ended) - Long.valueOf(intervalData.started);
            holder.intervalDataItem_time.setText(Util.millisToTimeFormat(time,"mm:ss"));



        }else{


        }

    }



    @Override
    public int getItemCount() {
        return mIntervalData.size();
    }


}
