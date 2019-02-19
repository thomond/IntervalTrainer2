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
import joqu.intervaltrainer.model.Template;

// TODO: Populate ViewHolder with elements
// TODO: Add content of templatelist viewholder
public class TemplateListAdapter extends RecyclerView.Adapter<TemplateListAdapter.TemplateListHolder> {
    private final LayoutInflater mLayoutInflater;
    private List<Template> mTemplateList = Collections.emptyList();
    
    public class TemplateListHolder extends RecyclerView.ViewHolder {
        // Viewholder items go here
        private final TextView templateListItem_name;
        private final TextView templateListItem_desc;

        public TemplateListHolder(@NonNull View itemView) {
            super(itemView);
            // Viewholder items go here
            templateListItem_name = itemView.findViewById(R.id.templateListItem_name);
            templateListItem_desc = itemView.findViewById(R.id.templateListItem_desc);
        }
    }

    public TemplateListAdapter(Context mContext) {
        mLayoutInflater = LayoutInflater.from(mContext);
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
            mViewHolder.templateListItem_desc.setText(mTemplateList.get(i).description);
            mViewHolder.templateListItem_name.setText(mTemplateList.get(i).name);
        }

    }

    @Override
    public int getItemCount() {
        return mTemplateList.size();
    }


}
