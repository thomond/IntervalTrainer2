package joqu.intervaltrainer.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import joqu.intervaltrainer.R;

import static joqu.intervaltrainer.ui.fragments.MainActivity.switchFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private Button mBtnNewSession;
    private Button mBtnViewHistory;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        // Create button actions
        mBtnNewSession = v.findViewById(R.id.btnNewSession);
        mBtnNewSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(TemplateFragment.newInstance(), R.id.mainContentFrame,getActivity().getSupportFragmentManager());
            }
        });

        // Create button actions
        mBtnViewHistory = v.findViewById(R.id.btnViewHistory);
        mBtnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(SavedSessionFragment.newInstance(), R.id.mainContentFrame,getActivity().getSupportFragmentManager());
            }
        });

        return v;
    }
}
