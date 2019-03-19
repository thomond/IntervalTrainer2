package joqu.intervaltrainer.ui;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import joqu.intervaltrainer.R;
import joqu.intervaltrainer.model.Session;

// TODO: generalise fragment to also show selected session
public class SessionListFragment extends Fragment implements ItemClickListener {


    private AppViewModel mViewModel;
    private int mSessionSelected; // The session id selelcted via the session list to be presented
    private RecyclerView mSessionDataRecyclerView;
    private  View mSessionView;

    public static SessionListFragment newInstance() {
        return new SessionListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_session, container, false);
        mSessionView = v.findViewById(R.id.ViewSavedSession);
        mSessionDataRecyclerView = v.findViewById(R.id.sessionListRecyclerView);
        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);
        showSessionList();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void showSessionList() {
        mSessionDataRecyclerView.setVisibility(View.VISIBLE);
        mSessionView.setVisibility(View.INVISIBLE);

        // Populate recycler with  items only if empty
        if (mSessionDataRecyclerView.getAdapter()==null) {
            final SessionListAdapter mAdapter = new SessionListAdapter(getContext(), this);
            mSessionDataRecyclerView.setAdapter(mAdapter);
            mSessionDataRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mAdapter.setSessionList(mViewModel.getAllSessions(), mViewModel.getAllTemplates());
        }
    }

    private void showSession(int sessionID) {
        // Hide session list and show session
        mSessionDataRecyclerView.setVisibility(View.INVISIBLE);
        mSessionView.setVisibility(View.VISIBLE);

        // Session information
        Session msess = mViewModel.getSessionById(sessionID);
        TextView sessionStartedText = getActivity().findViewById(R.id.sessionItem_started);
        sessionStartedText.setText(msess.started);
        TextView sessionEndedText = getActivity().findViewById(R.id.sessionItem_ended);
        sessionEndedText.setText(msess.ended);

        // Populate recycler for IntervalData items
        RecyclerView intervalDataRecyclerView = getActivity().findViewById(R.id.intervalDataView);
        final IntervalDataAdapter mAdapter = new IntervalDataAdapter(getContext());

        intervalDataRecyclerView.setAdapter(mAdapter);
        intervalDataRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onItemClick(View view, int position) {
        this.mSessionSelected = position;
        Log.i(TAG,"Click From View: "+view.toString());
        // TODO switch view to saved session based on ID
        showSession(this.mSessionSelected);

    }
}
