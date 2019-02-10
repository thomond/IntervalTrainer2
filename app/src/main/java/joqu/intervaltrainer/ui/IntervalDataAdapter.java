package joqu.intervaltrainer.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import joqu.intervaltrainer.R;
import joqu.intervaltrainer.model.IntervalData;

public class IntervalDataAdapter extends RecyclerView.Adapter<IntervalDataAdapter.IntervalDataViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private List<IntervalData> mIntervalData = Collections.emptyList();

    public class IntervalDataViewHolder extends RecyclerView.ViewHolder {
        // Interval related items
        private final TextView dataText;

        public IntervalDataViewHolder(@NonNull View itemView) {
            super(itemView);

            // Interval related items

            dataText = itemView.findViewById(R.id.IntervalDataItem_data);
        }
    }

    public IntervalDataAdapter(Context mContext) {
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public IntervalDataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Get the  Recycler View from layout and add it to a new Holder
        View itemView = mLayoutInflater.inflate(R.layout.interval_data_view, viewGroup, false);
        return new IntervalDataViewHolder(itemView);
    }

    public void setIntervalData(List<IntervalData> intervalData){
        // Sets the cached data to be presented and notify that there's been a change
        mIntervalData = intervalData;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull IntervalDataViewHolder intervalDataViewHolder, int i) {
        // Bind data to the Holder
        // Get session from list and add it data to the to the view
        if (mIntervalData != null){
            IntervalData intervalData = mIntervalData.get(i);
            // bind all data columns to view elements
            intervalDataViewHolder.dataText.setText(intervalData.data);

        }else{

            intervalDataViewHolder.dataText.setText("Nan");
        }

    }

    @Override
    public int getItemCount() {
        return mIntervalData.size();
    }


}
