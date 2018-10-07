package com.chargnn.service;

import com.chargnn.Main;
import com.chargnn.api.NGT;
import com.chargnn.utils.ListenerMap;

import java.util.UUID;

public class BalanceService {

    public static Main main;
    public static ListenerMap<UUID, Double> balance = new ListenerMap<>();

    public BalanceService(Main main){
        this.main = main;
    }

    public boolean hasBalance(UUID uuid){
        return balance.containsKey(uuid);
    }

    public void set(UUID uuid, double x){
        balance.put(uuid, x);
    }

    public void add(UUID uuid, double x){
        if(balance.getTransactCount() >= 5)
            NGT.saveBalances();

        balance.put(uuid, balance.get(uuid) + x);
    }

    public void clear(UUID uuid){
        balance.put(uuid, 0d);
    }

}
