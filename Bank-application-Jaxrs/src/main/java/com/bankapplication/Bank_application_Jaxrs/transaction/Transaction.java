package com.bankapplication.Bank_application_Jaxrs.transaction;

public class Transaction {
    int id;
    String type;
    float amount;
    float balance;

    public Transaction(){}

    public Transaction(String type, float amount, float balance) {
        this.type = type;
        this.amount = amount;
        this.balance = balance;
    }

    public Transaction(int id, String type, float amount, float balance) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    @Override
    public String toString(){return id+","+type+","+amount+","+balance;}
}
