package joqu.intervaltrainer.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import joqu.intervaltrainer.R;
import joqu.intervaltrainer.model.Template;

import static android.content.ContentValues.TAG;

// TODO: generalise fragment to also show selected template
public class TemplateFragment extends Fragment implements ItemClickListener {


    private AppViewModel mViewModel;
    private int mTemplateSelected; // The template id selelcted via the template list to be presented

    public static TemplateFragment newInstance() {
        return new TemplateFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_template, container, false);
        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        // Populate recycler for  items
        RecyclerView templateDataRecyclerView = v.findViewById(R.id.templateListRecyclerView);

        final TemplateListAdapter mAdapter = new TemplateListAdapter(getContext());

        templateDataRecyclerView.setAdapter(mAdapter);
        templateDataRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter.setTemplateList(mViewModel.getAllTemplates());


        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

    private void showTemplate(int templateID) {
        /*View v = getActivity().findViewById(R.id.ViewSavedTemplate);
        v.setVisibility(View.VISIBLE);

        // Template information
        Template msess = mViewModel.getTemplateById(templateID);
        TextView templateStartedText = getActivity().findViewById(R.id.templateItem_started);
        templateStartedText.setText(msess.started);
        TextView templateEndedText = getActivity().findViewById(R.id.templateItem_ended);
        templateEndedText.setText(msess.ended);

        // Populate recycler for IntervalData items
        RecyclerView intervalDataRecyclerView = getActivity().findViewById(R.id.intervalDataView);
        final IntervalDataAdapter mAdapter = new IntervalDataAdapter(getContext());

        intervalDataRecyclerView.setAdapter(mAdapter);
        intervalDataRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));*/
    }

    @Override
    public void onItemClick(View view, int position) {
        this.mTemplateSelected = position;
        Log.i(TAG,"Click From View: "+view.toString());
        // TODO switch view to saved template based on ID
        showTemplate(this.mTemplateSelected);

    }
}
