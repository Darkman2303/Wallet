package com.darkman.wallet_3;

import java.io.Serializable;

public class Balance implements Serializable {
    public int id;
    public String name;
    public int score;
    public int maxScore;
    public long basedDay;
    public long targetDay;
    public int categoryId;

    public int iconResId;
    public int colorResId;

    public Balance(int id, String name, int score, int maxScore, long basedDay, long targetDay, int categoryId, int iconResId, int colorResId) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.maxScore = maxScore;
        this.basedDay = basedDay;
        this.targetDay = targetDay;
        this.categoryId = categoryId;
        this.iconResId = iconResId;
        this.colorResId = colorResId;
    }
    public int getProgressPercent() {
        if (maxScore <= 0) return 0;
        return (int) (((double) score / maxScore) * 100);
    }
}
