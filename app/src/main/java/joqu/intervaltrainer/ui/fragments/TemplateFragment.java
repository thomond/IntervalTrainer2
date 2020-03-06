package joqu.intervaltrainer.ui.fragments;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import joqu.intervaltrainer.R;
import joqu.intervaltrainer.Util;
import joqu.intervaltrainer.model.entities.Interval;
import joqu.intervaltrainer.model.entities.Template;
import joqu.intervaltrainer.ui.AppViewModel;
import joqu.intervaltrainer.ui.adapters.IntervalAdapter;
import joqu.intervaltrainer.ui.ItemClickListener;
import joqu.intervaltrainer.ui.adapters.TemplateListAdapter;

import static android.content.ContentValues.TAG;

// TODO: generalise fragment to also show selected template
public class TemplateFragment extends Fragment implements ItemClickListener, View.OnClickListener {


    private AppViewModel mViewModel;
    private int mTemplateSelected; // The template id selelcted via the template list to be presented

    private RecyclerView intervalRecyclerView;
    private RecyclerView templateListRecyclerView;
    private TextView templateListItem_name;
    private TextView templateListItem_time;
    private TextView templateListItem_distance;
    private Button template_btnNewSession;
    private View mTemplateView;
    private View mTemplateListView;
    private int mTemplateID;
    private TextView templateListItem_intervals;

    public static TemplateFragment newInstance() {
        return new TemplateFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_template, container, false);
        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        templateListRecyclerView = v.findViewById(R.id.templateListRecyclerView);
        mTemplateView = v.findViewById(R.id.templateView);
        mTemplateListView = v.findViewById(R.id.templateListView);
        // Viewholder items go here
        templateListItem_name = v.findViewById(R.id.templateListItem_name);
        templateListItem_time = v.findViewById(R.id.templateListItem_time);
        templateListItem_intervals = v.findViewById(R.id.templateListItem_intervals);
        templateListItem_distance = v.findViewById(R.id.templateListItem_distance);
        template_btnNewSession = v.findViewById(R.id.template_btnNewSession);
        intervalRecyclerView =  v.findViewById(R.id.intervalView);

        showTemplateList();



        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

    private void showTemplateList(){
        mTemplateListView.setVisibility(View.VISIBLE);
        mTemplateView.setVisibility(View.GONE);
        // Populate recycler for  items
        final TemplateListAdapter mAdapter = new TemplateListAdapter(getContext(), this);
        templateListRecyclerView.setAdapter(mAdapter);
        templateListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter.setTemplateList(mViewModel.getAllTemplates());
    }

    private void showTemplate(final int templateID) {
        mTemplateListView.setVisibility(View.GONE);
        mTemplateView.setVisibility(View.VISIBLE);
        mTemplateID = templateID;
        // Template information
        Template template = mViewModel.getSessionTemplate(templateID);
        templateListItem_name.setText(template.name);
        templateListItem_time.setText(Util.millisToTimeFormat(template.time,"mm:ss"));

        template_btnNewSession.setOnClickListener(this);

        // Retrive template Intervals and set the number
        List<Interval> intervals = mViewModel.getTemplateIntervals(templateID);
        templateListItem_intervals.setText(String.valueOf(intervals.size()));

        final IntervalAdapter mAdapter = new IntervalAdapter(getContext());
        intervalRecyclerView.setAdapter(mAdapter);
        intervalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter.setIntervalList(intervals,mViewModel.getSessionTemplate(templateID));
    }

    @Override
    public void onItemClick(View view, int position) {
        this.mTemplateSelected = position;
        Log.i(TAG,"Click From View: "+view.toString());
        // TODO switch view to saved template based on ID
        showTemplate(this.mTemplateSelected);

    }

    @Override
    public void onClick(View v) {



        // Include template id in argument list
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putInt("template_id",mTemplateID);

        MainActivity.switchFragment(LiveSessionFragment.newInstance(),R.id.mainContentFrame,getActivity().getSupportFragmentManager(),fragmentBundle);


    }
}
