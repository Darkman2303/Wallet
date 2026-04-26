package com.darkman.wallet_3.ui.accumulation;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.darkman.wallet_3.R;

import java.util.List;


public class GoalViewModel extends AndroidViewModel {

    public static class GoalStatsState {
        String performanceText;
        String statusDescription;
        int remainingScore;
        int realPlanPerWeek;
        int basePlanPerWeek;
        int remainingDays;
        int totalDays;
        int passedDays;
        int safeTimeProgress;

        public GoalStatsState(String performanceText, String statusDescription, int remainingScore,
                              int realPlanPerWeek, int basePlanPerWeek, int remainingDays, int totalDays, int passedDays,
                              int safeTimeProgress) {
            this.performanceText = performanceText;
            this.statusDescription = statusDescription;
            this.remainingScore = remainingScore;
            this.realPlanPerWeek = realPlanPerWeek;
            this.basePlanPerWeek = basePlanPerWeek;
            this.remainingDays = remainingDays;
            this.totalDays = totalDays;
            this.passedDays = passedDays;
            this.safeTimeProgress = safeTimeProgress;
        }
    }

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
    public final LiveData<GoalStatsState> statsState = Transformations.map(balance, b -> {
        Balance balance = _balance.getValue();
        if (balance == null) return null;
        long currentTime = System.currentTimeMillis();
        long totalMs = balance.targetDay - balance.basedDay;
        long passedMs = Math.max(0, currentTime - balance.basedDay);
        long remainingMs = Math.max(0, balance.targetDay - currentTime);

        double weeks_const = 604800000.0;
        long days_const = 86400000L;

        long totalDays = Math.max(1, totalMs / days_const);
        long passedDays = passedMs / days_const;
        long remainingDays = remainingMs / days_const;

        double weeksTotal = Math.max(1.0, totalMs / weeks_const);
        double remainingWeeks = (double) remainingMs / weeks_const;

        int remainingScore = (int) Math.max(0, balance.maxScore - balance.score);
        int progressPercent = (int) ((balance.score * 100.0) / balance.maxScore);

        int basePlanPerWeek = (int) (balance.maxScore / weeksTotal);
        int realPlanPerWeek = (remainingWeeks > 0.05) ? (int) (remainingScore / remainingWeeks) : remainingScore;

        double timeProgressPercent = (totalMs > 0) ? (passedMs * 100.0 / totalMs) : 0;
        int performanceIndex = (timeProgressPercent > 0) ? (int) ((progressPercent / timeProgressPercent) * 100) : (balance.score > 0 ? 110 : 0);

        int safeTimeProgress = (int) Math.max(0, Math.min(100, timeProgressPercent));


        String performanceText;
        Context context = getApplication().getApplicationContext();
        if (performanceIndex >= 100) {
            performanceText = context.getString(R.string.ahead) + " " + performanceIndex + "%";
        } else if (performanceIndex >= 80) {
            performanceText = context.getString(R.string.on_track)+" " + performanceIndex + "%";
        } else {
            performanceText = context.getString(R.string.behind)+" " + performanceIndex + "%";
        }

        double daysFromStart = Math.max(0.1, (double) passedMs / 86400000.0);
        double avgScorePerDay = balance.score / daysFromStart;
        int idealScoreNow = (int) (balance.maxScore * (timeProgressPercent / 100.0));
        int debt = idealScoreNow - (int) balance.score;

        String statusDescription;
        if (debt > 0) {
            statusDescription = context.getString(R.string.need_to_catch_up)+": +" + debt + " " + context.getString(R.string.right_now);
        } else if (avgScorePerDay > 0) {
            int forecastDaysToFinish = (int) (remainingScore / avgScorePerDay);
            long daysEarly = (balance.targetDay - (currentTime + (forecastDaysToFinish * 86400000L))) / 86400000L;
            statusDescription = (daysEarly > 0) ? context.getString(R.string.analyse_finish_day)+" " + daysEarly + " "+ context.getString(R.string.days_early)
                    : context.getString(R.string.finishing_on_time);
        } else {
            statusDescription = context.getString(R.string.f_on_time);
        }
        return new GoalStatsState(performanceText, statusDescription, remainingScore, realPlanPerWeek, basePlanPerWeek,
                (int) remainingDays, (int) totalDays, (int) passedDays, safeTimeProgress);
    });

    public void addTransaction(int value, int type, String date) {
        dbHelper.insertHistory(balanceId, type, value, date);
        loadData();
    }
}
