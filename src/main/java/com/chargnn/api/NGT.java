package com.chargnn.api;

import com.chargnn.Main;
import com.chargnn.service.BalanceService;
import net.milkbowl.vault.economy.Economy;

import java.util.UUID;

public class NGT {

    private static Main main = BalanceService.main;

    public static void saveBalances(){
        for(UUID uuid : BalanceService.balance.keySet()){
            main.getConfig().set("balance." + uuid, BalanceService.balance.get(uuid));
        }

        main.saveConfig();
    }

    public static void loadBalances(Economy econ){
        if(!main.getConfig().contains("balance")) return;

        for(String uuid : main.getConfig().getConfigurationSection("balance").getKeys(false)){
            econ.depositPlayer(NameFetcher.getName(uuid), main.getConfig().getDouble("balance." + UUID.fromString(uuid)));
        }
    }
}
