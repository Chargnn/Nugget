package com.chargnn.listener;

import com.chargnn.service.BalanceService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private BalanceService service;

    public PlayerListener(BalanceService service){
        this.service = service;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){

        if(!service.hasAccount(e.getPlayer().getName())) {
            service.setPlayer(e.getPlayer().getName(), 200d);
        }
    }

}
