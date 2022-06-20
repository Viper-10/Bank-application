package com.bankapplication.Bank_application_Jaxrs.operations;

import com.bankapplication.Bank_application_Jaxrs.account.Account;
import com.bankapplication.Bank_application_Jaxrs.constants.Constants;
import com.bankapplication.Bank_application_Jaxrs.customer.Customer;
import com.bankapplication.Bank_application_Jaxrs.exceptions.MinimumBalanceViolatedException;
import com.bankapplication.Bank_application_Jaxrs.exceptions.NotEnoughBalanceException;

public class AccountOperations{
    public static float withdraw(Account acc, float withdrawAmount) throws NotEnoughBalanceException, MinimumBalanceViolatedException {
        if(acc.getBalance() < withdrawAmount){
            throw new NotEnoughBalanceException(withdrawAmount-acc.getBalance());
        }

        if(acc.getBalance()-withdrawAmount < Constants.MINIMUM_BALANCE){
            throw new MinimumBalanceViolatedException();
        }

        acc.setBalance(acc.getBalance()-withdrawAmount);
        return acc.getBalance();
    }


    public static float deposit(Account acc, float depositAmount) {
        acc.setBalance(acc.getBalance()+depositAmount);
        return acc.getBalance();
    }


    public static void changePassword(Customer c, String newPassword) {
        c.setPassword(newPassword);
    }


    public static float transfer(Account acc1, Account acc2, float transferAmount) throws NotEnoughBalanceException, MinimumBalanceViolatedException {
        withdraw(acc1, transferAmount);
        deposit(acc2,transferAmount);
        return acc1.getBalance();
    }
}
