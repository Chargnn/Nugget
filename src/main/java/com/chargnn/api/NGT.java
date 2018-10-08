package com.chargnn.api;

import com.chargnn.Main;
import com.chargnn.service.BalanceService;
import com.chargnn.utils.file.AbstractYml;
import net.milkbowl.vault.economy.Economy;

import java.io.IOException;
import java.util.UUID;

public class NGT extends AbstractYml {

    private Economy econ = new BalanceService();

    public NGT(Main main, String fileName) throws IOException {
        super(main, fileName);
    }

    public void saveBalances(){
        for(UUID uuid : BalanceService.balance.keySet()){
            fileConfiguration.set("balance." + uuid, BalanceService.balance.get(uuid));
        }

        try {
            this.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBalances(){
        if(!fileConfiguration.contains("balance")) return;

        for(String uuid : fileConfiguration.getConfigurationSection("balance").getKeys(false)){
            econ.depositPlayer(NameFetcher.getName(uuid), fileConfiguration.getDouble("balance." + uuid));
        }
    }
}
