package com.chargnn.model;

import java.util.List;
import java.util.UUID;

public class Bank {

    public String name;
    public UUID owner;
    public Balance balance;
    public List<UUID> members;

    public Bank(String name, UUID owner){
        this.name = name;
        this.owner = owner;
    }

}
