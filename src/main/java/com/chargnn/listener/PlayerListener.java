package com.chargnn.listener;

import com.chargnn.api.UUIDFetcher;
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

        if(!service.hasBalance(UUIDFetcher.getUUID(e.getPlayer().getName()))) {
            service.set(UUIDFetcher.getUUID(e.getPlayer().getName()), 200d);
        }
    }

}
