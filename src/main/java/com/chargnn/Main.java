package com.chargnn;

import com.chargnn.api.NGT;
import com.chargnn.command.BalanceCommand;
import com.chargnn.listener.PlayerListener;
import com.chargnn.service.BalanceService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Hello world!
 *
 */
public class Main extends JavaPlugin
{
    private BalanceService service;

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
}
