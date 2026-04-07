package com.darkman.wallet_3.ui.accumulation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.darkman.wallet_3.Balance;
import com.darkman.wallet_3.R;
import com.darkman.wallet_3.databinding.FragmentAccuBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.transition.MaterialSharedAxis;

import java.util.ArrayList;

public class AccuFragment extends Fragment implements BalanceAdapter.OnBalanceClickListener {
    public static final String TAG = "BalanceEditorFragment";
    private FragmentAccuBinding binding;
    private AccuViewModel accuViewModel;
    private BalanceAdapter balanceAdapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.Z, true));
        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.Z, false));
        accuViewModel = new ViewModelProvider(this).get(AccuViewModel.class);
        setupRecyclerView();
        observeViewModel();

        binding.fab.setOnClickListener(v -> showBalanceEditor(null));
    }

    private void setupRecyclerView() {
        balanceAdapter = new BalanceAdapter(requireContext(), new ArrayList<>(), this);
        RecyclerView recyclerView = binding.BalanceContainer;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(balanceAdapter);
    }

    private void observeViewModel() {
        accuViewModel.balances.observe(getViewLifecycleOwner(), balances -> {
            if (balances != null && balanceAdapter != null) {
                balanceAdapter.updateData(balances);
            }
        });

        accuViewModel.totalSum.observe(getViewLifecycleOwner(), total -> {
            binding.sumText.setText(getString(R.string.sum) + ": " + total);
        });
    }

    private void showBalanceEditor(@Nullable Balance balance) {

        BalanceEditorFragment editorFragment = BalanceEditorFragment.newInstance(null);
        editorFragment.show(getChildFragmentManager(), BalanceEditorFragment.TAG);

    }

    public void onBalanceClick(Balance balance) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("balance_data", balance);

        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_accu_to_goal, bundle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        accuViewModel.loadBalances();
    }
}
