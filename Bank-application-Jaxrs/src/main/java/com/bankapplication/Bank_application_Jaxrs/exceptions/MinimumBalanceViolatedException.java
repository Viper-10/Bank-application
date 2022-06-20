package com.bankapplication.Bank_application_Jaxrs.exceptions;

public class MinimumBalanceViolatedException extends Exception{
    public MinimumBalanceViolatedException(){
        super("PLEASE ENSURE THAT MINIMUM BALANCE OF 1000 IS MAINTAINED");
    }
}
