package com.darkman.wallet_3.ui.accumulation;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.darkman.wallet_3.R;
import com.darkman.wallet_3.databinding.FragmentGoalBinding;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.android.material.transition.MaterialSharedAxis;

public class GoalFragment extends Fragment {

    private FragmentGoalBinding binding;
    private GoalViewModel viewModel;
    private HistoryAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGoalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.Z, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.Z, false));



        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.Z, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.Z, false));

        if (getArguments() != null) {
            Balance balance = (Balance) getArguments().getSerializable("balance_data");
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null && activity.getSupportActionBar() != null) {
                assert balance != null;
                activity.getSupportActionBar().setTitle(balance.name);
            }
            historyAdapter = new HistoryAdapter(balance);
            binding.historyList.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.historyList.setAdapter(historyAdapter);
            if (balance != null) {
                if (getArguments() != null) {
                        setupViewModel(balance.id);
                        initUI();
                        observeViewModel();
                }
                setupViewModel(balance.id);
                initUI();
                observeViewModel();
            }
        }
    }

    private void setupViewModel(int balanceId) {
        GoalActivityViewModelFactory factory = new GoalActivityViewModelFactory(
                requireActivity().getApplication(), balanceId);
        viewModel = new ViewModelProvider(this, factory).get(GoalViewModel.class);
    }

    private void initUI() {
        binding.btnShowMore.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(binding.getRoot());
            if (binding.btnShowMore.getText().toString().equals("Show more")) {
                historyAdapter.setExpanded(true);
                binding.btnShowMore.setText("Show less");
            } else {
                historyAdapter.setExpanded(false);
                binding.btnShowMore.setText("Show more");
            }
        });

        MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        divider.setDividerInsetStart(70);
        divider.setDividerInsetEnd(16);
        divider.setLastItemDecorated(false);
        binding.historyList.addItemDecoration(divider);

        binding.fab.setOnClickListener(view -> showAddTransactionDialog());
    }
    private void showAddTransactionDialog() {
        getParentFragmentManager().setFragmentResultListener("add_transaction", getViewLifecycleOwner(), (requestKey, result) -> {
            int amount = result.getInt("amount");
            String date = result.getString("date");

            viewModel.addTransaction(amount, 0, date);
        });

        AddTransaction dialog = new AddTransaction();
        dialog.show(getParentFragmentManager(), "AddTransactionTag");
    }
    private String formatDate(int day, int month, int year) {
        return String.format(java.util.Locale.getDefault(), "%02d.%02d.%04d", day, month + 1, year);
    }

    private void observeViewModel() {
        viewModel.balance.observe(getViewLifecycleOwner(), this::updateBalanceUI);

        viewModel.history.observe(getViewLifecycleOwner(), histories -> {
            if (histories != null) {
                historyAdapter.submitList(histories);
                binding.btnShowMore.setVisibility(histories.size() > 5 ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void updateBalanceUI(Balance balance) {
        if (balance == null) return;
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(balance.name);
        }
        float percent = 0;
        if (balance.maxScore > 0) {
            percent = (balance.score * 100f) / balance.maxScore;
        }

        if (percent > 100f) percent = 100f;
        if (percent < 0) percent = 0;
        int actualColor = ContextCompat.getColor(requireContext(), balance.colorResId);
        binding.circularGoalProgress.animateProgress(percent, actualColor);
        binding.goalDetailIcon.setImageResource(balance.iconResId);

        updateStats(balance);

    }
    @SuppressLint("SetTextI18n")
    private void updateStats(Balance balance) {
        viewModel.statsState.observe(getViewLifecycleOwner(), stats -> {
            if (stats == null) return;

        binding.determinateBar.setProgress(stats.safeTimeProgress);
        binding.tvProgressLabel.setText(stats.safeTimeProgress + "%");
        binding.determinateBar.post(() -> {
            int width = binding.determinateBar.getWidth();
            float xPosition = ((float) stats.safeTimeProgress / 100) * width;
            float finalX = Math.max(0, Math.min(xPosition - (binding.tvProgressLabel.getWidth() / 2f), width - binding.tvProgressLabel.getWidth()));
            binding.tvProgressLabel.setTranslationX(finalX);
        });

        setStatCard(binding.statScore.getRoot(), balance.score + " / " + balance.maxScore, getString(R.string.goal_score));
        setStatCard(binding.statRemains.getRoot(), String.valueOf(stats.remainingScore), getString(R.string.goal_remains));
        setStatCard(binding.statDaily.getRoot(), stats.realPlanPerWeek + " / " + stats.basePlanPerWeek, getString(R.string.stat_plan));
        setStatCard(binding.statPercent.getRoot(), stats.performanceText, getString(R.string.performance));
        setStatCard(binding.statState.getRoot(), stats.statusDescription, getString(R.string.goal_status));
        setStatCard(binding.statLeftDays.getRoot(), stats.remainingDays + " / " + stats.totalDays, getString(R.string.goal_days_left));
        setStatCard(binding.statPassedDays.getRoot(), String.valueOf(stats.passedDays), getString(R.string.goal_days_passed));

        binding.statStart.setText(formatLongDate(balance.basedDay));
        binding.statEnd.setText(formatLongDate(balance.targetDay));
        });
    }
    public void updateProgress(int progress) {
        ProgressBar progressBar = binding.determinateBar;
        TextView tvLabel = binding.tvProgressLabel;

        progressBar.setProgress(progress);
        tvLabel.setText(progress + "%");

        progressBar.post(() -> {
            int width = progressBar.getWidth();
            int max = progressBar.getMax();

            float xPosition = ((float) progress / max) * width;

            float finalX = xPosition - (tvLabel.getWidth() / 2f);

            if (finalX < 0) finalX = 0;
            if (finalX > width - tvLabel.getWidth()) finalX = width - tvLabel.getWidth();

            tvLabel.setTranslationX(finalX);
        });
    }
    private void setStatCard(View cardView, String value, String label) {
        if (cardView == null) return;
        TextView tvValue = cardView.findViewById(R.id.value);
        TextView tvLabel = cardView.findViewById(R.id.label);
        if (tvValue != null) tvValue.setText(value);
        if (tvLabel != null) tvLabel.setText(label);
    }
    private String formatLongDate(long timeInMs) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timeInMs));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}