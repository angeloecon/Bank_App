/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package bankapplication;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Nishikata
 */
public interface IUser {
    public void setColor(Color val);
    public Color getColor();
    public String getName();
    public String getUsername();
    public String getPassword();
    public BigDecimal getBalance();
    public String getAccountNumber();
    public void deposit(BigDecimal amount);
    public void withdraw(BigDecimal amount);
    public void transferMoney(BigDecimal amount, User recipient);
    public void addTransaction(String transaction);
    public List<String> getTransaction();
    public String getTransactionHistoryString();
    public List<String> parseTransactionHistory(String transactionHistoryString);
}
