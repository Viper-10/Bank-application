package com.bankapplication.Bank_application_Jaxrs.exceptions;

public class NotEnoughBalanceException extends Exception{
    float balanceNeeded;

    public NotEnoughBalanceException(float balanceNeeded){
        this.balanceNeeded = balanceNeeded;
    }

    public float getBalanceNeeded(){
        return balanceNeeded;
    }
}
