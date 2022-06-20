package com.bankapplication.Bank_application_Jaxrs.database;

import com.bankapplication.Bank_application_Jaxrs.account.Account;
import com.bankapplication.Bank_application_Jaxrs.constants.Constants;
import com.bankapplication.Bank_application_Jaxrs.cryptography.AddOneCryptography;
import com.bankapplication.Bank_application_Jaxrs.cryptography.CryptographyI;
import com.bankapplication.Bank_application_Jaxrs.customer.Customer;
import com.bankapplication.Bank_application_Jaxrs.fixed_deposit.FixedDeposit;
import com.bankapplication.Bank_application_Jaxrs.transaction.Transaction;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

public abstract class DatabaseOperations {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Win32dll$";
    private static final List<Customer> customers = new LinkedList<>();
    private static final CryptographyI cryptography = new AddOneCryptography();

    public static List<Customer> loadCustomersFromDB(){
        try {
        	Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            
            if(conn == null){
                System.out.println("CONNECTION ESTABLISHMENT FAILED!!");
                return null;
            }

            try(
                    Statement statement = conn.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT * from Customers");
            ){

                while(rs.next()){
//                	Adding a customer
                    int cusId = rs.getInt("id");
                    String name = rs.getString("name");
                    String password = cryptography.decrypt(rs.getString("password"));

                    Account acc = getAccountOfCustomer(cusId);
                    Customer c = new Customer(cusId, name, password, acc);

                    addAllTransactionsOfCustomer(c);
                    addPasswordHistoryOfCustomer(c);
                    addFixedDepositsOfCustomer(c);

                    customers.add(c);
                }

//            INITIALISE STATIC CUSTOMER ID AND ACCOUNT NUMBER VARIABLE
                Customer.idCount = customers.size();
                Account.accountNumberCount = customers.size()+ Constants.ACC_NO_INITIAL_SIZE;
            }catch(SQLException e){
                e.printStackTrace();
            }

            return customers;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void persistCustomersToDB(){
        try(
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        ) {
            // DELETING EXISTING RECORDS
            try(
                    Statement customerStatement = conn.createStatement();
                Statement accountStatement = conn.createStatement();
                Statement transactionStatement = conn.createStatement();
                Statement passwordStatement = conn.createStatement();
            	Statement fixedDepositsStatement = conn.createStatement();
            ){
                transactionStatement.execute("DELETE FROM transactions");
                accountStatement.execute("DELETE FROM accounts");
                passwordStatement.execute("DELETE FROM passwords");
                fixedDepositsStatement.execute("DELETE FROM fixed_deposits");
                customerStatement.execute("DELETE FROM customers");
            } catch (SQLException e) {
                e.printStackTrace();
            }

//        INSERTING NEW RECORDS
            for(Customer c : customers){
                try(
                        PreparedStatement customerStatement = conn.prepareStatement("INSERT INTO customers VALUES(?, ?, ?)");
                        PreparedStatement accountStatement = conn.prepareStatement("INSERT INTO accounts VALUES(?, ?, ?)");
                        PreparedStatement transactionStatement = conn.prepareStatement("INSERT INTO transactions VALUES(?, ?, ?, ?, ?)");
                        PreparedStatement passwordStatement = conn.prepareStatement("INSERT INTO passwords VALUES(?, ?)");
                		PreparedStatement fixedDepositsStatement = conn.prepareStatement("INSERT INTO fixed_deposits VALUES(?, ?, ?, ?, ?, ?)");
                ) {
                    customerStatement.setInt(1, c.getId());
                    customerStatement.setString(2, c.getName());
                    customerStatement.setString(3, cryptography.encrypt(c.getPassword()));

                    customerStatement.executeUpdate();

                    Account acc = c.getAccount();
                    accountStatement.setFloat(1, acc.getBalance());
                    accountStatement.setInt(2, acc.getAccountNumber());
                    accountStatement.setInt(3, c.getId());

                    accountStatement.executeUpdate();

                    for(Transaction t : c.getTransactionHistory()){
                        transactionStatement.setInt(1, t.getId());
                        transactionStatement.setString(2, t.getType());
                        transactionStatement.setFloat(3, t.getAmount());
                        transactionStatement.setFloat(4, t.getBalance());
                        transactionStatement.setInt(5, c.getId());

                        transactionStatement.executeUpdate();
                    }

                    for(String password : c.getPasswordHistory()) {
                        passwordStatement.setString(1, cryptography.encrypt(password));
                        passwordStatement.setInt(2,c.getId());
                        passwordStatement.executeUpdate();
                    }
                    
                    for(FixedDeposit fd : c.getFixedDeposits()) {
                    	fixedDepositsStatement.setInt(1, fd.getId());
                    	fixedDepositsStatement.setBoolean(2, fd.isAlive()); 
                    	fixedDepositsStatement.setFloat(3, fd.getPrincipal());
                    	fixedDepositsStatement.setTimestamp(4, Timestamp.valueOf(fd.getCreationTime()));
                    	fixedDepositsStatement.setTimestamp(5, Timestamp.valueOf(fd.getEndingTime()));
                    	fixedDepositsStatement.setInt(6, c.getId());
                    	
                    	fixedDepositsStatement.executeUpdate();
                    }
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Account getAccountOfCustomer(int cusId){
        try(
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        ) {
            try(
                    Statement statement = conn.createStatement();
                    ResultSet rs2 = statement.executeQuery("SELECT * from accounts where customer_id = " + cusId);
            ){
                if(rs2.next()){
                    int accNumber = rs2.getInt("number");
                    float balance = rs2.getFloat("balance");
                    return new Account(accNumber, balance);
                }


            } catch(SQLException e){
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }

    private static void addAllTransactionsOfCustomer(Customer c){
        try(
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        ) {
            try(
                    Statement statement = conn.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT * from transactions where customer_id = " + c.getId());
            ){

                while(rs.next()){
                    int id = rs.getInt("id");
                    String type = rs.getString("type");
                    float amount = rs.getFloat("amount");
                    float balance = rs.getFloat("balance");

                    c.addTransactionWithId(new Transaction(id, type, amount, balance));
                }

            }catch(SQLException e){
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private static void addPasswordHistoryOfCustomer(Customer c){
        try(
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM passwords where customer_id = ?");
                ){

            statement.setInt(1,c.getId());
            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                String password = cryptography.decrypt(rs.getString("password"));
//                TODO: ADD only to password history if this doesn't work
                c.setPassword(password);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    private static void addFixedDepositsOfCustomer(Customer c) {
    	try(
    			Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
    			Statement statement = conn.createStatement();
    			ResultSet rs = statement.executeQuery("SELECT * FROM fixed_deposits where customer_id = " + c.getId());
    			){
    			
    			while(rs.next()) {
    				int id = rs.getInt("id");
    				boolean isAlive = rs.getBoolean("is_alive");
    				float principal = rs.getFloat("principal");
    				
    				LocalDateTime creationTime = LocalDateTime.ofInstant(rs.getTimestamp("creation_time").toInstant(), ZoneId.of("UTC"));
    				LocalDateTime endingTime = LocalDateTime.ofInstant(rs.getTimestamp("ending_time").toInstant(), ZoneId.of("UTC"));
    				
    				FixedDeposit fd = new FixedDeposit(id, principal, creationTime, endingTime, isAlive);
    				c.addFixedDepositWithId(fd);
    			}
    			
    			
    	}catch(SQLException s) {
    		s.printStackTrace();
    	}
    }
}
