package com.chargnn.command;

import com.chargnn.Main;
import com.chargnn.api.UUIDFetcher;
import com.chargnn.service.BalanceService;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BalanceCommand implements CommandExecutor {

    private Main main;
    private Economy econ;

    public BalanceCommand(Main main){
        this.econ = new BalanceService();
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(strings.length == 0 || strings.length > 3){
            sendUsage(commandSender);
            return true;
        }

        switch (strings[0].toLowerCase()){
            case "balance":{
                if(strings.length == 1) {
                    if(!econ.hasAccount(commandSender.getName())){
                        noAccount(commandSender, commandSender.getName(), false);
                        return true;
                    }

                    commandSender.sendMessage(ChatColor.GREEN + "Balance:" + ChatColor.WHITE + " " +  econ.getBalance(commandSender.getName()) + "$.");
                } else if(strings.length == 2){
                    if(!econ.hasAccount(strings[1])){
                        noAccount(commandSender, strings[1], false);
                        return true;
                    }
                    commandSender.sendMessage(ChatColor.GREEN + "Balance of " + strings[1] + " :" + ChatColor.WHITE + " " + econ.getBalance(strings[1]) + "$.");
                } else {
                    sendUsage(commandSender);
                    return true;
                }

                break;
                }
            case "add":{
                if(!econ.hasAccount(strings[1])){
                    noAccount(commandSender, strings[1], true);
                    return true;
                }

                if(strings.length == 3){
                    double x;

                    try{
                        x = Double.parseDouble(strings[2]);
                    }catch(Exception e){
                        invalidAmount(commandSender);
                        return true;
                    }

                    if(x < 0){
                        invalidAmount(commandSender);
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
                    noAccount(commandSender, strings[1], true);
                    return true;
                }

                if(strings.length == 3){
                    double x;

                    try{
                        x = Double.parseDouble(strings[2]);
                    }catch(Exception e){
                        invalidAmount(commandSender);
                        return true;
                    }
                    if(x < 0){
                        invalidAmount(commandSender);
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

    private void noAccount(CommandSender cs, String playerName, boolean createAccount){
        cs.sendMessage(ChatColor.RED + "Player " + playerName + " does not have an account.");
        if(createAccount) {
            econ.createPlayerAccount(playerName);
            cs.sendMessage(ChatColor.GREEN + "Account was created for " + playerName + ".");
        }
    }

    private void sendUsage(CommandSender cs){
        cs.sendMessage(ChatColor.RED + main.getCommand("econ").getUsage());
    }

    private void invalidAmount(CommandSender cs){
        cs.sendMessage(ChatColor.RED + "This is not a valid amount.");
    }
}
