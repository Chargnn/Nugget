package com.chargnn.model;

import com.chargnn.utils.file.ConfigManager;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Balance {
    private int fracDigit;
    private double amount;

    public Balance(double amount) {
        this.amount = amount;
        this.fracDigit = ConfigManager.getFractionalDigits();
        this.amount = round(amount, fracDigit);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        amount = round(amount, fracDigit);
        this.amount = amount;
    }
}
