package com.darkman.wallet_3.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.darkman.wallet_3.R;
import com.darkman.wallet_3.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    private TextView advice, progressText, scoreText, leftScoreText, maxText;
    private ProgressBar progressBar;
    private View warning_container;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        observeViewModel();
    }

    private void setupUI() {
        advice = binding.adviceText;
        progressText = binding.ProgressText;
        scoreText = binding.scoreText;
        leftScoreText = binding.leftScoreText;
        progressBar = binding.progressbar;
        maxText = binding.maxScoreText;
        warning_container = binding.warningContainer;

        binding.fab.setOnClickListener(v -> showScoreDialog());
        binding.warningCloseBt.setOnClickListener(v -> warning_container.setVisibility(View.GONE));
        binding.moveToEditScore.setOnClickListener(v -> showEditGoalDialog());
    }

    private void observeViewModel() {
        homeViewModel.score.observe(getViewLifecycleOwner(), this::updateScoreUI);
        homeViewModel.goal.observe(getViewLifecycleOwner(), this::updateGoalUI);
        homeViewModel.advice.observe(getViewLifecycleOwner(), advice::setText);
        homeViewModel.notification.observe(getViewLifecycleOwner(), event -> {
            HomeViewModel.Notification notification = event.getContentIfNotHandled();
            if (notification != null) {
                showNotification(notification.isAddition, notification.amount);
            }
        });
    }

    private void updateScoreUI(Integer score) {
        if (score == null) return;
        Integer goal = homeViewModel.goal.getValue();
        if (goal == null) goal = 0;

        scoreText.setText(getString(R.string.score_text) + ": " + score);
        progressBar.setProgress(score);

        if (score >= goal) {
            progressText.setText("100%");
            leftScoreText.setText(getString(R.string.left_score_text) + ": " + 0);
            warning_container.setVisibility(View.VISIBLE);
        } else {
            float percent = (float) (score * 100) / goal;
            leftScoreText.setText(getString(R.string.left_score_text) + ": " + (goal - score));
            progressText.setText(String.format("%.1f%%", percent));
            warning_container.setVisibility(View.GONE);
        }
    }

    private void updateGoalUI(Integer goal) {
        if (goal == null) return;
        maxText.setText(getString(R.string.max_score_text) + ": " + goal);
        progressBar.setMax(goal);
        updateScoreUI(homeViewModel.score.getValue());
    }

    private void showNotification(boolean isAddition, int amount) {
        String message = isAddition
                ? String.format("%s +%d", getString(R.string.add), amount)
                : String.format("%s -%d", getString(R.string.remove), amount);

        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    private void showScoreDialog() {
        ScoreBottomSheetDialog dialog = new ScoreBottomSheetDialog();
        dialog.show(getChildFragmentManager(), ScoreBottomSheetDialog.TAG);
    }

    private void showEditGoalDialog() {
        EditGoalBottomSheetDialogFragment dialog = new EditGoalBottomSheetDialogFragment();
        dialog.show(getChildFragmentManager(), EditGoalBottomSheetDialogFragment.TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
