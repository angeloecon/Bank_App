/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package bankapplication;

import javax.swing.JLabel;

/**
 *
 * @author Nishikata
 */
public interface IBank {
    public boolean registerUser(String name, String username, String password);
    public User login (String username, String password);
    public User findUser(String username);
    public User findAccNum(String accNum);
    public void updateUser(User updateUser);
    public void success(JLabel label, String message);
    public void failed(JLabel label, String message);
    public void warningTimer(JLabel warningLabel, int delay, String message);
}
