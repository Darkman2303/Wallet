package com.darkman.wallet_3.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.darkman.wallet_3.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditGoalBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = "EditGoalBottomSheetDialogFragment";

    private HomeViewModel homeViewModel;
    private EditText goalEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goalEditText = view.findViewById(R.id.goal_edittext);

        homeViewModel.goal.observe(getViewLifecycleOwner(), goal -> {
            if (goal != null) {
                goalEditText.setText(String.valueOf(goal));
            }
        });

        view.findViewById(R.id.bt_10k).setOnClickListener(v -> goalEditText.setText("10000"));
        view.findViewById(R.id.bt_30k).setOnClickListener(v -> goalEditText.setText("30000"));
        view.findViewById(R.id.bt_50k).setOnClickListener(v -> goalEditText.setText("50000"));
        view.findViewById(R.id.bt_80k).setOnClickListener(v -> goalEditText.setText("80000"));
        view.findViewById(R.id.bt_100k).setOnClickListener(v -> goalEditText.setText("100000"));

        view.findViewById(R.id.save_button).setOnClickListener(v -> {
            String goalStr = goalEditText.getText().toString();
            if (!goalStr.isEmpty()) {
                try {
                    int goal = Integer.parseInt(goalStr);
                    homeViewModel.setGoal(goal);
                    dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), R.string.number_format_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), R.string.error_empty_field, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
