package joqu.intervaltrainer.ui;
import static android.content.ContentValues.TAG;
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
import joqu.intervaltrainer.model.Session;

// TODO: generalise fragment to also show selected session
public class SessionListFragment extends Fragment implements ItemClickListener {


    private AppViewModel mViewModel;
    private int mSessionSelected; // The session id selelcted via the session list to be presented

    public static SessionListFragment newInstance() {
        return new SessionListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_session, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        // Populate recycler for  items
        RecyclerView sessionDataRecyclerView = getView().findViewById(R.id.sessionListRecyclerView);

        final SessionListAdapter mAdapter = new SessionListAdapter(getContext(), this);

        sessionDataRecyclerView.setAdapter(mAdapter);
        sessionDataRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter.setSessionList(mViewModel.getAllSessions(),mViewModel.getAllTemplates());



    }

    private void showSession(int sessionID) {
        View v = getActivity().findViewById(R.id.ViewSavedSession);
        v.setVisibility(View.VISIBLE);

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
