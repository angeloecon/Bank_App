/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bankapplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.math.BigDecimal;
import java.awt.Color;

/**
 *
 * @author Nishikata
 */
public class User {
    private String name;
    private String username;
    private String password;
    private BigDecimal balance;
    private String accountNumber;
    private List<String> transactionHistory = new ArrayList<>();
    private Color userColor;
    
    public User(String name, String username, String password, String accountNumber, BigDecimal balance, String transactionHistoryString, Color userColor){
        this.name = name;
        this.username = username;
        this.password = password;
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.transactionHistory = parseTransactionHistory(transactionHistoryString);
        this.userColor = userColor;
    }
    
    public User(String name, String username, String password, String accountNumber, BigDecimal balance){
        this(name, username,password,accountNumber,balance, "", Color.WHITE);
    }
    
    public void setColor(Color val){
        userColor = val;
    }
    
    public Color getColor(){
        return userColor;
    }
    
   
    
    public String getName(){
        return name;
    }
    
    public String getUsername(){
        return username;
    }
    
    public String getPassword(){
        return password;
    }
    
    public BigDecimal getBalance(){
        return balance;
    }
    
    public String getAccountNumber(){
        return accountNumber;
    }
    
    public void deposit(BigDecimal amount){
        this.balance = this.balance.add(amount);
        System.out.println("Users saved to file."); 
    }
    
    public void withdraw(BigDecimal amount){
//        if(balance >= amount){
//            balance -= amount;
//        } else {
//            System.out.println("Not enough balance");
//        }
          this.balance = this.balance.subtract(amount);
    }
    
    public void transferMoney(BigDecimal amount, User recipient){
            this.balance = this.balance.subtract(amount);
            recipient.deposit(amount);
    }
    
    public void addTransaction(String transaction){
        transactionHistory.add(transaction);
    }
    
    public List<String> getTransaction(){
        return transactionHistory;
    }
    
     public String getTransactionHistoryString(){
        return String.join(";", transactionHistory);
    }
     
    private List<String> parseTransactionHistory(String transactionHistoryString){
        if(transactionHistoryString == null || transactionHistoryString.isEmpty()){
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(transactionHistoryString.split(";"))); 
    }
}
