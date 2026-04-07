
package com.darkman.wallet_3.ui.accumulation;

import static android.widget.Toast.LENGTH_LONG;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import com.darkman.wallet_3.R;
import com.darkman.wallet_3.databinding.FragmentBalanceEditorBinding;
import com.darkman.wallet_3.Balance;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BalanceEditorFragment extends BottomSheetDialogFragment {

    public static final String TAG = "BalanceEditorFragment";

    private FragmentBalanceEditorBinding binding;
    private AccuViewModel viewModel;

    private int selectedIconResId = R.drawable.ic_accumulation;
    private int selectedColorResId = R.color.acc_growth_green;
    private long basedDayTimestamp = System.currentTimeMillis();
    private long targetDayTimestamp = 0;

    private List<ImageButton> iconButtons;

    public static BalanceEditorFragment newInstance(@Nullable Balance balance) {
        BalanceEditorFragment fragment = new BalanceEditorFragment();
        Bundle args = new Bundle();
        if (balance != null) {
            args.putInt("id", balance.id);
            args.putString("name", balance.name);
            args.putInt("max", balance.maxScore);
            args.putInt("icon", balance.iconResId);
            args.putInt("color", balance.colorResId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBalanceEditorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AccuViewModel.class);
        Calendar calendar = Calendar.getInstance();

        // 1. Дата начала (Сегодня)
        basedDayTimestamp = calendar.getTimeInMillis();
        String todayStr = formatDate(calendar);
        binding.basedDayText.setText(todayStr);

        // 2. Целевая дата (Сегодня + 1 месяц)
        calendar.add(Calendar.MONTH, 1);
        targetDayTimestamp = calendar.getTimeInMillis();
        String nextMonthStr = formatDate(calendar);
        binding.dateText.setText(nextMonthStr);
        // ---------------------------------------

        // Установка заголовка (Создание vs Редактирование)
        if (getArguments() != null && getArguments().containsKey("id")) {
            binding.fragmentTitle.setText(R.string.edit_balance);

            // Перезаписываем даты, если мы в режиме редактирования
            // (здесь должна быть логика получения дат из аргументов, если ты их туда передаешь)

            binding.textInputEditText.setText(getArguments().getString("name"));
            binding.InputMaxscore.setText(String.valueOf(getArguments().getInt("max")));
            selectedIconResId = getArguments().getInt("icon");
            selectedColorResId = getArguments().getInt("color");
        } else {
            binding.fragmentTitle.setText(R.string.create_balance);
        }


        iconButtons = Arrays.asList(
                binding.imageButtonCar, binding.imageButtonEvent,
                binding.imageButtonHoliday, binding.imageButtonWallet,
                binding.imageButtonSmartphone, binding.imageButtonLaptop,
                binding.imageButtonEducation, binding.imageButtonShield
        );

        if (getArguments() != null && getArguments().containsKey("id")) {
            binding.textInputEditText.setText(getArguments().getString("name"));
            binding.InputMaxscore.setText(String.valueOf(getArguments().getInt("max")));
            selectedIconResId = getArguments().getInt("icon");
            selectedColorResId = getArguments().getInt("color");
        }

        // Слушатели для кнопок цветов
        binding.cGrowthGreenBtn.setOnClickListener(v -> changeGlobalColor(R.color.acc_growth_green));
        binding.cTrustBlueBtn.setOnClickListener(v -> changeGlobalColor(R.color.acc_trust_blue));
        binding.cSmartPurpleBtn.setOnClickListener(v -> changeGlobalColor(R.color.acc_smart_purple));
        binding.cEnergyOrangeBtn.setOnClickListener(v -> changeGlobalColor(R.color.acc_energy_orange));
        binding.cBerryFuchsiaBtn.setOnClickListener(v -> changeGlobalColor(R.color.acc_berry_fuchsia));
        binding.cFreshTealBtn.setOnClickListener(v -> changeGlobalColor(R.color.acc_fresh_teal));
        binding.cRoyalGoldBtn.setOnClickListener(v -> changeGlobalColor(R.color.acc_royal_gold));
        binding.cRoseDesireBtn.setOnClickListener(v -> changeGlobalColor(R.color.acc_rose_desire));
        binding.cRoyalPurpleBtn.setOnClickListener(v -> changeGlobalColor(R.color.acc_royal_purple));
        binding.cSlateGrayBtn.setOnClickListener(v -> changeGlobalColor(R.color.acc_slate_gray));
        binding.cSoftCoralBtn.setOnClickListener(v -> changeGlobalColor(R.color.acc_soft_coral));

        setupIconClicks();

        binding.basedDayPicker.setOnClickListener(v -> showDatePicker(true));
        binding.datePicker.setOnClickListener(v -> showDatePicker(false));

        binding.doneButton.setOnClickListener(v -> save());


        changeGlobalColor(selectedColorResId);
    }

    private void setupIconClicks() {
        binding.imageButtonCar.setOnClickListener(v -> selectIcon(R.drawable.ic_car));
        binding.imageButtonEvent.setOnClickListener(v -> selectIcon(R.drawable.ic_event));
        binding.imageButtonHoliday.setOnClickListener(v -> selectIcon(R.drawable.ic_holiday));
        binding.imageButtonWallet.setOnClickListener(v -> selectIcon(R.drawable.ic_accumulation));
        binding.imageButtonSmartphone.setOnClickListener(v -> selectIcon(R.drawable.ic_smartphone));
        binding.imageButtonLaptop.setOnClickListener(v -> selectIcon(R.drawable.ic_laptop));
        binding.imageButtonEducation.setOnClickListener(v -> selectIcon(R.drawable.ic_education));
        binding.imageButtonShield.setOnClickListener(v -> selectIcon(R.drawable.ic_shield));
    }

    private void selectIcon(int resId) {
        selectedIconResId = resId;
        refreshIconsDisplay();
    }

    private void changeGlobalColor(int colorRes) {
        selectedColorResId = colorRes;

        int nameResId;
        if (colorRes == R.color.acc_growth_green) nameResId = R.string.color_growth_green;
        else if (colorRes == R.color.acc_trust_blue) nameResId = R.string.color_trust_blue;
        else if (colorRes == R.color.acc_smart_purple) nameResId = R.string.color_smart_purple;
        else if (colorRes == R.color.acc_energy_orange) nameResId = R.string.color_energy_orange;
        else if (colorRes == R.color.acc_berry_fuchsia) nameResId = R.string.color_berry_fuchsia;
        else if (colorRes == R.color.acc_fresh_teal) nameResId = R.string.color_fresh_teal;
        else if (colorRes == R.color.acc_royal_gold) nameResId = R.string.color_royal_gold;
        else if (colorRes == R.color.acc_rose_desire) nameResId = R.string.color_rose_desire;
        else if (colorRes == R.color.acc_royal_purple) nameResId = R.string.color_purple;
        else if (colorRes == R.color.acc_slate_gray) nameResId = R.string.color_slate;
        else if (colorRes == R.color.acc_soft_coral) nameResId = R.string.color_coral;
        else nameResId = R.string.none;

        binding.selectedColorName.setText(getString(R.string.selected_color_prefix, getString(nameResId)));

        refreshColorButtons();
        refreshIconsDisplay();
    }

    private void refreshColorButtons() {
        // Создаем карту соответствия кнопки и ресурса цвета для точности
        Map<MaterialButton, Integer> colorMap = new HashMap<>();
        colorMap.put(binding.cGrowthGreenBtn, R.color.acc_growth_green);
        colorMap.put(binding.cTrustBlueBtn, R.color.acc_trust_blue);
        colorMap.put(binding.cSmartPurpleBtn, R.color.acc_smart_purple);
        colorMap.put(binding.cEnergyOrangeBtn, R.color.acc_energy_orange);
        colorMap.put(binding.cBerryFuchsiaBtn, R.color.acc_berry_fuchsia);
        colorMap.put(binding.cFreshTealBtn, R.color.acc_fresh_teal);
        colorMap.put(binding.cRoyalGoldBtn, R.color.acc_royal_gold);
        colorMap.put(binding.cRoseDesireBtn, R.color.acc_rose_desire);
        colorMap.put(binding.cRoyalPurpleBtn, R.color.acc_royal_purple);
        colorMap.put(binding.cSlateGrayBtn, R.color.acc_slate_gray);
        colorMap.put(binding.cSoftCoralBtn, R.color.acc_soft_coral);

        for (Map.Entry<MaterialButton, Integer> entry : colorMap.entrySet()) {
            MaterialButton btn = entry.getKey();
            int colorRes = entry.getValue();

            if (colorRes == selectedColorResId) {
                btn.setCornerRadius(100); // Идеальный круг
                btn.setStrokeWidth(6);    // Обводка
                btn.setStrokeColor(ColorStateList.valueOf(Color.LTGRAY));
            } else {
                btn.setCornerRadius(20);  // Скругленный квадрат
                btn.setStrokeWidth(0);
            }
        }
    }

    private void refreshIconsDisplay() {
        int activeColor = ContextCompat.getColor(requireContext(), selectedColorResId);
        int inactiveColor = ContextCompat.getColor(requireContext(), R.color.gray_progress);

        for (ImageButton btn : iconButtons) {
            boolean isSelected = isButtonSelected(btn);

            if (isSelected) {
                btn.getBackground().setColorFilter(activeColor, PorterDuff.Mode.SRC_IN);
                btn.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            } else {
                btn.getBackground().setColorFilter(inactiveColor, PorterDuff.Mode.SRC_IN);
                btn.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private boolean isButtonSelected(ImageButton btn) {
        int id = btn.getId();
        if (id == R.id.imageButtonCar && selectedIconResId == R.drawable.ic_car) return true;
        if (id == R.id.imageButtonEvent && selectedIconResId == R.drawable.ic_event) return true;
        if (id == R.id.imageButtonHoliday && selectedIconResId == R.drawable.ic_holiday) return true;
        if (id == R.id.imageButtonWallet && selectedIconResId == R.drawable.ic_accumulation) return true;
        if (id == R.id.imageButtonSmartphone && selectedIconResId == R.drawable.ic_smartphone) return true;
        if (id == R.id.imageButtonLaptop && selectedIconResId == R.drawable.ic_laptop) return true;
        if (id == R.id.imageButtonEducation && selectedIconResId == R.drawable.ic_education) return true;
        return id == R.id.imageButtonShield && selectedIconResId == R.drawable.ic_shield;
    }

    private void showDatePicker(boolean isBasedDay) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            String dateStr = dayOfMonth + "/" + (month + 1) + "/" + year;

            if (isBasedDay) {
                basedDayTimestamp = selected.getTimeInMillis();
                binding.basedDayText.setText(dateStr);
            } else {
                targetDayTimestamp = selected.getTimeInMillis();
                binding.dateText.setText(dateStr);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void save() {
        String name = Objects.requireNonNull(binding.textInputEditText.getText()).toString().trim();
        String maxStr = Objects.requireNonNull(binding.InputMaxscore.getText()).toString().trim();

        boolean hasError = false;

        if (name.isEmpty()) {
            binding.textInputLayout.setError(getString(R.string.error_empty_field));
            hasError = true;
        } else {
            binding.textInputLayout.setError(null);
        }

        if (maxStr.isEmpty() || Double.parseDouble(maxStr) <= 0) {
            binding.InputMaxscoreLayout.setError(getString(R.string.number_format_error));
            hasError = true;
        } else {
            binding.InputMaxscoreLayout.setError(null);
        }

        if (hasError) return;

        int max = Integer.parseInt(maxStr);
        viewModel.addBalance(name, 0, max, basedDayTimestamp, targetDayTimestamp, 0, selectedIconResId, selectedColorResId);
        dismiss();
    }
    private String formatDate(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return day + "." + month + "." + year;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}