package ch.beerpro.presentation.profile.myFridge;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ch.beerpro.R;
import ch.beerpro.domain.models.MyBeer;
import ch.beerpro.data.repositories.CurrentUser;

public class MyFridgeFragment extends Fragment {

    private static final String TAG = "MyFridgeFragment";

    private OnMyFridgeItemInteractionListener interactionListener;

    private MyFridgeRecyclerViewAdapter adapter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.emptyView)
    View emptyView;

    public MyFridgeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchresult_list, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        MyFridgeViewModel model = ViewModelProviders.of(getActivity()).get(MyFridgeViewModel.class);
        model.getMyFilteredBeers().observe(getActivity(), this::handleBeersChanged);

        adapter = new MyFridgeRecyclerViewAdapter(interactionListener, model.getCurrentUser());

        recyclerView.setAdapter(adapter);
        return view;
    }

    private void handleBeersChanged(List<MyBeer> beers) {
        adapter.submitList(new ArrayList<>(beers));
        if (beers.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMyFridgeItemInteractionListener) {
            interactionListener = (OnMyFridgeItemInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }
}