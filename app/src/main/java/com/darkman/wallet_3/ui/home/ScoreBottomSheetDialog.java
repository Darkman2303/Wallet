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

public class ScoreBottomSheetDialog extends BottomSheetDialogFragment {

    public static final String TAG = "ScoreBottomSheetDialog";

    private HomeViewModel homeViewModel;
    private EditText scoreEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.set_score_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scoreEditText = view.findViewById(R.id.score_edit_text);
        Button addButton = view.findViewById(R.id.add_bt);
        Button removeButton = view.findViewById(R.id.remove_bt);
        Button setScoreButton = view.findViewById(R.id.set_bt);

        addButton.setOnClickListener(v -> {
            int value = getNumberFromEditText();
            if (value != 0) {
                homeViewModel.addScore(value);
                dismiss();
            }
        });

        removeButton.setOnClickListener(v -> {
            int value = getNumberFromEditText();
            if (value != 0) {
                homeViewModel.removeScore(value);
                dismiss();
            }
        });

        setScoreButton.setOnClickListener(v -> {
            int value = getNumberFromEditText();
            homeViewModel.setScore(value);
            dismiss();
        });
    }

    private int getNumberFromEditText() {
        String text = scoreEditText.getText().toString();
        if (text.isEmpty()) {
            Toast.makeText(requireContext(), R.string.number_hint, Toast.LENGTH_SHORT).show();
            return 0;
        } else {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), R.string.number_format_error, Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
    }
}
