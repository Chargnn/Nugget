package com.chargnn.utils.file;

import com.chargnn.Main;
import com.chargnn.api.NameFetcher;
import com.chargnn.service.BalanceService;
import net.milkbowl.vault.economy.Economy;

import java.io.IOException;
import java.util.UUID;

public class YmlManager extends AbstractYml {

    private Economy econ = new BalanceService();

    public YmlManager(Main main, String fileName) throws IOException {
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
