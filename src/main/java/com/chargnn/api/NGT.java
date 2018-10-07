package com.chargnn.api;

import com.chargnn.Main;
import com.chargnn.service.BalanceService;

import java.util.UUID;

public class NGT {

    private static Main main = BalanceService.main;

    public static void saveBalances(){
        for(UUID uuid : BalanceService.balance.keySet()){
            main.getConfig().set("balance." + uuid, BalanceService.balance.get(uuid));
        }

        main.saveConfig();
    }

    public static void loadBalances(BalanceService service){
        if(!main.getConfig().contains("balance")) return;

        for(String uuid : main.getConfig().getConfigurationSection("balance").getKeys(false)){
            service.set(UUID.fromString(uuid), main.getConfig().getDouble("balance." + UUID.fromString(uuid)));
        }
    }
}
