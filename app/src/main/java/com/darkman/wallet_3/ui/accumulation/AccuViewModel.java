package com.darkman.wallet_3.ui.accumulation;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class AccuViewModel extends AndroidViewModel {
    private final DatabaseHelper dbHelper;
    private final MutableLiveData<List<Balance>> _balances = new MutableLiveData<>();
    public final LiveData<List<Balance>> balances = _balances;

    private final MutableLiveData<Long> _totalSum = new MutableLiveData<>();
    public final LiveData<Long> totalSum = _totalSum;

    public AccuViewModel(Application application) {
        super(application);
        dbHelper = new DatabaseHelper(application);
        loadBalances();
    }

    public void loadBalances() {
        new Thread(() -> {
            List<Balance> balanceList = dbHelper.getAllBalances();
            long total = 0;
            for (Balance balance : balanceList) {
                balance.score = dbHelper.getSumForBalance(balance.id);
                total += balance.score;
            }
            _balances.postValue(balanceList);
            _totalSum.postValue(total);
        }).start();
    }

    public void addBalance(String name, int score, int maxScore, long basedDay, long targetDay, int categoryId, int iconResId, int colorResId) {
        new Thread(() -> {
            dbHelper.insertBalance(name, score, maxScore, basedDay, targetDay, categoryId, iconResId, colorResId);
            loadBalances();
        }).start();
    }

    public void updateBalance(Balance balance) {
        new Thread(() -> {
            dbHelper.updateBalance(balance);
            loadBalances();
        }).start();
    }

    public void upsertBalance(Balance balance) {
        new Thread(() -> {
            if (dbHelper.getBalanceById(balance.id) != null) {
                dbHelper.updateBalance(balance);
            } else {
                dbHelper.insertBalance(balance.name, balance.score, balance.maxScore, balance.basedDay, balance.targetDay, balance.categoryId, balance.iconResId, balance.colorResId);
            }
            loadBalances();
        }).start();
    }
}
