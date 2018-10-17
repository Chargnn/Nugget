package com.chargnn.utils.file;

import com.chargnn.Main;
import com.chargnn.api.NameFetcher;
import com.chargnn.service.BalanceService;
import net.milkbowl.vault.economy.Economy;

import java.io.IOException;
import java.util.UUID;

public class BalanceFileManager extends AbstractYml {

    private Economy econ = new BalanceService();

    public BalanceFileManager(Main main, String fileName) throws IOException {
        super(main, fileName);
    }

    public void saveBalances() throws IOException {
        for(UUID uuid : BalanceService.balance.keySet()){
            fileConfiguration.set("balance." + uuid, BalanceService.balance.get(uuid));
        }

        this.save();
    }

    public void loadBalances(){
        if(!this.file.exists()){
            this.file.getParentFile().mkdirs();
            main.saveResource(this.fileName, false);
        }

        if(!fileConfiguration.contains("balance")) return;

        for(String uuid : fileConfiguration.getConfigurationSection("balance").getKeys(false)){
            econ.depositPlayer(NameFetcher.getName(uuid), fileConfiguration.getDouble("balance." + uuid));
        }
    }
}
