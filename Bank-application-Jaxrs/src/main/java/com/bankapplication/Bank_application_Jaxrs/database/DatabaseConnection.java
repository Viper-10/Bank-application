//package com.bankapplication.Bank_application_Jaxrs.database;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public abstract class DatabaseConnection {
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
//    private static final StringUSER = "root";
//    private static final String PASSWORD = "Win32dll$";
//
//    public static Connection getConnection(){
//    	try {
//    		Class.forName("com.mysql.jdbc.Driver");
//		} catch (ClassNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//        try(
//                Connection c = DriverManager.getConnection(DB_URL, USER, PASSWORD);
//        ) {
//            return c;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//}
