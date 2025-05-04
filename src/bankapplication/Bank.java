/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bankapplication;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.math.BigDecimal;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.Timer;
/**
 *
 * @author Nishikata
 */
public class Bank {
    private List<User> users = new ArrayList<>();
    private static final String USERS_FILE = "C:\\Users\\Nishikata\\Documents\\NetBeansProjects\\BankApplication\\src\\bankapplication\\users.txt";
    
    public Bank(){
        loadUsers();
    }
    
    public boolean registerUser(String name, String username,String password){
        if(findUser(username) != null){
            return false;
        } 
        BigDecimal balance = new BigDecimal("0.0");
        String accNumber = accountNumGenerator();
        User newUser = new User(name,username,password, accNumber, balance);
        users.add(newUser);
        saveUsers();
        return true;
    }
    
    public User login(String username, String password){
        for(User user :users){
            if(user.getUsername().equals(username) && user.getPassword().equals(password)){
                return user;
            }
        }
        return null;
    }
    
    public User findUser(String username){
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        System.out.println("User not found."); 
        return null;
    }
    
    public User findAccNum(String accNum){
        for(User user : users){
            if(user.getAccountNumber().equals(accNum)){
                return user;
            }
        }
        System.out.println("Account Number not found.");
        return null;
    }
    
    private void loadUsers(){
        try(
        BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE)))
        {
            String line;
            
            while((line = reader.readLine()) != null){
                User user = parseUser(line);
                if(user != null){
                    users.add(user);
                }
            }
        } catch (IOException e){
           System.out.print(e);
                
        }
    }
    
    private void saveUsers(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))){
            for(User user : users){
                String json = userToJson(user);
                writer.write(json);
                writer.newLine();
                System.out.println("Users saved to file."); 
            } 
        }catch (IOException e){
             System.out.println("Error saving users: " + e.getMessage()); // Debugging: Print error
            e.printStackTrace(); //Print the full error stack trace.
        }
    }
    
    public void saveUserData(){
        System.out.println("Users saved to file."); 
        saveUsers();
    }
    
    public void updateUser(User updateUser){
        for(User user: users){
            if(user.getUsername().equals(updateUser.getUsername())){
                int index = users.indexOf(user);
                users.set(users.indexOf(user), updateUser);
                return;
            }
        }
    }
    private String userToJson(User user){
        return "{" +
                "\"name\":\"" + user.getName() + "\","+
                "\"username\":\"" + user.getUsername() + "\","+
                "\"password\":\"" + user.getPassword() + "\","+
                "\"accountNumber\":\"" + user.getAccountNumber() + "\","+
                "\"balance\":\"" + user.getBalance().toString() + "\","+
                "\"transactionHistory\":\"" + user.getTransactionHistoryString() + "\","+
                "\"userColor\":\"" + user.getColor().getRGB() + "\","+
                "}";
    }
    
    private User parseUser(String json){
        try{
            String name = extractValue(json, "name");
            String username = extractValue(json, "username");
            String password = extractValue(json, "password");
            String accNumber = extractValue(json, "accountNumber");
            BigDecimal balance = new BigDecimal(extractValue(json, "balance"));
            String transactionHistoryString = extractValue(json, "transactionHistory");
            int ColorRGB = Integer.parseInt(extractValue(json,"userColor"));
            Color userColor = new Color(ColorRGB);
            System.out.println("Parsed user: name=" + name + ", username=" + username + ", password=" + password + ", accountNumber=" + accNumber + "balance" + balance); // Debugging
            return new User(name, username, password, accNumber, balance, transactionHistoryString, userColor);
            
        } catch (NumberFormatException | NullPointerException e){
            return null;
        }
    }
    
    private String extractValue(String json, String key){
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if(startIndex == -1) {
            return null;
        }
        
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if(endIndex == -1){
            return null;
        }
        return json.substring(startIndex, endIndex);
    }
    
    private String accountNumGenerator(){
        Random random = new Random();
        String accountNumber;
        do {
            accountNumber = String.valueOf(100000+random.nextInt(900000));
        } while (accountNumChecker(accountNumber));
        return accountNumber;
                
    }
    
    private boolean accountNumChecker(String accountNum){
        for(User user: users){
            if(user.getAccountNumber().equals(accountNum)){
                return true;
            }
        } return false;
    }
    
//   Multi purpose Design Codes diri
    
    public void success(JLabel label,String message){
        label.setForeground(Color.green);
        warningTimer(label, 2000, message);
    }
    
    public  void failed(JLabel label, String message){
        label.setForeground(Color.red);
        warningTimer(label, 2000, message);
    }
    
    public void warningTimer(JLabel warningLabel, int delay, String message){
        warningLabel.setText(message);
        Timer timer = new Timer(delay, (ActionEvent evt) -> {
            warningLabel.setText("");
            ((Timer) evt.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

}
