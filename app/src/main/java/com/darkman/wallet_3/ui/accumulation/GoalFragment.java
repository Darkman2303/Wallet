package com.darkman.wallet_3.ui.accumulation;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.darkman.wallet_3.Balance;
import com.darkman.wallet_3.R;
import com.darkman.wallet_3.databinding.FragmentGoalBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.transition.MaterialSharedAxis;

import java.util.Objects;

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
            historyAdapter = new HistoryAdapter(balance);
            binding.historyList.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.historyList.setAdapter(historyAdapter);
            if (balance != null) {
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.accumulation_dialog, null);
        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        TextView dateText = view.findViewById(R.id.dateText);
        ImageButton datePickerBtn = view.findViewById(R.id.date_picker);
        TextInputEditText editText = view.findViewById(R.id.dialog_edittext);
        Button btnAdd = view.findViewById(R.id.dialog_addButton);
        Button btnRemove = view.findViewById(R.id.dialog_removeButton);
        AutoCompleteTextView dropdown = view.findViewById(R.id.my_autocomplete_textview);
        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.toggleGroup);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        dateText.setText(formatDate(
                calendar.get(java.util.Calendar.DAY_OF_MONTH),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.YEAR)
        ));

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                int arrayRes = (checkedId == R.id.btnIncome) ? R.array.income_categories : R.array.input_category;
                String[] items = getResources().getStringArray(arrayRes);

                String btText = (checkedId == R.id.btnIncome) ? getString(R.string.remove) : getString(R.string.add);
                btnAdd.setText(btText);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);
                dropdown.setAdapter(adapter);

                if (items.length > 0) {
                    dropdown.setText(items[0], false);
                }
            }
        });

        int initialArray = (toggleGroup.getCheckedButtonId() == R.id.btnIncome) ? R.array.income_categories : R.array.input_category;
        String[] initialItems = getResources().getStringArray(initialArray);
        dropdown.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, initialItems));
        if (initialItems.length > 0) {
            dropdown.setText(initialItems[0], false);
        }

        datePickerBtn.setOnClickListener(v -> {
            android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                    requireContext(),
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        dateText.setText(formatDate(selectedDay, selectedMonth, selectedYear));
                    },
                    calendar.get(java.util.Calendar.YEAR),
                    calendar.get(java.util.Calendar.MONTH),
                    calendar.get(java.util.Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        btnAdd.setOnClickListener(v -> {
            String amountStr = Objects.requireNonNull(editText.getText()).toString();
            String date = dateText.getText().toString();

            if (!amountStr.isEmpty()) {
                int amount = Integer.parseInt(amountStr);
                if (toggleGroup.getCheckedButtonId() != R.id.btnExpense) {
                    amount = -amount;
                }

                viewModel.addTransaction(amount, 0, date);
                bottomSheetDialog.dismiss();
            } else {
                editText.setError(getString(R.string.error_inner));
            }
        });

        btnRemove.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }
    private String formatDate(int day, int month, int year) {
        return String.format(java.util.Locale.getDefault(), "%02d.%02d.%04d", day, month + 1, year);
    }

    private void observeViewModel() {
        viewModel.balance.observe(getViewLifecycleOwner(), this::updateBalanceUI);

        viewModel.history.observe(getViewLifecycleOwner(), histories -> {
            if (histories != null) {
                historyAdapter.submitList(histories);
                // Управление кнопкой "Show more"
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

        int actualColor = ContextCompat.getColor(requireContext(), balance.colorResId);
        binding.circularGoalProgress.animateProgress(percent, actualColor);
        binding.goalDetailIcon.setImageResource(balance.iconResId);

        updateStats(balance);

    }
    @SuppressLint("SetTextI18n")
    private void updateStats(Balance balance) {
        long currentTime = System.currentTimeMillis();
        long totalMs = balance.targetDay - balance.basedDay;
        long passedMs = Math.max(0, currentTime - balance.basedDay);
        long remainingMs = Math.max(0, balance.targetDay - currentTime);

        long totalDays = Math.max(1, totalMs / 86400000L);
        long passedDays = passedMs / 86400000L;
        long remainingDays = remainingMs / 86400000L;

        double weeksTotal = Math.max(1.0, totalMs / 604800000.0);
        double weeksPassed = Math.max(0.1, passedMs / 604800000.0);
        double remainingWeeks = (double) remainingMs / 604800000.0;

        int remainingScore = (int) Math.max(0, balance.maxScore - balance.score);
        int progressPercent = (int) ((balance.score * 100.0) / balance.maxScore);

        int basePlanPerWeek = (int) (balance.maxScore / weeksTotal);
        int actualPerWeek = (int) (balance.score / weeksPassed);
        int realPlanPerWeek = (remainingWeeks > 0.05) ? (int) (remainingScore / remainingWeeks) : remainingScore;

        double timeProgressPercent = (totalMs > 0) ? (passedMs * 100.0 / totalMs) : 0;
        int performanceIndex = (timeProgressPercent > 0) ? (int) ((progressPercent / timeProgressPercent) * 100) : (balance.score > 0 ? 110 : 0);

        int safeTimeProgress = (int) Math.max(0, Math.min(100, timeProgressPercent));
        binding.determinateBar.setProgress(safeTimeProgress);
        binding.tvProgressLabel.setText(safeTimeProgress + "%");
        binding.determinateBar.post(() -> {
            int width = binding.determinateBar.getWidth();
            float xPosition = ((float) safeTimeProgress / 100) * width;
            float finalX = Math.max(0, Math.min(xPosition - (binding.tvProgressLabel.getWidth() / 2f), width - binding.tvProgressLabel.getWidth()));
            binding.tvProgressLabel.setTranslationX(finalX);
        });

        String performanceText;
        int statusColor;
        if (performanceIndex >= 100) {
            performanceText = getString(R.string.ahead) + " " + performanceIndex + "%";
            statusColor = Color.parseColor("#4CAF50");
        } else if (performanceIndex >= 80) {
            performanceText = getString(R.string.on_track)+" " + performanceIndex + "%";
            statusColor = Color.parseColor("#FFC107");
        } else {
            performanceText = getString(R.string.behind)+" " + performanceIndex + "%";
            statusColor = Color.parseColor("#F44336");
        }

        double daysFromStart = Math.max(0.1, (double) passedMs / 86400000.0);
        double avgScorePerDay = balance.score / daysFromStart;
        int idealScoreNow = (int) (balance.maxScore * (timeProgressPercent / 100.0));
        int debt = idealScoreNow - (int) balance.score;

        String statusDescription;
        if (debt > 0) {
            statusDescription = getString(R.string.need_to_catch_up)+": +" + debt + " " + getString(R.string.right_now);
        } else if (avgScorePerDay > 0) {
            int forecastDaysToFinish = (int) (remainingScore / avgScorePerDay);
            long daysEarly = (balance.targetDay - (currentTime + (forecastDaysToFinish * 86400000L))) / 86400000L;
            statusDescription = (daysEarly > 0) ? getString(R.string.analyse_finish_day)+" " + daysEarly + " "+ getString(R.string.days_early)
                    : getString(R.string.finishing_on_time);
        } else {
            statusDescription = getString(R.string.f_on_time);
        }

        setStatCard(binding.statScore.getRoot(), balance.score + " / " + balance.maxScore, getString(R.string.goal_score));
        setStatCard(binding.statRemains.getRoot(), String.valueOf(remainingScore), getString(R.string.goal_remains));
        setStatCard(binding.statDaily.getRoot(), realPlanPerWeek + " / " + basePlanPerWeek, getString(R.string.stat_plan));
        setStatCard(binding.statPercent.getRoot(), performanceText, getString(R.string.performance));
        setStatCard(binding.statState.getRoot(), statusDescription, getString(R.string.goal_status));
        setStatCard(binding.statStart.getRoot(), formatLongDate(balance.basedDay), getString(R.string.start));
        setStatCard(binding.statEnd.getRoot(), formatLongDate(balance.targetDay), getString(R.string.end));
        setStatCard(binding.statLeftDays.getRoot(), remainingDays + " / " + totalDays, getString(R.string.goal_days_left));
        setStatCard(binding.statPassedDays.getRoot(), String.valueOf(passedDays), getString(R.string.goal_days_passed));

        if (realPlanPerWeek > basePlanPerWeek * 1.2) {
            binding.statDaily.value.setTextColor(Color.RED);
        }
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