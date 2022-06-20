package com.bankapplication.Bank_application_Jaxrs.customer;

import com.bankapplication.Bank_application_Jaxrs.account.Account;
import com.bankapplication.Bank_application_Jaxrs.constants.Constants;
import com.bankapplication.Bank_application_Jaxrs.fixed_deposit.FixedDeposit;
import com.bankapplication.Bank_application_Jaxrs.transaction.Transaction;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.*;


@XmlRootElement
public class Customer {
    public static int idCount = 0;
    private static final Map<Integer, Boolean> usedIds = new HashMap<>();

    private int id;
    private String name;
    private String password;
    private Account account;

    private final Set<String> passwordHistory = new LinkedHashSet<>();
    public final List<Transaction> transactionHistory = new LinkedList<>();
    public final List<FixedDeposit> fixedDeposits = new LinkedList<FixedDeposit>();

    {
        account = new Account();
    }

    public Customer(){

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Customer(String name, String password) {
        this.name = name;
        this.password = password;
        account.setBalance(Constants.MINIMUM_BALANCE);
        passwordHistory.add(password);
    }

    public Customer(int id, int accountNumber, String name, float balance, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        passwordHistory.add(password);
    }

    public Customer(int id, String name, String password, Account acc){
        this.id = id;
        this.name = name;
        this.password = password;
        this.account = acc;
    }

    public int getId() {
        return id;
    }

    public void setId() {
        while(usedIds.containsKey(idCount)) idCount++;
        usedIds.put(idCount, true);
        id = idCount++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        passwordHistory.add(password);

        if(passwordHistory.size() > 3){
            Iterator<String> it = passwordHistory.iterator();
            passwordHistory.remove(it.next());
        }
    }

    public void addToPasswordHistory(String password){
        if(passwordHistory.size() > 2) {
            Iterator<String> it = passwordHistory.iterator();
            passwordHistory.remove(it.next());
        }
        this.password = password;
        passwordHistory.add(password);
    }

    public Account getAccount() {
        return account;
    }

    public static Customer createCustomerFromStringFields(String[] fields){
        return new Customer(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]), fields[2], Float.parseFloat(fields[3]), fields[4]);
    }

    @Override
    public String toString(){
        return id + "," + account.getAccountNumber() + "," + name + "," + account.getBalance() + ","+ password;
    }

//    @Override
//    public String toString(){
//        return id + "," + name + "," + password;
//    }

    public boolean alreadyUsedPassword(String password){
        return passwordHistory.contains(password);
    }

    public void addTransaction(Transaction t){
        t.setId(transactionHistory.size()+1);
        transactionHistory.add(t);
    }

    public void addTransactionWithId(Transaction t){
        transactionHistory.add(t);
    }

    public int noOfTransactions(){
        return transactionHistory.size();
    }

    public void printTransactions(){
        for(Transaction t : transactionHistory){
            System.out.println(t);
        }
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void addFixedDeposit(FixedDeposit fd) {
    	fd.setId(fixedDeposits.size());
    	fixedDeposits.add(fd);
    }
    
    public void addFixedDepositWithId(FixedDeposit fd) {
    	fd.setId(fixedDeposits.size());
    	fixedDeposits.add(fd);
    }
    
    public Set<String> getPasswordHistory() {
        return passwordHistory;
    }
    
    public  List<FixedDeposit> getFixedDeposits(){
    	return fixedDeposits;
    }
    
    public FixedDeposit getFixedDeposit(int id) {
    	for(FixedDeposit fd: fixedDeposits) {
    		if(fd.getId() == id) {
    			return fd;
    		}
    	}
    	
    	return null; 
    }
}
