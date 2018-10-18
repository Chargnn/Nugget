package com.chargnn.command;

import com.chargnn.Main;
import com.chargnn.service.EconomyService;
import com.chargnn.utils.Permissions;
import com.chargnn.utils.file.ConfigManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BalanceCommand implements CommandExecutor {

    private Main main;
    private Economy econ;

    public BalanceCommand(Main main){
        this.econ = new EconomyService();
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(strings.length == 0 || strings.length > 3){
            sendUsage(commandSender);
            return true;
        }

        switch (strings[0].toLowerCase()){
            case "balances":{
                if(!commandSender.hasPermission(Permissions.BALANCE_CMD)){
                    noPersmission(commandSender);
                    return true;
                }

                if(strings.length == 1) {
                    if(!econ.hasAccount(commandSender.getName())){
                        noAccount(commandSender, commandSender.getName(), false);
                        return true;
                    }

                    commandSender.sendMessage(ChatColor.GREEN + "Balance:" + ChatColor.WHITE + " " +  econ.getBalance(commandSender.getName()) + " " + (econ.getBalance(commandSender.getName()) > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ".");
                    return true;
                } else if(strings.length == 2){
                    if(!econ.hasAccount(strings[1])){
                        noAccount(commandSender, strings[1], false);
                        return true;
                    }

                    if(!commandSender.hasPermission(Permissions.BALANCE_OTHER_CMD)){
                        noPersmission(commandSender);
                        return true;
                    }

                    commandSender.sendMessage(ChatColor.GREEN + "Balance of " + strings[1] + " :" + ChatColor.WHITE + " " + econ.getBalance(strings[1]) + " " + (econ.getBalance(commandSender.getName()) > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ".");
                    return true;
                } else {
                    sendUsage(commandSender);
                    return true;
                }
            }
            case "add":{
                if(!commandSender.hasPermission(Permissions.BALANCE_ADD_CMD)){
                    noPersmission(commandSender);
                    return true;
                }

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
                    commandSender.sendMessage(ChatColor.GREEN + "Successfully added " + ChatColor.WHITE + x + " " + (x > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ChatColor.GREEN + " to " + ChatColor.WHITE + strings[1] + ".");
                    return true;
                } else {
                    sendUsage(commandSender);
                    return true;
                }
            }
            case "sub":{
                if(!commandSender.hasPermission(Permissions.BALANCE_SUB_CMD)){
                    noPersmission(commandSender);
                    return true;
                }

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
                    commandSender.sendMessage(ChatColor.GREEN + "Successfully removed " + ChatColor.WHITE + x + " " + (x > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ChatColor.GREEN + " to " + ChatColor.WHITE + strings[1] + ".");
                    return true;
                } else {
                    sendUsage(commandSender);
                    return true;
                }
            }
            case "set":{
                if(!commandSender.hasPermission(Permissions.BALANCE_SET_CMD)){
                    noPersmission(commandSender);
                    return true;
                }

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

                    econ.withdrawPlayer(strings[1], econ.getBalance(strings[1]));
                    econ.depositPlayer(strings[1], x);
                    commandSender.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.WHITE + x + " " + (x > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ChatColor.GREEN + " to " + ChatColor.WHITE + strings[1] + ".");
                    return true;
                } else {
                    sendUsage(commandSender);
                    return true;
                }
            }
            default:{
                sendUsage(commandSender);
                return true;
            }
        }
    }

    private void noAccount(CommandSender cs, String playerName, boolean createAccount){
        cs.sendMessage(ChatColor.RED + "Player " + playerName + " does not have an account.");
        if(createAccount) {
            econ.createPlayerAccount(playerName);
            cs.sendMessage(ChatColor.GREEN + "Account was created for " + playerName + ".");
        }
    }

    private void noPersmission(CommandSender cs){
        cs.sendMessage(ChatColor.RED + "You don't have enough permission for that command!");
    }

    private void sendUsage(CommandSender cs){
        cs.sendMessage(ChatColor.RED + main.getCommand("ngt").getUsage());
    }

    private void invalidAmount(CommandSender cs){
        cs.sendMessage(ChatColor.RED + "This is not a valid amount.");
    }
}
