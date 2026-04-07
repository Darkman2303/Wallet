package com.darkman.wallet_3.ui.notifications;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import com.darkman.wallet_3.R;

public class ThemeSelectionDialogFragment extends DialogFragment {

    public static final String TAG = "ThemeSelectionDialogFragment";

    private static final String[] THEMES = {"System Default", "Light", "Dark"};
    private static final int[] THEME_MODES = {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_YES
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("save_data", Context.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.personalisation)
                .setSingleChoiceItems(THEMES, getCheckedItem(sharedPreferences), (dialog, which) -> {
                    int selectedMode = THEME_MODES[which];
                    AppCompatDelegate.setDefaultNightMode(selectedMode);
                    sharedPreferences.edit().putInt("theme_mode", selectedMode).apply();
                    dialog.dismiss();
                });
        return builder.create();
    }

    private int getCheckedItem(SharedPreferences sharedPreferences) {
        int currentMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        for (int i = 0; i < THEME_MODES.length; i++) {
            if (THEME_MODES[i] == currentMode) {
                return i;
            }
        }
        return 0;
    }
}
