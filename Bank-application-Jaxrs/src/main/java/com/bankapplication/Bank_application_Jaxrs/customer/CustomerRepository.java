package com.bankapplication.Bank_application_Jaxrs.customer;

import java.util.LinkedList;
import java.util.List;

import static com.bankapplication.Bank_application_Jaxrs.BankApplication.customers;
import com.bankapplication.Bank_application_Jaxrs.database.DatabaseOperations;
import com.bankapplication.Bank_application_Jaxrs.threads.StoreToDB;
import com.bankapplication.Bank_application_Jaxrs.transaction.Transaction;

public class CustomerRepository{	
	static {
		 customers = DatabaseOperations.loadCustomersFromDB();
        Thread storePeriodically = new Thread(new StoreToDB());
        storePeriodically.start();
	}
	
	public List<Customer> getCustomers() {
		return customers;
	}
	
	public List<Transaction> getTransactions(){
		List<Transaction> transactions = new LinkedList<>(); 
		
		for(Customer c : customers) {
			 for(Transaction t: c.getTransactionHistory()) {
				 transactions.add(t);
			 }
		}
		
		return transactions;
	}
	
	public Customer getCustomer(int id){
		for(Customer c : customers) {
			if(c.getId() == id){
				return c; 
			}
		}
			
		return null; 
	}
		
	public List<Transaction> getTransactionOfCustomer(int id){
		Customer c = getCustomer(id);
		if(c == null) return null; 
		return c.getTransactionHistory(); 
	}
	
	public Customer addCustomer(Customer c) {
		customers.add(c);
		c.setId();
		return c;
	}
}
