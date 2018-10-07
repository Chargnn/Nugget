package com.chargnn.listener;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private Economy econ;

    public PlayerListener(Economy econ){
        this.econ = econ;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(!econ.hasAccount(e.getPlayer().getName())) {
            econ.createPlayerAccount(e.getPlayer().getName());
        }
    }

}
