package com.darkman.wallet_3.ui.notifications;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.DialogFragment;
import com.darkman.wallet_3.R;

public class LanguageSelectionDialogFragment extends DialogFragment {

    public static final String TAG = "LanguageSelectionDialogFragment";

    private static final String[] LANGUAGES = {"English", "Русский"};
    private static final String[] LANGUAGE_CODES = {"en", "ru"};

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.languages)
                .setItems(LANGUAGES, (dialog, which) -> {
                    String selectedLangCode = LANGUAGE_CODES[which];
                    LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(selectedLangCode);
                    AppCompatDelegate.setApplicationLocales(appLocale);
                });
        return builder.create();
    }
}
