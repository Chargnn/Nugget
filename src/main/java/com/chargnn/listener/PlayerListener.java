package com.chargnn.listener;

import com.chargnn.service.EconomyService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private Economy econ;

    public PlayerListener(){
        this.econ = new EconomyService();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(!econ.hasAccount(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()))) {
            econ.createPlayerAccount(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
        }
    }

}
