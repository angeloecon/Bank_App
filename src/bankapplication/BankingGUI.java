/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package bankapplication;

import java.awt.CardLayout;
import java.awt.Color;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Nishikata
 */
public class BankingGUI extends javax.swing.JFrame {
     java.awt.Image icon = java.awt.Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Icons/Images/banklogo.png"));
    private User currentUser;
    private Bank bank = new Bank();
    private User recepient;
    private BigDecimal amount;
    Color hoveredColor = new Color(64,216,255);
    Color hoveredOut = new Color(51,204,255);
    Color defaultColor = new Color(204,204,204);
    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    /**
     * Creates new form BankingGUI
     */
    public BankingGUI(User user) {
        this.currentUser = user;
        initComponents();
        updateInfo();
        customizeTransactionHistoryScrollBar();
    }
    
    private void updateInfo(){
        if(currentUser != null){
            user_name.setText(currentUser.getName());
            balanceLabel(user_balance);
            balanceLabel(deposit_balance);
            balanceLabel(withdraw_balance);
            balanceLabel(transfer_balance);
            displayTransaction();
            profile_panel.setBackground(currentUser.getColor());
            account_number.setText(currentUser.getAccountNumber());
        }
    }
    
    private void deposit(){
        try{
            amount = new BigDecimal(deposit_input.getText());
            if(amount.compareTo(BigDecimal.ZERO) > 0){
                currentUser.deposit(amount);
                bank.success(deposit_warning, "Deposited Successfully.");
                currentUser.addTransaction("   + Deposited: ₱ "+ decimalFormat.format(amount) +" - " + timeTransaction());
                
                updateInfo();
                bank.updateUser(currentUser);
                bank.saveUserData();
                deposit_input.setText("");
            } else {
                bank.failed(deposit_warning, "Invalid Input.");
            }
        } catch (NumberFormatException e){
            bank.failed(deposit_warning, "Input number only.");
        }
    }
    private void withdraw(){
        try{
            amount = new BigDecimal(withdraw_input.getText());
            if(amount.compareTo(BigDecimal.ZERO) > 0){
                if(currentUser.getBalance().compareTo(amount) >= 0){
                    currentUser.withdraw(amount);
                    bank.success(withdraw_warning, "Withdraw Successfully");
                    currentUser.addTransaction("   - Withdrew: ₱ "+ decimalFormat.format(amount) +" - " + timeTransaction());
                    updateInfo();
                    bank.updateUser(currentUser);
                    bank.saveUserData();
                    withdraw_input.setText("");
                } else {
                    bank.failed(withdraw_warning, "Not enough balance.");
                }
            } else {
                throw new Exception();
            }
            
        } catch (NumberFormatException e) {
            bank.failed(withdraw_warning, "Input number only.");
        } catch (Exception e){
            bank.failed(withdraw_warning, "Invalid input.");
        }
    }
    
    private void transferBalance() {
        try {
            if(!transfer_input.getText().isBlank() || !recepient_input.getText().isBlank()){
                amount = new BigDecimal(transfer_input.getText());
                recepient = bank.findAccNum(recepient_input.getText());
                if(!currentUser.getAccountNumber().equals(recepient.getAccountNumber())){
                    if( amount.compareTo(BigDecimal.ZERO) > 0){
                        if(currentUser.getBalance().compareTo(amount) >= 0){
                            currentUser.transferMoney(amount, recepient);
                            bank.success(transfer_warning, "Sent Successfully.");
                            currentUser.addTransaction("   <- Transferred: ₱ " +  decimalFormat.format(amount) + " to "+ recepient.getName() +" - " + timeTransaction());
                            recepient.addTransaction("   -> Received: ₱ " +  decimalFormat.format(amount) + " from " + currentUser.getName() + " - " +timeTransaction());
                            updateInfo();
                            bank.updateUser(currentUser);
                            bank.updateUser(recepient);
                            bank.saveUserData();
                            transfer_input.setText("");
                            recepient_input.setText("");
                        } else {
                            bank.failed(transfer_warning, "Not enough balance.");
                        }   
                    } else {
                        throw new NumberFormatException();
                    }
                    
                } else {
                    bank.failed(transfer_warning, "Receiver cannot be your self.");
                }  
            } else {
                throw new Exception();
            }
        } catch (NullPointerException e){
            bank.failed(transfer_warning, "Recepient not found."); 
        } catch (NumberFormatException e){
            bank.failed(transfer_warning,"Invalid Input.");
        } catch (Exception e){
            bank.failed(transfer_warning, "Enter details.");
            System.out.println(e.getMessage());
        }
    }
    
    private void balanceLabel(JLabel label){
        label.setText(decimalFormat.format(currentUser.getBalance()));
    }
    
    private void displayTransaction(){
        transaction_text.setText("");
        List<String> history = currentUser.getTransaction();
        if(history != null){
                for(String transaction : history){
                transaction_text.append(transaction + "\n");
            }
        } else {
            transaction_text.append("No transaction history available");
        }
    }
    
    private String timeTransaction(){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd / HH:mm");
        LocalDateTime now  = LocalDateTime.now();
        String formattedDate = now.format(format);
        return formattedDate;
    }
   
    
    void changeCard(String card){
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, card);
    }
    
    void setColor(JPanel panel){
        panel.setBackground(new Color(100,100,100));
    }
    
    void resetColor(JPanel panel){
        panel.setBackground(new Color(0,0,0));
    }
    
    private void customizeTransactionHistoryScrollBar(){
        transaction_history.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new java.awt.Color(102,204,255);
                this.trackColor = new java.awt.Color(80, 150, 200);
            }

            @Override
            protected javax.swing.JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected javax.swing.JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private javax.swing.JButton createZeroButton() {
                javax.swing.JButton button = new javax.swing.JButton();
                java.awt.Dimension zeroDim = new java.awt.Dimension(0, 0);
                button.setPreferredSize(zeroDim);
                button.setMinimumSize(zeroDim);
                button.setMaximumSize(zeroDim);
                return button;
            }
        });
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        nav_menu = new javax.swing.JPanel();
        home_panel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        deposit_panel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        withdraw_panel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        transfer_panel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        signout_btn = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        user_balance = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        account_number = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        transaction_history = new javax.swing.JScrollPane();
        transaction_text = new javax.swing.JTextArea();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel12 = new javax.swing.JLabel();
        profile_panel = new javax.swing.JPanel();
        user_name = new javax.swing.JLabel();
        option_btn = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel42 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        jPanel43 = new javax.swing.JPanel();
        deposit_balance = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jPanel50 = new javax.swing.JPanel();
        jPanel51 = new javax.swing.JPanel();
        deposit_separator = new javax.swing.JSeparator();
        deposit_btn = new javax.swing.JButton();
        deposit_input = new javax.swing.JTextField();
        deposit_warning = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel31 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jPanel40 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jPanel41 = new javax.swing.JPanel();
        withdraw_balance = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jPanel48 = new javax.swing.JPanel();
        jPanel49 = new javax.swing.JPanel();
        withdraw_separator = new javax.swing.JSeparator();
        withdraw_btn = new javax.swing.JButton();
        withdraw_input = new javax.swing.JTextField();
        withdraw_warning = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel35 = new javax.swing.JPanel();
        jPanel36 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jPanel39 = new javax.swing.JPanel();
        transfer_balance = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel30 = new javax.swing.JLabel();
        jPanel37 = new javax.swing.JPanel();
        jPanel38 = new javax.swing.JPanel();
        transfer_separator = new javax.swing.JSeparator();
        transfer_btn = new javax.swing.JButton();
        transfer_input = new javax.swing.JTextField();
        transfer_warning = new javax.swing.JLabel();
        recepient_input = new javax.swing.JTextField();
        recepient_separator = new javax.swing.JSeparator();
        transfer_label = new javax.swing.JLabel();
        recepient_label = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Davao Bank");
        setIconImage(icon);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(230, 400));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setOpaque(false);

        nav_menu.setOpaque(false);
        nav_menu.setRequestFocusEnabled(false);
        nav_menu.setLayout(new java.awt.GridLayout(4, 1));

        home_panel.setBackground(new java.awt.Color(100, 100, 100));
        home_panel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        home_panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                home_nav(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Images/home.png"))); // NOI18N
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Home");
        jLabel5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout home_panelLayout = new javax.swing.GroupLayout(home_panel);
        home_panel.setLayout(home_panelLayout);
        home_panelLayout.setHorizontalGroup(
            home_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(home_panelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
        );
        home_panelLayout.setVerticalGroup(
            home_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        nav_menu.add(home_panel);

        deposit_panel.setBackground(new java.awt.Color(0, 0, 0));
        deposit_panel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deposit_panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deposit_nav(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Images/deposit.png"))); // NOI18N
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel6.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Deposit");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout deposit_panelLayout = new javax.swing.GroupLayout(deposit_panel);
        deposit_panel.setLayout(deposit_panelLayout);
        deposit_panelLayout.setHorizontalGroup(
            deposit_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deposit_panelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
        );
        deposit_panelLayout.setVerticalGroup(
            deposit_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        nav_menu.add(deposit_panel);

        withdraw_panel.setBackground(new java.awt.Color(0, 0, 0));
        withdraw_panel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        withdraw_panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                withdraw_nav(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Images/withdraw.png"))); // NOI18N
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel7.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Withdraw");
        jLabel7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout withdraw_panelLayout = new javax.swing.GroupLayout(withdraw_panel);
        withdraw_panel.setLayout(withdraw_panelLayout);
        withdraw_panelLayout.setHorizontalGroup(
            withdraw_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(withdraw_panelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
        );
        withdraw_panelLayout.setVerticalGroup(
            withdraw_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        nav_menu.add(withdraw_panel);

        transfer_panel.setBackground(new java.awt.Color(0, 0, 0));
        transfer_panel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        transfer_panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                transfer_nav(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Images/transfer.png"))); // NOI18N
        jLabel4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel8.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Transfer Money");
        jLabel8.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout transfer_panelLayout = new javax.swing.GroupLayout(transfer_panel);
        transfer_panel.setLayout(transfer_panelLayout);
        transfer_panelLayout.setHorizontalGroup(
            transfer_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transfer_panelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
        );
        transfer_panelLayout.setVerticalGroup(
            transfer_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        nav_menu.add(transfer_panel);

        jPanel12.setOpaque(false);
        jPanel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                transfer_nav(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Bank");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Davao");

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Images/Logo 70 x 80 px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        signout_btn.setBackground(new java.awt.Color(250, 0, 0));
        signout_btn.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        signout_btn.setForeground(new java.awt.Color(255, 255, 255));
        signout_btn.setText("Sign out");
        signout_btn.setBorder(null);
        signout_btn.setBorderPainted(false);
        signout_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        signout_btn.setFocusPainted(false);
        signout_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                signout_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                signout_btnMouseExited(evt);
            }
        });
        signout_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signout_btnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(nav_menu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(signout_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nav_menu, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(signout_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 230, 400));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Images/Bank Image.png"))); // NOI18N
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 231, -1));

        jPanel1.add(jPanel3, java.awt.BorderLayout.WEST);

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));
        contentPanel.setLayout(new java.awt.CardLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(102, 204, 255));

        jLabel13.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Balance");

        user_balance.setFont(new java.awt.Font("Microsoft YaHei", 1, 18)); // NOI18N
        user_balance.setForeground(new java.awt.Color(255, 255, 255));
        user_balance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        user_balance.setText("1,000.00");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Account ID");

        account_number.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        account_number.setForeground(new java.awt.Color(255, 255, 255));
        account_number.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        account_number.setText("09123512");
        account_number.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        jLabel28.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("₱");

        jSeparator1.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(account_number))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(user_balance, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator1))))
                .addGap(17, 17, 17))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(user_balance)
                            .addComponent(jLabel28))
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(account_number))
                .addGap(16, 16, 16))
        );

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));

        transaction_history.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        transaction_text.setEditable(false);
        transaction_text.setColumns(1);
        transaction_text.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        transaction_text.setLineWrap(true);
        transaction_text.setRows(5);
        transaction_text.setTabSize(7);
        transaction_text.setWrapStyleWord(true);
        transaction_text.setBorder(null);
        transaction_text.setRequestFocusEnabled(false);
        transaction_history.setViewportView(transaction_text);

        jLayeredPane1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Images/Profile.png"))); // NOI18N
        jLayeredPane1.setLayer(jLabel12, javax.swing.JLayeredPane.PALETTE_LAYER);
        jLayeredPane1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        profile_panel.setBackground(new java.awt.Color(51, 204, 255));

        javax.swing.GroupLayout profile_panelLayout = new javax.swing.GroupLayout(profile_panel);
        profile_panel.setLayout(profile_panelLayout);
        profile_panelLayout.setHorizontalGroup(
            profile_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        profile_panelLayout.setVerticalGroup(
            profile_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLayeredPane1.add(profile_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 400, 100));

        user_name.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        user_name.setText("Name");
        jLayeredPane1.add(user_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 100, 200, -1));

        option_btn.setBackground(new java.awt.Color(102, 102, 102));
        option_btn.setForeground(new java.awt.Color(153, 153, 153));
        option_btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Images/Untitled design (6).png"))); // NOI18N
        option_btn.setBorder(null);
        option_btn.setBorderPainted(false);
        option_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        option_btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        option_btn.setOpaque(true);
        option_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                option_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                option_btnMouseExited(evt);
            }
        });
        option_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option_btnActionPerformed(evt);
            }
        });
        jLayeredPane1.add(option_btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 100, 30, 30));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(transaction_history)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(transaction_history, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        contentPanel.add(jPanel2, "card1");

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jPanel42.setBackground(new java.awt.Color(255, 255, 255));

        jLabel45.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Images/deposit.gif"))); // NOI18N

        jPanel43.setBackground(new java.awt.Color(51, 204, 255));

        deposit_balance.setFont(new java.awt.Font("Microsoft YaHei", 1, 18)); // NOI18N
        deposit_balance.setForeground(new java.awt.Color(255, 255, 255));
        deposit_balance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        deposit_balance.setText("12312312");

        jLabel47.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(255, 255, 255));
        jLabel47.setText("₱");

        jLabel48.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(255, 255, 255));
        jLabel48.setText("Balance");

        jSeparator9.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel43Layout = new javax.swing.GroupLayout(jPanel43);
        jPanel43.setLayout(jPanel43Layout);
        jPanel43Layout.setHorizontalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel43Layout.createSequentialGroup()
                .addGroup(jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel43Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(jLabel47)
                            .addComponent(jLabel48)))
                    .addGroup(jPanel43Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deposit_balance)))
                .addGap(31, 31, 31))
        );
        jPanel43Layout.setVerticalGroup(
            jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel43Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel48)
                .addGap(18, 18, 18)
                .addGroup(jPanel43Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(deposit_balance))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout jPanel42Layout = new javax.swing.GroupLayout(jPanel42);
        jPanel42.setLayout(jPanel42Layout);
        jPanel42Layout.setHorizontalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel42Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel42Layout.setVerticalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
            .addComponent(jPanel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel50.setBackground(new java.awt.Color(255, 255, 255));

        jPanel51.setBackground(new java.awt.Color(255, 255, 255));
        jPanel51.setPreferredSize(new java.awt.Dimension(355, 0));
        jPanel51.setLayout(new java.awt.BorderLayout());

        deposit_separator.setForeground(new java.awt.Color(204, 204, 204));
        deposit_separator.setPreferredSize(new java.awt.Dimension(3, 3));
        jPanel51.add(deposit_separator, java.awt.BorderLayout.CENTER);

        deposit_btn.setBackground(new java.awt.Color(51, 204, 255));
        deposit_btn.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        deposit_btn.setForeground(new java.awt.Color(255, 255, 255));
        deposit_btn.setText("Deposit");
        deposit_btn.setBorderPainted(false);
        deposit_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deposit_btn.setFocusPainted(false);
        deposit_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deposit_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                deposit_btnMouseExited(evt);
            }
        });
        deposit_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deposit_btnActionPerformed(evt);
            }
        });

        deposit_input.setFont(new java.awt.Font("Microsoft YaHei", 0, 14)); // NOI18N
        deposit_input.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        deposit_input.setBorder(null);
        deposit_input.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                deposit_inputFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                deposit_inputFocusLost(evt);
            }
        });
        deposit_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                deposit_inputKeyPressed(evt);
            }
        });

        deposit_warning.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        deposit_warning.setForeground(new java.awt.Color(255, 0, 0));
        deposit_warning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jPanel7.setBackground(new java.awt.Color(51, 204, 255));
        jPanel7.setPreferredSize(new java.awt.Dimension(109, 55));

        jLabel37.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("Deposit");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel50Layout = new javax.swing.GroupLayout(jPanel50);
        jPanel50.setLayout(jPanel50Layout);
        jPanel50Layout.setHorizontalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(deposit_warning, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
            .addGroup(jPanel50Layout.createSequentialGroup()
                .addGap(130, 130, 130)
                .addComponent(deposit_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel50Layout.createSequentialGroup()
                .addContainerGap(74, Short.MAX_VALUE)
                .addGroup(jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(deposit_input, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel51, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(76, 76, 76))
        );
        jPanel50Layout.setVerticalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel50Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(deposit_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel51, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(deposit_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deposit_warning)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addComponent(jLabel17)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addComponent(jPanel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        contentPanel.add(jPanel9, "card2");

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jPanel40.setBackground(new java.awt.Color(255, 255, 255));

        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Images/withdraw.gif"))); // NOI18N

        jPanel41.setBackground(new java.awt.Color(51, 204, 255));

        withdraw_balance.setFont(new java.awt.Font("Microsoft YaHei", 1, 18)); // NOI18N
        withdraw_balance.setForeground(new java.awt.Color(255, 255, 255));
        withdraw_balance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        withdraw_balance.setText("12312312");

        jLabel43.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(255, 255, 255));
        jLabel43.setText("₱");

        jLabel44.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 255, 255));
        jLabel44.setText("Balance");

        jSeparator8.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel41Layout = new javax.swing.GroupLayout(jPanel41);
        jPanel41.setLayout(jPanel41Layout);
        jPanel41Layout.setHorizontalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel41Layout.createSequentialGroup()
                .addGroup(jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel41Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(jLabel43)
                            .addComponent(jLabel44)))
                    .addGroup(jPanel41Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(withdraw_balance)))
                .addGap(31, 31, 31))
        );
        jPanel41Layout.setVerticalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel41Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel44)
                .addGap(18, 18, 18)
                .addGroup(jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(withdraw_balance))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
            .addComponent(jPanel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel48.setBackground(new java.awt.Color(255, 255, 255));

        jPanel49.setLayout(new java.awt.BorderLayout());

        withdraw_separator.setForeground(new java.awt.Color(204, 204, 204));
        jPanel49.add(withdraw_separator, java.awt.BorderLayout.CENTER);

        withdraw_btn.setBackground(new java.awt.Color(51, 204, 255));
        withdraw_btn.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        withdraw_btn.setForeground(new java.awt.Color(255, 255, 255));
        withdraw_btn.setText("Withdraw");
        withdraw_btn.setBorderPainted(false);
        withdraw_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        withdraw_btn.setFocusPainted(false);
        withdraw_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                withdraw_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                withdraw_btnMouseExited(evt);
            }
        });
        withdraw_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withdraw_btnActionPerformed(evt);
            }
        });

        withdraw_input.setFont(new java.awt.Font("Microsoft YaHei", 0, 14)); // NOI18N
        withdraw_input.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        withdraw_input.setBorder(null);
        withdraw_input.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                withdraw_inputFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                withdraw_inputFocusLost(evt);
            }
        });
        withdraw_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                withdraw_inputKeyPressed(evt);
            }
        });

        withdraw_warning.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        withdraw_warning.setForeground(new java.awt.Color(255, 0, 0));
        withdraw_warning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jPanel6.setBackground(new java.awt.Color(51, 204, 255));

        jLabel35.setBackground(new java.awt.Color(51, 204, 255));
        jLabel35.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("Withdraw");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel48Layout = new javax.swing.GroupLayout(jPanel48);
        jPanel48.setLayout(jPanel48Layout);
        jPanel48Layout.setHorizontalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(withdraw_warning, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel48Layout.createSequentialGroup()
                .addContainerGap(74, Short.MAX_VALUE)
                .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel48Layout.createSequentialGroup()
                        .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(withdraw_input, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel49, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(76, 76, 76))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel48Layout.createSequentialGroup()
                        .addComponent(withdraw_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(122, 122, 122))))
        );
        jPanel48Layout.setVerticalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel48Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(withdraw_input, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(withdraw_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(withdraw_warning)
                .addGap(9, 9, 9))
        );

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addComponent(jLabel27)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel31Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27)
                .addGap(18, 18, 18)
                .addComponent(jPanel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel31, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        contentPanel.add(jPanel10, "card3");

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jPanel36.setBackground(new java.awt.Color(255, 255, 255));

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Images/send.gif"))); // NOI18N

        jPanel39.setBackground(new java.awt.Color(51, 204, 255));

        transfer_balance.setFont(new java.awt.Font("Microsoft YaHei", 1, 18)); // NOI18N
        transfer_balance.setForeground(new java.awt.Color(255, 255, 255));
        transfer_balance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        transfer_balance.setText("12312312");

        jLabel39.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setText("₱");

        jLabel40.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setText("Balance");

        jSeparator7.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel39Layout = new javax.swing.GroupLayout(jPanel39);
        jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel39Layout.createSequentialGroup()
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel39Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel39)
                            .addComponent(jLabel40)))
                    .addGroup(jPanel39Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(transfer_balance)))
                .addGap(31, 31, 31))
        );
        jPanel39Layout.setVerticalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel39Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel40)
                .addGap(18, 18, 18)
                .addGroup(jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(transfer_balance))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
            .addComponent(jPanel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel37.setBackground(new java.awt.Color(255, 255, 255));

        jPanel38.setLayout(new java.awt.BorderLayout());

        transfer_separator.setForeground(new java.awt.Color(204, 204, 204));
        jPanel38.add(transfer_separator, java.awt.BorderLayout.CENTER);

        transfer_btn.setBackground(new java.awt.Color(51, 204, 255));
        transfer_btn.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        transfer_btn.setForeground(new java.awt.Color(255, 255, 255));
        transfer_btn.setText("Send");
        transfer_btn.setBorderPainted(false);
        transfer_btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        transfer_btn.setFocusPainted(false);
        transfer_btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                transfer_btnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                transfer_btnMouseExited(evt);
            }
        });
        transfer_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transfer_btnActionPerformed(evt);
            }
        });

        transfer_input.setFont(new java.awt.Font("Microsoft YaHei", 0, 14)); // NOI18N
        transfer_input.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        transfer_input.setBorder(null);
        transfer_input.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                transfer_inputFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                transfer_inputFocusLost(evt);
            }
        });
        transfer_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                transfer_inputKeyPressed(evt);
            }
        });

        transfer_warning.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        transfer_warning.setForeground(new java.awt.Color(255, 0, 0));
        transfer_warning.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        recepient_input.setFont(new java.awt.Font("Microsoft YaHei", 0, 14)); // NOI18N
        recepient_input.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        recepient_input.setBorder(null);
        recepient_input.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                recepient_inputFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                recepient_inputFocusLost(evt);
            }
        });
        recepient_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                recepient_inputKeyPressed(evt);
            }
        });

        recepient_separator.setForeground(new java.awt.Color(204, 204, 204));

        transfer_label.setBackground(new java.awt.Color(204, 204, 204));
        transfer_label.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        transfer_label.setText("Amount");
        transfer_label.setDoubleBuffered(true);

        recepient_label.setBackground(new java.awt.Color(204, 204, 204));
        recepient_label.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        recepient_label.setText("Receiver's Account Number");
        recepient_label.setDoubleBuffered(true);

        jPanel13.setBackground(new java.awt.Color(51, 204, 255));

        jLabel31.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Transfer Money");
        jLabel31.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel37Layout = new javax.swing.GroupLayout(jPanel37);
        jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(transfer_warning, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel37Layout.createSequentialGroup()
                .addContainerGap(74, Short.MAX_VALUE)
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(recepient_separator, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(recepient_input, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(transfer_input, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel38, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(76, 76, 76))
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel37Layout.createSequentialGroup()
                        .addGap(131, 131, 131)
                        .addComponent(transfer_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel37Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(transfer_label)
                            .addComponent(recepient_label, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel37Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel37Layout.setVerticalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(recepient_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(recepient_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(recepient_separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(transfer_label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(transfer_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(transfer_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(transfer_warning)
                .addGap(9, 9, 9))
        );

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel36, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addComponent(jLabel30)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel35Layout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(jPanel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addComponent(jPanel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel35, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        contentPanel.add(jPanel11, "card4");

        jPanel1.add(contentPanel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    
    private void signout_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signout_btnActionPerformed
        // TODO add your handling code here:
        this.currentUser = null;
        LoginGUI login = new LoginGUI();
        login.setLocationRelativeTo(this);
        login.setVisible(true);
        dispose();
    }//GEN-LAST:event_signout_btnActionPerformed

    private void transfer_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transfer_btnActionPerformed
        // TODO add your handling code here:
        transferBalance();
    }//GEN-LAST:event_transfer_btnActionPerformed

    private void withdraw_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withdraw_btnActionPerformed
        // TODO add your handling code here:
        this.withdraw();
    }//GEN-LAST:event_withdraw_btnActionPerformed

    private void deposit_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deposit_btnActionPerformed
        // TODO add your handling code here:
        this.deposit();
    }//GEN-LAST:event_deposit_btnActionPerformed

    private void home_nav(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_home_nav
        // TODO add your handling code here:
        changeCard("card1");
        setColor(home_panel);
        resetColor(deposit_panel);
        resetColor(withdraw_panel);
        resetColor(transfer_panel);
        home_panel.setOpaque(true); 
        home_panel.repaint();
    }//GEN-LAST:event_home_nav

    private void deposit_nav(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deposit_nav
        // TODO add your handling code here:
        changeCard("card2");
        setColor(deposit_panel);
        resetColor(home_panel);
        resetColor(withdraw_panel);
        resetColor(transfer_panel);
    }//GEN-LAST:event_deposit_nav

    private void withdraw_nav(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_withdraw_nav
        // TODO add your handling code here:
        changeCard("card3");
        setColor(withdraw_panel);
        resetColor(deposit_panel);
        resetColor(home_panel);
        resetColor(transfer_panel);
    }//GEN-LAST:event_withdraw_nav

    private void transfer_nav(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_transfer_nav
        // TODO add your handling code here:
        changeCard("card4");
        setColor(transfer_panel);
        resetColor(deposit_panel);
        resetColor(home_panel);
        resetColor(withdraw_panel);
    }//GEN-LAST:event_transfer_nav

    private void deposit_inputFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_deposit_inputFocusGained
        // TODO add your handling code here:
        deposit_input.setForeground(hoveredColor);
        deposit_separator.setForeground(Color.BLACK);
        deposit_separator.setBackground(hoveredColor);
    }//GEN-LAST:event_deposit_inputFocusGained

    private void deposit_inputFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_deposit_inputFocusLost
        // TODO add your handling code here:
        deposit_input.setForeground(defaultColor);
        deposit_separator.setForeground(defaultColor);
        deposit_separator.setBackground(defaultColor);
    }//GEN-LAST:event_deposit_inputFocusLost

    private void withdraw_inputFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_withdraw_inputFocusGained
        // TODO add your handling code here:
        withdraw_input.setForeground(hoveredColor);
        withdraw_separator.setForeground(Color.BLACK);
        withdraw_separator.setBackground(hoveredColor);
    }//GEN-LAST:event_withdraw_inputFocusGained

    private void withdraw_inputFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_withdraw_inputFocusLost
        // TODO add your handling code here:
        withdraw_input.setForeground(defaultColor);
        withdraw_separator.setForeground(defaultColor);
        withdraw_separator.setBackground(defaultColor);
    }//GEN-LAST:event_withdraw_inputFocusLost

    private void recepient_inputFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_recepient_inputFocusGained
        // TODO add your handling code here:
        recepient_input.setForeground(hoveredColor);
        recepient_label.setForeground(Color.BLACK);
        recepient_separator.setForeground(Color.BLACK);
        recepient_separator.setBackground(hoveredColor);
    }//GEN-LAST:event_recepient_inputFocusGained

    private void recepient_inputFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_recepient_inputFocusLost
        // TODO add your handling code here:
        recepient_input.setForeground(defaultColor);
        recepient_label.setForeground(defaultColor);
        recepient_separator.setForeground(defaultColor);
        recepient_separator.setBackground(defaultColor);
    }//GEN-LAST:event_recepient_inputFocusLost

    private void transfer_inputFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_transfer_inputFocusGained
        // TODO add your handling code here:
        transfer_input.setForeground(hoveredColor);
        transfer_label.setForeground(Color.BLACK);
        transfer_separator.setForeground(Color.BLACK);
        transfer_separator.setBackground(hoveredColor);
    }//GEN-LAST:event_transfer_inputFocusGained

    private void transfer_inputFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_transfer_inputFocusLost
        // TODO add your handling code here:
        transfer_input.setForeground(defaultColor);
        transfer_label.setForeground(defaultColor);
        transfer_separator.setForeground(defaultColor);
        transfer_separator.setBackground(defaultColor);
    }//GEN-LAST:event_transfer_inputFocusLost

    private void transfer_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_transfer_btnMouseEntered
        // TODO add your handling code here:
        transfer_btn.setBackground(hoveredColor);
    }//GEN-LAST:event_transfer_btnMouseEntered

    private void transfer_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_transfer_btnMouseExited
        // TODO add your handling code here:
        transfer_btn.setBackground(hoveredOut);
    }//GEN-LAST:event_transfer_btnMouseExited

    private void withdraw_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_withdraw_btnMouseEntered
        // TODO add your handling code here:
        withdraw_btn.setBackground(hoveredColor);
        
    }//GEN-LAST:event_withdraw_btnMouseEntered

    private void withdraw_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_withdraw_btnMouseExited
        // TODO add your handling code here:
        withdraw_btn.setBackground(hoveredOut);
    }//GEN-LAST:event_withdraw_btnMouseExited

    private void deposit_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deposit_btnMouseEntered
        // TODO add your handling code here:
        deposit_btn.setBackground(hoveredColor);
    }//GEN-LAST:event_deposit_btnMouseEntered

    private void deposit_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deposit_btnMouseExited
        // TODO add your handling code here:
        deposit_btn.setBackground(hoveredOut);
    }//GEN-LAST:event_deposit_btnMouseExited

    private void signout_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_signout_btnMouseEntered
        // TODO add your handling code here:
        signout_btn.setBackground(new Color(200,0,0));
    }//GEN-LAST:event_signout_btnMouseEntered

    private void signout_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_signout_btnMouseExited
        // TODO add your handling code here:
        signout_btn.setBackground(new Color(255,0,0));
    }//GEN-LAST:event_signout_btnMouseExited

    private void option_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option_btnActionPerformed
        // TODO add your handling code here:
        Color selectedColor = JColorChooser.showDialog (this,"",currentUser.getColor());

        if(selectedColor != null){
            currentUser.setColor(selectedColor);
            profile_panel.setBackground(selectedColor);
            bank.updateUser(currentUser);
            bank.saveUserData();
        }
    }//GEN-LAST:event_option_btnActionPerformed

    private void option_btnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_option_btnMouseEntered
        // TODO add your handling code here:
        option_btn.setBackground(Color.BLACK);
    }//GEN-LAST:event_option_btnMouseEntered

    private void option_btnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_option_btnMouseExited
        // TODO add your handling code here:
        option_btn.setBackground(new Color(102,102,102));
    }//GEN-LAST:event_option_btnMouseExited

    private void deposit_inputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_deposit_inputKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.deposit();
        }
    }//GEN-LAST:event_deposit_inputKeyPressed

    private void withdraw_inputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_withdraw_inputKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
           this.withdraw(); 
        }
    }//GEN-LAST:event_withdraw_inputKeyPressed

    private void transfer_inputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_transfer_inputKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.transferBalance();
        }
    }//GEN-LAST:event_transfer_inputKeyPressed

    private void recepient_inputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_recepient_inputKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.transferBalance();
        }
    }//GEN-LAST:event_recepient_inputKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BankingGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BankingGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BankingGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BankingGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel account_number;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel deposit_balance;
    private javax.swing.JButton deposit_btn;
    private javax.swing.JTextField deposit_input;
    private javax.swing.JPanel deposit_panel;
    private javax.swing.JSeparator deposit_separator;
    private javax.swing.JLabel deposit_warning;
    private javax.swing.JPanel home_panel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel50;
    private javax.swing.JPanel jPanel51;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JPanel nav_menu;
    private javax.swing.JButton option_btn;
    private javax.swing.JPanel profile_panel;
    private javax.swing.JTextField recepient_input;
    private javax.swing.JLabel recepient_label;
    private javax.swing.JSeparator recepient_separator;
    private javax.swing.JButton signout_btn;
    private javax.swing.JScrollPane transaction_history;
    private javax.swing.JTextArea transaction_text;
    private javax.swing.JLabel transfer_balance;
    private javax.swing.JButton transfer_btn;
    private javax.swing.JTextField transfer_input;
    private javax.swing.JLabel transfer_label;
    private javax.swing.JPanel transfer_panel;
    private javax.swing.JSeparator transfer_separator;
    private javax.swing.JLabel transfer_warning;
    private javax.swing.JLabel user_balance;
    private javax.swing.JLabel user_name;
    private javax.swing.JLabel withdraw_balance;
    private javax.swing.JButton withdraw_btn;
    private javax.swing.JTextField withdraw_input;
    private javax.swing.JPanel withdraw_panel;
    private javax.swing.JSeparator withdraw_separator;
    private javax.swing.JLabel withdraw_warning;
    // End of variables declaration//GEN-END:variables
}
