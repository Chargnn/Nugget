package com.chargnn;

import com.chargnn.command.BankCommand;
import com.chargnn.utils.file.BalanceFileManager;
import com.chargnn.command.BalanceCommand;
import com.chargnn.listener.PlayerListener;
import com.chargnn.service.EconomyService;
import com.chargnn.utils.file.BankFileManager;
import com.chargnn.utils.file.ConfigManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Logger;

public class Main extends JavaPlugin
{
    private static final Logger log = Logger.getLogger("Nugget");
    public ServicesManager sm = getServer().getServicesManager();
    private BalanceFileManager balanceFile = new BalanceFileManager(this, "balances.yml");
    private BankFileManager banksFile = new BankFileManager(this, "banks.yml");

    private ConfigManager configManager;

    public Main() throws IOException {
        configManager = new ConfigManager(this);
    }

    @Override
    public void onEnable(){
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        configManager.setup();
        balanceFile.loadBalances();
        banksFile.loadBanks();
        log.info("Balances and banks info loaded");

        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("bank").setExecutor(new BankCommand(this));
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onDisable(){
        try {
            balanceFile.saveBalances();
            banksFile.saveBanks();
            log.info("Balances and banks info saved");
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            sm.register(Economy.class, new EconomyService(), this, ServicePriority.Highest);
            return true;
        }
        return false;
    }

}
