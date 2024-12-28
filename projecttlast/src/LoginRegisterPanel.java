import javax.swing.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Rectangle;

public class LoginRegisterPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField usernameField;
    private JPasswordField registerPasswordField;
    private JTextField accountIdField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;

    private DatabaseHandler dbHandler;

    public LoginRegisterPanel(DatabaseHandler dbHandler) {
    	setBackground(new Color(240, 240, 240));
        this.dbHandler = dbHandler;
        initComponents();
    }

    private void initComponents() {
        setLayout(null);
        

        JLabel loginLabel = new JLabel("Login");
        loginLabel.setBounds(20, 20, 100, 30);
        add(loginLabel);

        JLabel emailLabel = new JLabel("E-mail");
        emailLabel.setBounds(20, 60, 100, 30);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(140, 60, 200, 30);
        add(emailField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(20, 100, 100, 30);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(140, 100, 200, 30);
        add(passwordField);

        loginButton = new JButton("Confirm");
        loginButton.setBounds(20, 140, 320, 40);
        add(loginButton);

        JLabel registerLabel = new JLabel("Signup");
        registerLabel.setBounds(20, 200, 100, 30);
        add(registerLabel);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(20, 240, 100, 30);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(140, 240, 200, 30);
        add(usernameField);

        JLabel registerPasswordLabel = new JLabel("Password");
        registerPasswordLabel.setBounds(20, 280, 100, 30);
        add(registerPasswordLabel);

        registerPasswordField = new JPasswordField();
        registerPasswordField.setBounds(140, 280, 200, 30);
        add(registerPasswordField);

        JLabel accountIdLabel = new JLabel("Account ID");
        accountIdLabel.setBounds(20, 320, 100, 30);
        add(accountIdLabel);

        accountIdField = new JTextField();
        accountIdField.setBounds(140, 320, 200, 30);
        add(accountIdField);

        registerButton = new JButton("Confirm");
        registerButton.setBounds(20, 360, 320, 40);
        add(registerButton);

        messageLabel = new JLabel();
        messageLabel.setBounds(20, 420, 320, 30);
        add(messageLabel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
    }

    private void login() {
        String username = emailField.getText();
        String password = new String(passwordField.getPassword());
        if (dbHandler.loginUser(username, password)) {
            messageLabel.setText("Login successful!");
            int accountId = dbHandler.getAccountId(username, password);
            int accountType = dbHandler.getAccountType(accountId);
            navigateToPanel(accountId, accountType);
        } else {
            messageLabel.setText("Invalid username or password.");
        }
    }

    private void register() {
        int accountId = Integer.parseInt(accountIdField.getText());
        String username = usernameField.getText();
        String password = new String(registerPasswordField.getPassword());
        if (dbHandler.registerUser(accountId, username, password)) {
            messageLabel.setText("Registration successful!");
        } else {
            messageLabel.setText("Username already exists or account ID is invalid.");
        }
    }

    private void navigateToPanel(int accountId, int accountType) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();

        if (accountType == 1) {
            frame.setContentPane(new StudentPanel(dbHandler, accountId));
        } else if (accountType == 2) {
            frame.setContentPane(new InstructorPanel(dbHandler, accountId));
        } else if (accountType == 0) {
            frame.setContentPane(new AdminPanel(dbHandler, accountId));
        }

        frame.revalidate();
        frame.repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Login/Register Panel");
        DatabaseHandler dbHandler = new DatabaseHandler("jdbc:mysql://localhost:3306/Projectlast?serverTimezone=UTC", "root", "root");
        frame.setContentPane(new LoginRegisterPanel(dbHandler));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);
        frame.setVisible(true);
    }
}
 