package com.bankapplication.Bank_application_Jaxrs.account;

import java.util.HashMap;
import java.util.Map;

public class Account {
    private static final Map<Integer, Boolean> usedAccNumbers = new HashMap<>();
    public static int accountNumberCount;

    private int accountNumber;
    private float balance;

    public Account(){}

    public Account(int accountNumber, float balance){
        this.accountNumber = accountNumber;
        this.balance = balance;
    }


    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber){
        this.accountNumber = accountNumber;
    }

    public void initializeAccountNumber() {
        while(usedAccNumbers.containsKey(accountNumberCount)) accountNumberCount++;
        usedAccNumbers.put(accountNumberCount, true);
        accountNumber = accountNumberCount++;
    }


    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
    
    @Override
    public String toString() {
    	return "account number: " + accountNumber + " balance: " + balance; 
    }
}
