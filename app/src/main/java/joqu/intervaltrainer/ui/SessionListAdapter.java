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
import joqu.intervaltrainer.model.Session;
import joqu.intervaltrainer.model.Template;
// TODO: Populate ViewHolder with elements
// TODO: Add content of Sessionlist viewholder
public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.SessionListHolder> {
    private final LayoutInflater mLayoutInflater;
    private List<Session> mSessionList = Collections.emptyList();
    private List<Template> mTemplates;
    private final ItemClickListener mListener; // Clcik Listener for the recyclerview

    // TODO: Toggle UI color elements to show that item is being clicked on
    // Define a Clickable ViewHolder for the view
    public class SessionListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Viewholder items go here
        private final TextView sessionListItem_name;
        private final TextView sessionListItem_date;
        private final TextView sessionListItem_id;
        public SessionListHolder(@NonNull final View itemView) {
            super(itemView);
            // Viewholder items go here
            sessionListItem_name = itemView.findViewById(R.id.sessionListItem_name);
            sessionListItem_date = itemView.findViewById(R.id.sessionListItem_date);
            sessionListItem_id = itemView.findViewById(R.id.sessionListItem_id);
            itemView.setOnClickListener(this);

        }


        // Makes the viewholder clickable
        @Override
        public void onClick(View v) {
            // Send up the RecyclerView listener
            if (mSessionList!=null){// Make list has entities and then retrive id to be sent
                int entityId = mSessionList.get(getAdapterPosition()).id;
                mListener.onItemClick(v,entityId);
            }

        }
    }

    public SessionListAdapter(Context mContext, ItemClickListener mListener) {
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public SessionListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Get session list recycler
        View itemView = mLayoutInflater.inflate(R.layout.recycler_session_list, viewGroup, false);
        return new SessionListHolder(itemView);
    }

    // Sets data to be presented in the viewholder
    public void setSessionList(List<Session> mSessionlist, List<Template> mTemplates){
        // Sets the cached data to be presented and notify that there's been a change
        this.mSessionList = mSessionlist;
        this.mTemplates = mTemplates;
        notifyDataSetChanged();
    }

    // Binds data to the holder
    @Override
    public void onBindViewHolder(@NonNull SessionListHolder mViewHolder, final int i) {
        // Bind data to the Holder
        // Get session from list and add it data to the to the view

        if (mSessionList != null && mTemplates != null){

            // bind all data columns to view elements
            for (Template t :
                    mTemplates) {
                if (t.id == mSessionList.get(i).templateId)
                    mViewHolder.sessionListItem_name.setText(t.name);
            }
            mViewHolder.sessionListItem_date.setText(mSessionList.get(i).started);
            //mViewHolder.sessionListItem_id.setText(mSessionList.get(i).id);
        }

    }

    @Override
    public int getItemCount() {
        return mSessionList.size();
    }


}
