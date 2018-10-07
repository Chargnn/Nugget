package com.chargnn;

import com.chargnn.api.NGT;
import com.chargnn.command.BalanceCommand;
import com.chargnn.listener.PlayerListener;
import com.chargnn.service.BalanceService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin
{
    private static final Logger log = Logger.getLogger("Server");
    private BalanceService service;
    private Economy econ = null;

    public Main(){
        service = new BalanceService(this);
    }

    @Override
    public void onEnable(){
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("econ").setExecutor(new BalanceCommand(econ, this));
        NGT.loadBalances(econ);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(econ), this);

        log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onDisable(){
        NGT.saveBalances();

        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    private boolean setupEconomy()
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        Bukkit.getServer().getServicesManager().register(Economy.class, service, this, ServicePriority.Normal);
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
