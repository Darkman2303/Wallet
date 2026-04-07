package com.darkman.wallet_3;

import java.io.Serializable;

public class History implements Serializable {
    public final int id;
    public final int balanceId;
    public final int categoryId;
    public final int value;
    public final String data;
    public final int balanceCategoryId;

    public History(int id, int balanceId, int categoryId, int value, String data, int balanceCategoryId) {
        this.id = id;
        this.balanceId = balanceId;
        this.categoryId = categoryId;
        this.value = value;
        this.data = data;
        this.balanceCategoryId = balanceCategoryId;
    }
}
