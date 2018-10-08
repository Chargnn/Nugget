package com.chargnn.utils;

import com.chargnn.Main;
import com.chargnn.service.BalanceService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;

public class VaultHook {

    private Main main;

    public VaultHook(Main main){
        this.main = main;
    }

    public void hook(ServicePriority priority) {
        main.sm.register(Economy.class, new BalanceService(), main, priority);
    }
}
