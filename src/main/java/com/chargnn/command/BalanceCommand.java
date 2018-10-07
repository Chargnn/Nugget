package com.chargnn.command;

import com.chargnn.api.UUIDFetcher;
import com.chargnn.service.BalanceService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BalanceCommand implements CommandExecutor {

    private Economy econ;

    public BalanceCommand(Economy econ){
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(strings.length == 0 || strings.length > 3){
            sendUsage(commandSender);
            return true;
        }

        switch (strings[0].toLowerCase()){
            case "balance":{
                if(!econ.hasAccount(commandSender.getName())){
                    noAccount(commandSender, commandSender.getName());
                    return true;
                }

                if(strings.length == 1) {
                    commandSender.sendMessage(ChatColor.GREEN + "Balance:" + ChatColor.WHITE + " " + BalanceService.balance.get(UUIDFetcher.getUUID(commandSender.getName())) + "$.");
                } else {
                    sendUsage(commandSender);
                    return true;
                }

                break;
                }
            case "add":{
                if(!econ.hasAccount(strings[1])){
                    noAccount(commandSender, strings[1]);
                    return true;
                }

                if(strings.length == 3){
                    double x;

                    try{
                        x = Double.parseDouble(strings[2]);
                    }catch(Exception e){
                        invalidAmmount(commandSender);
                        return true;
                    }

                    if(x < 0){
                        invalidAmmount(commandSender);
                        return true;
                    }

                    econ.depositPlayer(strings[1], x);
                    commandSender.sendMessage(ChatColor.GREEN + "Successfully added " + ChatColor.WHITE + x + "$" + ChatColor.GREEN + " to " + ChatColor.WHITE + strings[1] + ".");
                } else {
                    sendUsage(commandSender);
                    return true;
                }

                break;
            }
            case "sub":{
                if(!econ.hasAccount(strings[1])){
                    noAccount(commandSender, strings[1]);
                    return true;
                }

                if(strings.length == 3){
                    double x;

                    try{
                        x = Double.parseDouble(strings[2]);
                    }catch(Exception e){
                        invalidAmmount(commandSender);
                        return true;
                    }
                    if(x < 0){
                        invalidAmmount(commandSender);
                        return true;
                    }

                    econ.withdrawPlayer(strings[1], x);
                    commandSender.sendMessage(ChatColor.GREEN + "Successfully removed " + ChatColor.WHITE + x + "$" + ChatColor.GREEN + " to " + ChatColor.WHITE + strings[1] + ".");
                } else {
                    sendUsage(commandSender);
                    return true;
                }

                break;
            }
            default:{
                sendUsage(commandSender);
                break;
            }
        }

        return true;
    }

    private void noAccount(CommandSender cs, String playerName){
        cs.sendMessage(ChatColor.RED + "Player " + playerName + " does not have an account.");
        econ.createPlayerAccount(playerName);
        cs.sendMessage(ChatColor.GREEN + "Account was created for " + playerName + ".");
    }

    private void sendUsage(CommandSender cs){
        cs.sendMessage(ChatColor.RED + "Usage: /[set/add/clear] [player/balance] (amount)");
    }

    private void invalidAmmount(CommandSender cs){
        cs.sendMessage(ChatColor.RED + "This is not a valid amount.");
    }
}
