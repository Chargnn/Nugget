package com.chargnn.command;

import com.chargnn.Main;
import com.chargnn.model.Bank;
import com.chargnn.service.EconomyService;
import com.chargnn.utils.Permissions;
import com.chargnn.utils.file.ConfigManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BankCommand implements CommandExecutor {

    private Main main;
    private Economy econ;

    public BankCommand(Main main){
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

        if (strings.length > 2) {
            sendUsage(commandSender);
            return true;
        }

        // /bank
        if(strings.length == 0) {
            if (!commandSender.hasPermission(Permissions.BANK_CMD)) {
                noPersmission(commandSender);
                return true;
            }

            Bank bank = null;
            if(EconomyService.banks.size() > 1000) {
                bank = EconomyService.banks.stream().parallel().filter(x -> x.owner.equals(sender.getUniqueId())).findFirst().orElse(null);
            } else {
                bank = EconomyService.banks.stream().filter(x -> x.owner.equals(sender.getUniqueId())).findFirst().orElse(null);
            }

            if(bank == null){
                commandSender.sendMessage(ChatColor.RED + "You don't have any bank account");
                return true;
            }

            commandSender.sendMessage(ChatColor.GREEN + "Bank balance:" + ChatColor.WHITE + " " + econ.bankBalance(bank.name).balance + " " + (econ.bankBalance(bank.name).amount > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ".");
            return true;
        }

        switch (strings[0].toLowerCase()) {
            // /bank create {name}
            case "create": {
                if (!commandSender.hasPermission(Permissions.BANK_CREATE_CMD)) {
                    noPersmission(commandSender);
                    return true;
                }

                if (strings.length == 2) {
                    if (econ.getBanks().contains(strings[1])) {
                        bankExists(commandSender);
                        return true;
                    }

                    EconomyResponse resp = econ.createBank(strings[1], sender);
                    if(resp.transactionSuccess()) {
                        commandSender.sendMessage(ChatColor.GREEN + "You are now the owner of the bank account " + ChatColor.WHITE + strings[1]);
                        return true;
                    } else {
                        commandSender.sendMessage(ChatColor.RED + resp.errorMessage);
                        return true;
                    }
                } else {
                    sendUsage(commandSender);
                    return true;
                }
            }
            // /bank remove
            case "remove": {
                if (!commandSender.hasPermission(Permissions.BANK_REMOVE_CMD)) {
                    noPersmission(commandSender);
                    return true;
                }

                if (strings.length == 2) {
                    Bank bank = null;
                    if(EconomyService.banks.size() > 1000) {
                        bank = EconomyService.banks.stream().parallel().filter(x -> x.owner.equals(sender.getUniqueId())).findFirst().orElse(null);
                    } else {
                        bank = EconomyService.banks.stream().filter(x -> x.owner.equals(sender.getUniqueId())).findFirst().orElse(null);
                    }

                    if(bank == null){
                        commandSender.sendMessage(ChatColor.RED + "You don't have any bank account");
                        return true;
                    }

                    if (econ.isBankOwner(bank.name, sender).transactionSuccess()) {
                        econ.deleteBank(bank.name);
                        commandSender.sendMessage(ChatColor.GREEN + "The bank was successfully removed");
                        return true;
                    } else {
                        if (!commandSender.hasPermission(Permissions.BANK_REMOVE_OTHER_CMD)) {
                            commandSender.sendMessage(ChatColor.RED + "You are not the owner of that bank!");
                            return true;
                        }

                        EconomyResponse resp = econ.deleteBank(bank.name);
                        if(resp.transactionSuccess()) {
                            commandSender.sendMessage(ChatColor.GREEN + "The bank was successfully removed");
                            return true;
                        } else {
                            commandSender.sendMessage(ChatColor.RED + resp.errorMessage);
                            return true;
                        }
                    }
                } else {
                    sendUsage(commandSender);
                    return true;
                }
            }
            // /bank add {amount}
            case "add": {
                if (!commandSender.hasPermission(Permissions.BANK_ADD_CMD)) {
                    noPersmission(commandSender);
                    return true;
                }

                if (strings.length == 2) {
                    Bank bank = null;
                    if(EconomyService.banks.size() > 1000) {
                        bank = EconomyService.banks.stream().parallel().filter(x -> x.owner.equals(sender.getUniqueId())).findFirst().orElse(null);
                    } else {
                        bank = EconomyService.banks.stream().filter(x -> x.owner.equals(sender.getUniqueId())).findFirst().orElse(null);
                    }

                    if(bank == null){
                        commandSender.sendMessage(ChatColor.RED + "You don't have any bank account");
                        return true;
                    }

                    double x;

                    try{
                        x = Double.parseDouble(strings[1]);
                    }catch(Exception e){
                        invalidAmount(commandSender);
                        return true;
                    }
                    if(x < 0){
                        invalidAmount(commandSender);
                        return true;
                    }

                    if (econ.isBankOwner(bank.name, sender).transactionSuccess()) {
                        EconomyResponse resp = econ.bankDeposit(bank.name, x);
                        EconomyResponse resp2 = econ.withdrawPlayer(sender, x);
                        if (resp.transactionSuccess() && resp2.transactionSuccess()) {
                            commandSender.sendMessage(x + " "  + (x > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ChatColor.GREEN + " was added to your bank");
                            commandSender.sendMessage(ChatColor.GREEN + "You now have " + ChatColor.WHITE + econ.getBalance(sender) + " " + (x > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ChatColor.GREEN + " on you");
                            return true;
                        } else {
                            commandSender.sendMessage(ChatColor.RED + resp.errorMessage + " " + resp2.errorMessage);
                        }
                    } else {
                        InsufficientFunds(commandSender);
                        return true;
                    }
                } else {
                    sendUsage(commandSender);
                    return true;
                }
                return false;
            }
            // /bank sub {amount}
            case "sub": {
                if (!commandSender.hasPermission(Permissions.BANK_SUB_CMD)) {
                    noPersmission(commandSender);
                    return true;
                }

                if (strings.length == 2) {
                    Bank bank = null;
                    if(EconomyService.banks.size() > 1000) {
                        bank = EconomyService.banks.stream().parallel().filter(x -> x.owner.equals(sender.getUniqueId())).findFirst().orElse(null);
                    } else {
                        bank = EconomyService.banks.stream().filter(x -> x.owner.equals(sender.getUniqueId())).findFirst().orElse(null);
                    }

                    if(bank == null){
                        commandSender.sendMessage(ChatColor.RED + "You don't have any bank account");
                        return true;
                    }

                    double x;

                    try{
                        x = Double.parseDouble(strings[1]);
                    }catch(Exception e){
                        invalidAmount(commandSender);
                        return true;
                    }
                    if(x < 0){
                        invalidAmount(commandSender);
                        return true;
                    }

                    if (econ.isBankOwner(bank.name, sender).transactionSuccess() && econ.bankHas(bank.name, x).transactionSuccess()) {
                            if (econ.bankWithdraw(bank.name, x).transactionSuccess() && econ.depositPlayer(sender, x).transactionSuccess()) {
                                commandSender.sendMessage(x + " "  + (x > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ChatColor.GREEN + " was removed from your bank");
                                commandSender.sendMessage(ChatColor.GREEN + "You now have " + ChatColor.WHITE + econ.getBalance(sender) + " " + (x > 1 ? ConfigManager.getCurrencyNamePlural() : ConfigManager.getCurrencyNameSingular()) + ChatColor.GREEN + " on you");
                                return true;
                            }
                        } else {
                            InsufficientFunds(commandSender);
                            return true;
                        }
                } else {
                    sendUsage(commandSender);
                    return true;
                }
                return false;
            }
            default: {
                sendUsage(commandSender);
                return true;
            }
        }
    }

    private void InsufficientFunds(CommandSender cs){
        cs.sendMessage(ChatColor.RED + "Insufficient funds");
    }

    private void noPersmission(CommandSender cs){
        cs.sendMessage(ChatColor.RED + "You don't have enough permission for that command!");
    }

    private void sendUsage(CommandSender cs){
        cs.sendMessage(ChatColor.RED + main.getCommand("bank").getUsage());
    }

    private void bankExists(CommandSender cs){
        cs.sendMessage(ChatColor.RED + "This name is already taken.");
    }

    private void invalidAmount(CommandSender cs){
        cs.sendMessage(ChatColor.RED + "This is not a valid amount.");
    }
}
