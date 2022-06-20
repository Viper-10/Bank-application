package com.bankapplication.Bank_application_Jaxrs.threads;

import com.bankapplication.Bank_application_Jaxrs.BankApplication;
import com.bankapplication.Bank_application_Jaxrs.database.DatabaseOperations;

public class StoreToDB implements Runnable{
    @Override
    public void run(){
        persistDataToDB();
    }

    public void persistDataToDB(){
        while(!BankApplication.stop){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DatabaseOperations.persistCustomersToDB();
//            System.out.println("Data persisted to db");
        }
    }
}
