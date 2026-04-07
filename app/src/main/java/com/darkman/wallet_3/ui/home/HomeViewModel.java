package com.darkman.wallet_3.ui.home;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class HomeViewModel extends AndroidViewModel {

    private static final String HISTORY_PREFS = "history";
    private static final String SCORE_KEY = "score";
    private static final String GOAL_KEY = "goal";
    private static final String ADVICE_COUNT_KEY = "strCount";
    private static final int DEFAULT_GOAL = 30000;

    private final SharedPreferences sharedPreferences;

    private final MutableLiveData<Integer> _score = new MutableLiveData<>(0);
    public final LiveData<Integer> score = _score;

    private final MutableLiveData<Integer> _goal = new MutableLiveData<>(DEFAULT_GOAL);
    public final LiveData<Integer> goal = _goal;

    private final MutableLiveData<String> _advice = new MutableLiveData<>();
    public final LiveData<String> advice = _advice;

    private final MutableLiveData<Event<Notification>> _notification = new MutableLiveData<>();
    public final LiveData<Event<Notification>> notification = _notification;

    private final String[] advices;

    public HomeViewModel(Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(HISTORY_PREFS, Context.MODE_PRIVATE);
        advices = new String[]{
                // TODO: Replace with your actual string resources
                "Prove yourself and rise",
                "Higher and higher you chase it",
                "I tried so hard ang got so far"
        };
        loadData();
    }

    private void loadData() {
        _score.setValue(sharedPreferences.getInt(SCORE_KEY, 0));
        _goal.setValue(sharedPreferences.getInt(GOAL_KEY, DEFAULT_GOAL));
        updateAdvice();
    }

    private void updateAdvice() {
        int count = sharedPreferences.getInt(ADVICE_COUNT_KEY, 0);
        _advice.setValue(advices[count]);
        count = (count + 1) % advices.length;
        sharedPreferences.edit().putInt(ADVICE_COUNT_KEY, count).apply();
    }

    public void addScore(int value) {
        int newScore = (_score.getValue() != null ? _score.getValue() : 0) + value;
        _score.setValue(newScore);
        sharedPreferences.edit().putInt(SCORE_KEY, newScore).apply();
        _notification.setValue(new Event<>(new Notification(true, value)));
    }

    public void removeScore(int value) {
        int newScore = (_score.getValue() != null ? _score.getValue() : 0) - value;
        _score.setValue(newScore);
        sharedPreferences.edit().putInt(SCORE_KEY, newScore).apply();
        _notification.setValue(new Event<>(new Notification(false, value)));
    }

    public void setScore(int value) {
        _score.setValue(value);
        sharedPreferences.edit().putInt(SCORE_KEY, value).apply();
    }

    public void setGoal(int newGoal) {
        _goal.setValue(newGoal);
        sharedPreferences.edit().putInt(GOAL_KEY, newGoal).apply();
    }

    // Helper class for events
    public static class Event<T> {
        private final T content;
        private boolean hasBeenHandled = false;

        public Event(T content) {
            this.content = content;
        }

        public T getContentIfNotHandled() {
            if (hasBeenHandled) {
                return null;
            } else {
                hasBeenHandled = true;
                return content;
            }
        }

        public T peekContent() {
            return content;
        }
    }

    public static class Notification {
        public final boolean isAddition;
        public final int amount;

        public Notification(boolean isAddition, int amount) {
            this.isAddition = isAddition;
            this.amount = amount;
        }
    }
}
