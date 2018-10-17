package com.chargnn.utils.file;

import com.chargnn.Main;
import org.bukkit.ChatColor;

public class ConfigManager {

    private static Main main;

    public ConfigManager(Main main){
        this.main = main;
    }

    public void setup(){
        main.getConfig().options().copyDefaults(true);
        main.saveDefaultConfig();
    }

    public static int getInitial(){
        return main.getConfig().getInt("balance.initial");
    }

    public static String getCurrencyNamePlural(){
        String message = main.getConfig().getString("currency.currencyNamePlural");
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }

    public static String getCurrencyNameSingular(){
        String message = main.getConfig().getString("currency.currencyNameSingular");
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }

    public static int getFractionalDigits(){
        return main.getConfig().getInt("currency.fractionalDigits");
    }
}
