package com.chargnn.command;

import com.chargnn.Main;
import com.chargnn.service.EconomyService;
import com.chargnn.utils.Permissions;
import com.chargnn.utils.file.ConfigManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private Main main;
    private Economy econ;

    public BalanceCommand(Main main){
        this.econ = new EconomyService();
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("Player only command");
            return true;
        }

        Player p = (Player) commandSender;
        OfflinePlayer sender = Bukkit.getOfflinePlayer(p.getUniqueId());

        // /balance
        if(strings.length == 0) {
            if (!commandSender.hasPermission(Permissions.BALANCE_CMD)) {
                noPersmission(commandSender);
                return true;
            }

            if (!econ.hasAccount(sender)) {
                noAccount(commandSender, commandSender.getName(), false);
                return true;
            }

            commandSender.sendMessage(ChatColor.GREEN + "Balance:" + ChatColor.WHITE + " " + econ.getBalance(sender) + " " + (econ.getBalance(sender) > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ".");
            return true;

        }

        // /balance {player}
        if(strings.length == 1){
            if(!commandSender.hasPermission(Permissions.BALANCE_OTHER_CMD)){
                noPersmission(commandSender);
                return true;
            }

            if(!econ.hasAccount(Bukkit.getPlayer(strings[0]))){
                noAccount(commandSender, strings[0], false);
                return true;
            }

            commandSender.sendMessage(ChatColor.GREEN + "Balance of " + strings[0] + " :" + ChatColor.WHITE + " " + econ.getBalance(strings[0]) + " " + (econ.getBalance(sender) > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ".");
            return true;
        }


        switch (strings[0].toLowerCase()){
            // /balance add {player} {amount}
            case "add":{
                if(!commandSender.hasPermission(Permissions.BALANCE_ADD_CMD)){
                    noPersmission(commandSender);
                    return true;
                }

                if(strings.length == 3){
                    if(!econ.hasAccount(Bukkit.getPlayer(strings[1]))){
                        noAccount(commandSender, strings[1], true);
                        return true;
                    }

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

                    if(econ.depositPlayer(Bukkit.getPlayer(strings[1]), x).transactionSuccess()) {
                        commandSender.sendMessage(ChatColor.GREEN + "Successfully added " + ChatColor.WHITE + x + " " + (x > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ChatColor.GREEN + " to " + ChatColor.WHITE + strings[1] + ".");
                        return true;
                    }
                } else {
                    sendUsage(commandSender);
                    return true;
                }
                return false;
            }
            // /balance sub {player} {amount}
            case "sub":{
                if(!commandSender.hasPermission(Permissions.BALANCE_SUB_CMD)){
                    noPersmission(commandSender);
                    return true;
                }

                if(strings.length == 3){
                    if(!econ.hasAccount(Bukkit.getPlayer(strings[1]))){
                        noAccount(commandSender, strings[1], true);
                        return true;
                    }

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

                   if(econ.withdrawPlayer(Bukkit.getPlayer(strings[1]), x).transactionSuccess()) {
                       commandSender.sendMessage(ChatColor.GREEN + "Successfully removed " + ChatColor.WHITE + x + " " + (x > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ChatColor.GREEN + " to " + ChatColor.WHITE + strings[1] + ".");
                       return true;
                   }
                } else {
                    sendUsage(commandSender);
                    return true;
                }
                return false;
            }
            // /balance set {player} {amount}
            case "set":{
                if(!commandSender.hasPermission(Permissions.BALANCE_SET_CMD)){
                    noPersmission(commandSender);
                    return true;
                }

                if(strings.length == 3){
                    if(!econ.hasAccount(strings[1])){
                        noAccount(commandSender, strings[1], true);
                        return true;
                    }

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

                    if(econ.withdrawPlayer(strings[1], econ.getBalance(Bukkit.getPlayer(strings[1]))).transactionSuccess() &&  econ.depositPlayer(Bukkit.getPlayer(strings[1]), x).transactionSuccess()) {
                        commandSender.sendMessage(ChatColor.GREEN + "Successfully set " + ChatColor.WHITE + x + " " + (x > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ChatColor.GREEN + " to " + ChatColor.WHITE + strings[1] + ".");
                        return true;
                    }
                } else {
                    sendUsage(commandSender);
                    return true;
                }
                return false;
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
        cs.sendMessage(ChatColor.RED + main.getCommand("balance").getUsage());
    }

    private void invalidAmount(CommandSender cs){
        cs.sendMessage(ChatColor.RED + "This is not a valid amount.");
    }
}
