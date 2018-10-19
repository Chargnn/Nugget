package com.chargnn.utils.file;

import com.chargnn.Main;
import com.chargnn.api.NameFetcher;
import com.chargnn.model.Bank;
import com.chargnn.service.EconomyService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class BankFileManager extends AbstractYml {

    private Economy econ = new EconomyService();

    public BankFileManager(Main main, String fileName) throws IOException {
        super(main, fileName);
    }

    public void saveBanks() throws IOException {
        for(Bank bank : EconomyService.banks){
            fileConfiguration.set("banks." + bank.name, Arrays.asList(bank.balance.amount, bank.owner.toString()));
        }

        this.save();
    }

    public void loadBanks(){
        if(!this.file.exists()){
            this.file.getParentFile().mkdirs();
            main.saveResource(this.fileName, false);
        }

        if(!fileConfiguration.contains("banks")) return;

        for(String name : fileConfiguration.getConfigurationSection("banks").getKeys(false)){
            econ.createBank(name, Bukkit.getOfflinePlayer(UUID.fromString(fileConfiguration.getList("banks." + name).get(1).toString())));
        }
    }
}
