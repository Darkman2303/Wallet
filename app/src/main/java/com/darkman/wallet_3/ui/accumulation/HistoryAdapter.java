package com.darkman.wallet_3.ui.accumulation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.darkman.wallet_3.Balance;
import com.darkman.wallet_3.History;
import com.darkman.wallet_3.R;
import com.darkman.wallet_3.databinding.HistoryUccumulationBinding;

public class HistoryAdapter extends androidx.recyclerview.widget.ListAdapter<History, HistoryAdapter.ViewHolder> {

    private boolean isExpanded = false;
    private static final int MAX_COLLAPSED_ITEMS = 5;
    private final Balance currentBalance;

    public HistoryAdapter(Balance balance) {
        super(new DiffUtil.ItemCallback<History>() {
            @Override
            public boolean areItemsTheSame(@NonNull History oldItem, @NonNull History newItem) {
                return oldItem.id == newItem.id;
            }
            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull History oldItem, @NonNull History newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.currentBalance = balance;
    }

    public void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int actualCount = getCurrentList().size();
        if (isExpanded) {
            return actualCount;
        } else {
            return Math.min(actualCount, MAX_COLLAPSED_ITEMS);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), currentBalance);

        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(50f);
        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HistoryUccumulationBinding binding = HistoryUccumulationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final HistoryUccumulationBinding binding;
        private final Context context;

        public ViewHolder(HistoryUccumulationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = binding.getRoot().getContext();
        }

        public void bind(History history, Balance balance) {
            int arrayRes = (history.value > 0) ? R.array.income_categories : R.array.input_category;
            String[] transactionNames = context.getResources().getStringArray(arrayRes);

            if (history.categoryId >= 0 && history.categoryId < transactionNames.length) {
                binding.CategoryText.setText(transactionNames[history.categoryId]);
            }

            if (balance != null) {
                binding.BalanceTitle.setText(balance.name);
                binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, balance.colorResId));
            }

            int color = (history.value > 0) ? context.getColor(R.color.green_1) : context.getColor(R.color.red);
            String sign = history.value > 0 ? "+" : "";

            binding.valueText.setText(String.format("%s%d", sign, history.value));
            binding.valueText.setTextColor(color);
            binding.dateText.setText(history.data);
        }
    }
}