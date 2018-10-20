package com.chargnn.model;

import java.util.UUID;

public class Bank {

    public String name;
    public UUID owner;
    public Balance balance;

    public Bank(String name, UUID owner){
        this.name = name;
        this.owner = owner;
        this.balance = new Balance(0);
    }

}
