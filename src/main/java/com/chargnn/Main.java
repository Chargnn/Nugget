package com.chargnn;

import com.chargnn.api.NGT;
import com.chargnn.command.BalanceCommand;
import com.chargnn.listener.PlayerListener;
import com.chargnn.service.BalanceService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hello world!
 *
 */
public class Main extends JavaPlugin
{
    private BalanceService service;
    private Economy econ = null;

    public Main(){
        service = new BalanceService(this);
    }

    @Override
    public void onEnable(){
        getCommand("econ").setExecutor(new BalanceCommand(service));
        NGT.loadBalances(service);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(service), this);
    }

    @Override
    public void onDisable(){
        NGT.saveBalances();
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }

        return (econ != null);
    }
}
