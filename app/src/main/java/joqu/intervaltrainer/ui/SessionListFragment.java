package joqu.intervaltrainer.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import joqu.intervaltrainer.R;

public class SessionListFragment extends Fragment {


    private SavedSessionViewModel mViewModel;

    public static SessionListFragment newInstance() {
        return new SessionListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.session_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SavedSessionViewModel.class);

        // Populate recycler for  items
        RecyclerView sessionDataRecyclerView = getView().findViewById(R.id.sessionListRecyclerView);

        final SessionListAdapter mAdapter = new SessionListAdapter(getContext(), new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // FIXME: Point to saved session fragment
                // showSession(mViewModel,position);
            }
        });

        sessionDataRecyclerView.setAdapter(mAdapter);
        sessionDataRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter.setSessionList(mViewModel.getAllSessions(),mViewModel.getAllTemplates());


    }

}
