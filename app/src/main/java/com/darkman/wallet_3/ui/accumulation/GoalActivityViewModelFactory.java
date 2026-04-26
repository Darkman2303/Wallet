package com.darkman.wallet_3.ui.accumulation;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class GoalActivityViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final int balanceId;

    public GoalActivityViewModelFactory(Application application, int balanceId) {
        this.application = application;
        this.balanceId = balanceId;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GoalViewModel.class)) {
            return (T) new GoalViewModel(application, balanceId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
