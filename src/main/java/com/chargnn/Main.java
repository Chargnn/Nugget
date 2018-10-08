package com.chargnn;

import com.chargnn.api.NGT;
import com.chargnn.command.BalanceCommand;
import com.chargnn.listener.PlayerListener;
import com.chargnn.utils.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Logger;

public class Main extends JavaPlugin
{
    private static final Logger log = Logger.getLogger("Server");
    public ServicesManager sm;
    private VaultHook vaultHook;
    private NGT balanceFile;

    public Main() {
        sm = getServer().getServicesManager();
        vaultHook = new VaultHook(this);
    }

    @Override
    public void onEnable(){
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            setupFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getCommand("econ").setExecutor(new BalanceCommand(this));

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onDisable(){
        balanceFile.saveBalances();

        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            vaultHook.hook(ServicePriority.Highest);

            return true;
        }

        return false;
    }

    private void setupFiles() throws IOException {
        balanceFile = new NGT(this, "balance.yml");

        balanceFile.loadBalances();
    }
}
