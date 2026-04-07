package com.darkman.wallet_3.ui.accumulation;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.darkman.wallet_3.Balance;
import com.darkman.wallet_3.History;
import java.util.List;

public class GoalViewModel extends AndroidViewModel {

    private final DatabaseHelper dbHelper;
    private final int balanceId;

    private final MutableLiveData<Balance> _balance = new MutableLiveData<>();
    public final LiveData<Balance> balance = _balance;

    private final MutableLiveData<List<History>> _history = new MutableLiveData<>();
    public final LiveData<List<History>> history = _history;

    public GoalViewModel(Application application, int balanceId) {
        super(application);
        this.dbHelper = new DatabaseHelper(application);
        this.balanceId = balanceId;
        loadData();
    }

    public void loadData() {
        Balance b = dbHelper.getBalanceById(balanceId);
        if (b != null) {
            b.score = dbHelper.getSumForBalance(balanceId);
            _balance.setValue(b);
            _history.setValue(dbHelper.getHistoryForBalance(balanceId, b.categoryId));
        }
    }

    public void addTransaction(int value, int type, String date) {
        dbHelper.insertHistory(balanceId, type, value, date);
        loadData();
    }
}
