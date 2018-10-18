package com.chargnn.service;

import com.chargnn.api.NameFetcher;
import com.chargnn.api.UUIDFetcher;
import com.chargnn.model.Balance;
import com.chargnn.model.Bank;
import com.chargnn.utils.ListenerMap;
import com.chargnn.utils.file.ConfigManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EconomyService implements Economy {

    public static ListenerMap<UUID, Balance> balances = new ListenerMap<>();
    public static List<Bank> banks = new ArrayList<>();

    /**
     * Checks if economy method is enabled.
     * @return Success or Failure
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Gets name of economy method
     * @return Name of Economy Method
     */
    @Override
    public String getName() {
        return "nugget";
    }

    /**
     * Returns true if the given implementation supports banks.
     * @return true if the implementation supports banks
     */
    @Override
    public boolean hasBankSupport() {
        return true;
    }

    /**
     * Some economy plugins round off after a certain number of digits.
     * This function returns the number of digits the plugin keeps
     * or -1 if no rounding occurs.
     * @return number of digits after the decimal point kept
     */
    @Override
    public int fractionalDigits() {
        return ConfigManager.getFractionalDigits();
    }

    /**
     * Format amount into a human readable String This provides translation into
     * economy specific formatting to improve consistency between plugins.
     *
     * @param amount to format
     * @return Human readable string describing amount
     */
    @Override
    public String format(double amount) {
        amount = Math.ceil(amount);

        return String.format("%d %s", (int)amount, "$");
    }

    /**
     * Returns the name of the currency in plural form.
     * If the economy being used does not support currency names then an empty string will be returned.
     *
     * @return name of the currency (plural)
     */
    @Override
    public String currencyNamePlural() {
        return ConfigManager.getCurrencyNamePlural();
    }

    /**
     * Returns the name of the currency in singular form.
     * If the economy being used does not support currency names then an empty string will be returned.
     *
     * @return name of the currency (singular)
     */
    @Override
    public String currencyNameSingular() {
        return ConfigManager.getCurrencyNameSingular();
    }

    /**
     *
     * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer)} instead.
     */
    @Override
    public boolean hasAccount(String playerName) {
        return balances.containsKey(UUIDFetcher.getUUID(playerName));
    }

    /**
     * Checks if this player has an account on the server yet
     * This will always return true if the player has joined the server at least once
     * as all major economy plugins auto-generate a player account when the player joins the server
     *
     * @param player to check
     * @return if the player has an account
     */
    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return balances.containsKey(player.getUniqueId());
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer, String)} instead.
     */
    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    /**
     * Checks if this player has an account on the server yet on the given world
     * This will always return true if the player has joined the server at least once
     * as all major economy plugins auto-generate a player account when the player joins the server
     *
     * @param player to check in the world
     * @param worldName world-specific account
     * @return if the player has an account
     */
    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer)} instead.
     */
    @Override
    public double getBalance(String playerName) {
        if(hasAccount(playerName)) {
            return balances.get(UUIDFetcher.getUUID(playerName)).amount;
        } else{
            return 0;
        }
    }

    /**
     * Gets balances of a player
     *
     * @param player of the player
     * @return Amount currently held in players account
     */
    @Override
    public double getBalance(OfflinePlayer player) {
        if(hasAccount(player)) {
            return balances.get(player.getUniqueId()).amount;
        } else{
            return 0;
        }
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer, String)} instead.
     */
    @Override
    public double getBalance(String playerName, String worldName) {
        return getBalance(playerName);
    }

    /**
     * Gets balances of a player on the specified world.
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balances will be returned.
     * @param player to check
     * @param world name of the world
     * @return Amount currently held in players account
     */
    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #has(OfflinePlayer, double)} instead.
     */
    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    /**
     * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to check
     * @param amount to check for
     * @return True if <b>player</b> has <b>amount</b>, False else wise
     */
    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    /**
     * @deprecated As of VaultAPI 1.4 use @{link {@link #has(OfflinePlayer, String, double)} instead.
     */
    @Override
    public boolean has(String playerName, String world, double amount) {
        if(amount < 0)
            amount *= -1;

        return getBalance(playerName) >= amount;
    }

    /**
     * Checks if the player account has the amount in a given world - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balances will be returned.
     *
     * @param player to check
     * @param worldName to check with
     * @param amount to check for
     * @return True if <b>player</b> has <b>amount</b>, False else wise
     */
    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        if(amount < 0)
            amount *= -1;

        return getBalance(player) >= amount;
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #withdrawPlayer(OfflinePlayer, double)} instead.
     */
    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if(amount < 0)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");

        if(getBalance(playerName) >= amount) {
            balances.put(UUIDFetcher.getUUID(playerName), new Balance(getBalance(playerName) - amount));
            return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        }
    }

    /**
     * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to withdraw from
     * @param amount Amount to withdraw
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if(amount < 0)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");

        if(getBalance(player) >= amount) {
            balances.put(player.getUniqueId(), new Balance(getBalance(player) - amount));
            return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        }
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #withdrawPlayer(OfflinePlayer, String, double)} instead.
     */
    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    /**
     * Withdraw an amount from a player on a given world - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balances will be returned.
     * @param player to withdraw from
     * @param worldName - name of the world
     * @param amount Amount to withdraw
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #depositPlayer(OfflinePlayer, double)} instead.
     */
    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if(amount < 0)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");

        balances.put(UUIDFetcher.getUUID(playerName), new Balance(getBalance(playerName) + amount));
        return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
    }

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param player to deposit to
     * @param amount Amount to deposit
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if(amount < 0)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");

        balances.put(player.getUniqueId(), new Balance(getBalance(player) + amount));
        return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {@link #depositPlayer(OfflinePlayer, String, double)} instead.
     */
    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    /**
     * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balances will be returned.
     *
     * @param player to deposit to
     * @param worldName name of the world
     * @param amount Amount to deposit
     * @return Detailed response of transaction
     */
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {{@link #createBank(String, OfflinePlayer)} instead.
     */
    @Override
    public EconomyResponse createBank(String name, String owner) {
        Bank bank = new Bank(name, UUIDFetcher.getUUID(owner));
        if(!banks.contains(bank)) {
            banks.add(bank);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank with that name already exists");
        }
    }

    /**
     * Creates a bank account with the specified name and the player as the owner
     * @param name of account
     * @param player the account should be linked to
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        Bank bank = new Bank(name, player.getUniqueId());
        if(!banks.contains(bank)) {
            banks.add(bank);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank with that name already exists");
        }
    }

    /**
     * Deletes a bank account with the specified name.
     * @param name of the back to delete
     * @return if the operation completed successfully
     */
    @Override
    public EconomyResponse deleteBank(String name) {
        for(Bank bank : banks) {
            if (bank.name.equals(name)) {
                banks.remove(bank);
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
            }
        }

        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank not found");
    }

    /**
     * Returns the amount the bank has
     * @param name of the account
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankBalance(String name) {
        for(Bank bank : banks) {
            if (bank.name.equals(name)) {
                return new EconomyResponse(0, bank.balance.amount, EconomyResponse.ResponseType.SUCCESS, null);
            }
        }

        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank not found");
    }

    /**
     * Returns true or false whether the bank has the amount specified - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param name of the account
     * @param amount to check for
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankHas(String name, double amount) {
        if(amount < 0)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");

        for(Bank bank : banks) {
            if (bank.name.equals(name)) {
                if(bank.balance.amount >= amount) {
                    return new EconomyResponse(amount, bank.balance.amount, EconomyResponse.ResponseType.SUCCESS, null);
                } else {
                    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
                }
            }
        }

        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank not found");
    }

    /**
     * Withdraw an amount from a bank account - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param name of the account
     * @param amount to withdraw
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        if(amount < 0)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");

        for(Bank bank : banks) {
            if (bank.name.equals(name)) {
                if(bankHas(bank.name, amount).transactionSuccess()) {
                    bank.balance.amount -= amount;
                    return new EconomyResponse(amount, bank.balance.amount, EconomyResponse.ResponseType.SUCCESS, null);
                } else {
                    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
                }
            }
        }

        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank not found");
    }

    /**
     * Deposit an amount into a bank account - DO NOT USE NEGATIVE AMOUNTS
     *
     * @param name of the account
     * @param amount to deposit
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        if(amount < 0)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");

        for(Bank bank : banks) {
            if (bank.name.equals(name)) {
                if(bankHas(bank.name, amount).transactionSuccess()) {
                    bank.balance.amount += amount;
                    return new EconomyResponse(amount, bank.balance.amount, EconomyResponse.ResponseType.SUCCESS, null);
                } else {
                    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
                }
            }
        }

        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank not found");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {{@link #isBankOwner(String, OfflinePlayer)} instead.
     */
    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        for(Bank bank : banks) {
            if (bank.name.equals(name)) {
                if(NameFetcher.getName(bank.owner).equals(playerName)){
                    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
                } else {
                    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player is not the owner");
                }
            }
        }

        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank not found");
    }

    /**
     * Check if a player is the owner of a bank account
     *
     * @param name of the account
     * @param player to check for ownership
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        for(Bank bank : banks) {
            if (bank.name.equals(name)) {
                if(NameFetcher.getName(bank.owner).equals(player.getName())){
                    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
                } else {
                    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player is not the owner");
                }
            }
        }

        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank not found");
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {{@link #isBankMember(String, OfflinePlayer)} instead.
     */
    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        for(Bank bank : banks) {
            if (bank.name.equals(name)) {
                if(bank.members.contains(UUIDFetcher.getUUID(playerName))){
                    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
                } else {
                    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player is not a member");
                }
            }
        }

        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank not found");
    }

    /**
     * Check if the player is a member of the bank account
     *
     * @param name of the account
     * @param player to check membership
     * @return EconomyResponse Object
     */
    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        for(Bank bank : banks) {
            if (bank.name.equals(name)) {
                if(bank.members.contains(player.getUniqueId())){
                    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
                } else {
                    return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player is not a member");
                }
            }
        }

        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Bank not found");
    }

    /**
     * Gets the list of banks
     * @return the List of Banks
     */
    @Override
    public List<String> getBanks() {
        List<String> bankNames = new ArrayList<>();
        for(Bank bank : banks) {
            bankNames.add(bank.name);
        }

        return bankNames;
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {{@link #createPlayerAccount(OfflinePlayer)} instead.
     */
    @Override
    public boolean createPlayerAccount(String playerName) {
        if(hasAccount(playerName))
            return false;

        balances.put(UUIDFetcher.getUUID(playerName), new Balance((double) ConfigManager.getInitial()));
        return hasAccount(playerName);
    }

    /**
     * Attempts to create a player account for the given player
     * @param player OfflinePlayer
     * @return if the account creation was successful
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if(hasAccount(player))
            return false;

        balances.put(player.getUniqueId(), new Balance((double) ConfigManager.getInitial()));
        return hasAccount(player);
    }

    /**
     * @deprecated As of VaultAPI 1.4 use {{@link #createPlayerAccount(OfflinePlayer, String)} instead.
     */
    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    /**
     * Attempts to create a player account for the given player on the specified world
     * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the global balances will be returned.
     * @param player OfflinePlayer
     * @param worldName String name of the world
     * @return if the account creation was successful
     */
    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }
}
