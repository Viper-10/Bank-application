package com.bankapplication.Bank_application_Jaxrs.customer.vm;

import com.bankapplication.Bank_application_Jaxrs.account.Account;
import com.bankapplication.Bank_application_Jaxrs.customer.Customer;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CustomerVM {
	
	int id; 
	String name;
	Account account; 
	int noOfTransactions; 
		
	public CustomerVM(){
		name="Kd"; 
		id=1;
	}
	
	public CustomerVM(Customer c){
		this.id = c.getId();
		this.name = c.getName();
		this.account = c.getAccount();
		this.noOfTransactions = c.getTransactionHistory().size();
	}
	
	@Override
	public String toString() {
		return "id: " + id + " name: " + name + " account " + account.toString() + " no of transactions " + noOfTransactions;   
	}
}
