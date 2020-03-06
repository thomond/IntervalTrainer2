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
import joqu.intervaltrainer.model.entities.Template;
import joqu.intervaltrainer.ui.ItemClickListener;

// TODO: Populate ViewHolder with elements
// TODO: Add content of templatelist viewholder
public class TemplateListAdapter extends RecyclerView.Adapter<TemplateListAdapter.TemplateListHolder> {
    private final LayoutInflater mLayoutInflater;
    private List<Template> mTemplateList = Collections.emptyList();
    private ItemClickListener mListener;

    public class TemplateListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Viewholder items go here
        private final TextView templateListItem_name;
        private final TextView templateListItem_time;
        private final TextView templateListItem_distance;

        public TemplateListHolder(@NonNull View itemView) {
            super(itemView);
            // Viewholder items go here
            templateListItem_name = itemView.findViewById(R.id.templateListItem_name);
            templateListItem_time = itemView.findViewById(R.id.templateListItem_time);
            templateListItem_distance = itemView.findViewById(R.id.templateListItem_distance);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Send up the RecyclerView listener
            if (mTemplateList!=null){// Make list has entities and then retrive id to be sent
                int entityId = mTemplateList.get(getAdapterPosition()).id;
                mListener.onItemClick(v,entityId);
            }
        }
    }

    public TemplateListAdapter(Context mContext, ItemClickListener mListener) {
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public TemplateListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Get  recycler
        View itemView = mLayoutInflater.inflate(R.layout.recycler_template_list, viewGroup, false);
        return new TemplateListHolder(itemView);
    }

    // Sets data to be presented in the viewholder
    public void setTemplateList(List<Template> mTemplatelist){
        // Sets the cached data to be presented and notify that there's been a change
        this.mTemplateList = mTemplatelist;
        notifyDataSetChanged();
    }

    // Binds data to the holder
    @Override
    public void onBindViewHolder(@NonNull TemplateListHolder mViewHolder, int i) {
        // Bind data to the Holder
        // Get template from list and add it data to the to the view
        if (mTemplateList != null ){
            // bind all data columns to view elements
            mViewHolder.templateListItem_time.setText(Util.millisToTimeFormat(mTemplateList.get(i).time,"mm:ss"));
            mViewHolder.templateListItem_name.setText(mTemplateList.get(i).name);
        }

    }

    @Override
    public int getItemCount() {
        return mTemplateList.size();
    }


}
