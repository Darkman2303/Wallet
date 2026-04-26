package com.darkman.wallet_3.ui.accumulation;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.darkman.wallet_3.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddTransaction extends BottomSheetDialogFragment {

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInflater localInflater = LayoutInflater.from(requireActivity());
        return localInflater.inflate(R.layout.accumulation_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация View
        TextView dateText = view.findViewById(R.id.dateText);
        ImageButton datePickerBtn = view.findViewById(R.id.date_picker);
        TextInputEditText editText = view.findViewById(R.id.dialog_edittext);
        Button btnAdd = view.findViewById(R.id.dialog_addButton);
        Button btnRemove = view.findViewById(R.id.dialog_removeButton);
        AutoCompleteTextView dropdown = view.findViewById(R.id.my_autocomplete_textview);
        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.toggleGroup);

        Calendar calendar = Calendar.getInstance();
        dateText.setText(formatDate(
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)
        ));

        // Логика выбора категории (доход/расход)
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                int arrayRes = (checkedId == R.id.btnIncome) ? R.array.income_categories : R.array.input_category;
                String[] items = getResources().getStringArray(arrayRes);

                String btText = (checkedId == R.id.btnIncome) ? getString(R.string.add) : getString(R.string.remove);
                btnAdd.setText(btText);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, items);
                dropdown.setAdapter(adapter);
                if (items.length > 0) dropdown.setText(items[0], false);
            }
        });

        // Начальная настройка выпадающего списка
        int initialArray = (toggleGroup.getCheckedButtonId() == R.id.btnIncome) ? R.array.income_categories : R.array.input_category;
        dropdown.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(initialArray)));

        // Выбор даты
        datePickerBtn.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), (datePicker, year, month, day) -> {
                dateText.setText(formatDate(day, month, year));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Кнопка добавить
        btnAdd.setOnClickListener(v -> {
            String amountStr = Objects.requireNonNull(editText.getText()).toString();
            if (!amountStr.isEmpty()) {
                int amount = Integer.parseInt(amountStr);
                if (toggleGroup.getCheckedButtonId() == R.id.btnExpense) {
                    amount = -amount;
                }

                // Передаем данные обратно во фрагмент
                Bundle result = new Bundle();
                result.putInt("amount", amount);
                result.putString("date", dateText.getText().toString());
                getParentFragmentManager().setFragmentResult("add_transaction", result);

                dismiss();
            } else {
                editText.setError(getString(R.string.error_inner));
            }
        });

        btnRemove.setOnClickListener(v -> dismiss());
    }

    private String formatDate(int day, int month, int year) {
        return String.format(Locale.getDefault(), "%02d.%02d.%04d", day, month + 1, year);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(d -> {
            BottomSheetDialog bsd = (BottomSheetDialog) d;
            View bottomSheet = bsd.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }
}
