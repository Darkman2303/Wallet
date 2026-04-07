package com.darkman.wallet_3.ui.accumulation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.darkman.wallet_3.Balance;
import com.darkman.wallet_3.R;
import com.darkman.wallet_3.databinding.BalanceLayoutBinding;
import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder> {

    private final List<Balance> balanceList;
    private final OnBalanceClickListener listener;
    private final Context context;

    public interface OnBalanceClickListener {
        void onBalanceClick(Balance balance);
    }

    public BalanceAdapter(Context context, List<Balance> balanceList, OnBalanceClickListener listener) {
        this.context = context;
        this.balanceList = balanceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BalanceLayoutBinding binding = BalanceLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BalanceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        Balance balance = balanceList.get(position);
        holder.bind(balance, listener);
    }

    @Override
    public int getItemCount() {
        return balanceList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Balance> newBalances) {
        this.balanceList.clear();
        this.balanceList.addAll(newBalances);
        notifyDataSetChanged();
    }

    class BalanceViewHolder extends RecyclerView.ViewHolder {
        private final BalanceLayoutBinding binding;

        BalanceViewHolder(BalanceLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final Balance balance, final OnBalanceClickListener listener) {
            itemView.setOnClickListener(v -> listener.onBalanceClick(balance));

            // ЗАЩИТА: Если в базе 0, подставляем стандартный цвет/иконку
            int colorResId = (balance.colorResId != 0) ? balance.colorResId : android.R.color.darker_gray;
            int iconResId = (balance.iconResId != 0) ? balance.iconResId : android.R.drawable.ic_menu_help;

            float percent = 0;
            if (balance.maxScore != 0) {
                percent = (float) (balance.score * 100) / balance.maxScore;
            }

            String percentString = String.format("%.2f", percent);
            binding.balanceTitle.setText(balance.name);
            binding.balancePercent.setText(percentString + "%");

            // Используем нашу переменную colorResId вместо balance.colorResId
            int color = ContextCompat.getColor(context, colorResId);

            if (balance.maxScore == 0) {
                binding.balanceProgressBar.setVisibility(View.GONE);
                binding.balanceTitle.setText(balance.name + ":    " + (int)balance.score);
                binding.balanceScore.setVisibility(View.GONE);
                binding.balancePercent.setVisibility(View.GONE);
            } else {
                binding.balanceTitle.setText(balance.name);
                binding.balanceProgressBar.setVisibility(View.VISIBLE);
                binding.balanceProgressBar.setMax((int) balance.maxScore);
                binding.balanceProgressBar.setProgress((int) balance.score);
                binding.balanceScore.setText((int)balance.score + "/" + (int)balance.maxScore);
                binding.balanceProgressBar.setProgressTintList(ColorStateList.valueOf(color));
            }

            // Используем защищенные ресурсы
            binding.balanceImage.setImageResource(iconResId);
            binding.balanceImage.setBackgroundTintList(ColorStateList.valueOf(color));
        }
    }
}
