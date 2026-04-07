package com.darkman.wallet_3.ui.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.darkman.wallet_3.About_app;
import com.darkman.wallet_3.R;
import com.darkman.wallet_3.databinding.FragmentSettingBinding;
import com.darkman.wallet_3.ui.testing.Testing;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);

        binding.aboutApp.setOnClickListener(v -> moveActivity(About_app.class));

        binding.feedback.setOnClickListener(v -> sendFeedbackEmail());
        binding.languages.setOnClickListener(v -> showLanguageSelectionDialog());
        binding.personalisation.setOnClickListener(v -> showThemeSelectionDialog());
        binding.test.setOnClickListener(v -> moveActivity(Testing.class));

        binding.synch.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Synchronization will be implemented here.", Toast.LENGTH_SHORT).show();
        });

        // The Edit_score activity has been replaced by a dialog in HomeFragment
        binding.editScore.setVisibility(View.GONE);

        return binding.getRoot();
    }

    private void moveActivity(Class<?> activity) {
        Intent intent = new Intent(requireContext(), activity);
        startActivity(intent);
    }

    private void sendFeedbackEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developer_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for " + getString(R.string.app_name));
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showLanguageSelectionDialog() {
        LanguageSelectionDialogFragment dialog = new LanguageSelectionDialogFragment();
        dialog.show(getChildFragmentManager(), LanguageSelectionDialogFragment.TAG);
    }

    private void showThemeSelectionDialog() {
        ThemeSelectionDialogFragment dialog = new ThemeSelectionDialogFragment();
        dialog.show(getChildFragmentManager(), ThemeSelectionDialogFragment.TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
