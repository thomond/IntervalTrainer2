package joqu.intervaltrainer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.SessionViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private List<Session> mSessions = Collections.emptyList();
    private List<IntervalData> mIntervalData = Collections.emptyList();

    public class SessionViewHolder extends RecyclerView.ViewHolder {
        // Add relevent fileds relating to session
        private final TextView StartedItemView;
        private final TextView EndedItemView;
        private final TextView DataItemView;
        private final TextView TemplateNameItemView;
        // Interval related items
        private final TextView IntervalDataItemViews;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);

            StartedItemView = itemView.findViewById(R.id.StartedItemView);
            EndedItemView = itemView.findViewById(R.id.EndedItemView);
            DataItemView = itemView.findViewById(R.id.DataItemView);
            TemplateNameItemView = itemView.findViewById(R.id.TemplateNameItemView);
            // Interval related items
            IntervalDataItemViews = itemView.findViewById(R.id.IntervalDataItemViews);
        }
    }

    public SessionListAdapter(Context mContext) {
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Get the  Recycler View from layout and add it to a new Holder
        View itemView = mLayoutInflater.inflate(R.layout.recyclerview_item, viewGroup, false);
        return new SessionViewHolder(itemView);
    }

    void setSessions(List<Session> sessions){
        // Sets the cached data to be presented and notify that there's been a change
        mSessions = sessions;
        notifyDataSetChanged();
    }


    void setIntervalData(List<IntervalData> intervalData){
        // Sets the cached data to be presented and notify that there's been a change
        mIntervalData = intervalData;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder sessionViewHolder, int i) {
        // Bind data to the Holder
        // Get session from list and add it data to the to the view
        if (mSessions != null){
            Session mSession = mSessions.get(i);
            // bind all data columns to view elements
            sessionViewHolder.StartedItemView.setText(mSession.started);
            sessionViewHolder.EndedItemView.setText(mSession.ended);
            sessionViewHolder.DataItemView.setText(mSession.data);
            sessionViewHolder.TemplateNameItemView.setText(String.valueOf(mSession.templateId));
            // Add text for eac item in Interval data list
            for (IntervalData mi:
                 mIntervalData) {
                sessionViewHolder.IntervalDataItemViews.append(mi.data);
            }

        }else{
            sessionViewHolder.StartedItemView.setText("Nan");
            sessionViewHolder.EndedItemView.setText("Nan");
            sessionViewHolder.DataItemView.setText("Nan");
            sessionViewHolder.TemplateNameItemView.setText("Nan");
            sessionViewHolder.IntervalDataItemViews.setText("Nan");
        }

    }

    @Override
    public int getItemCount() {
        return mSessions.size();
    }


}
