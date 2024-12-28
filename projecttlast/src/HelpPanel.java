import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpPanel extends JPanel {
    private JTextField userIdField; 
    private JTextArea contentField;
    private JButton confirmButton;
    private JButton backButton;
    
    private DatabaseHandler dbHandler;
    private int accountId;

    public HelpPanel(DatabaseHandler dbHandler, int accountId) {
        this.dbHandler = dbHandler;
        this.accountId = accountId;
        initComponents();
    }

    private void initComponents() {
        setLayout(null);

        JLabel titleLabel = new JLabel("Support");
        titleLabel.setBounds(20, 20, 200, 30);
        add(titleLabel);

        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setBounds(20, 60, 100, 30);
        add(userIdLabel);

        userIdField = new JTextField();
        userIdField.setBounds(140, 60, 200, 30);
        userIdField.setText(String.valueOf(accountId));  // -fill with current user ID
        userIdField.setEditable(false); // User ID field should be non-editable
        add(userIdField);

        JLabel contentLabel = new JLabel("Content:");
        contentLabel.setBounds(20, 100, 100, 30);
        add(contentLabel);

        contentField = new JTextArea();
        contentField.setBounds(20, 140, 320, 100);
        add(contentField);

        confirmButton = new JButton("Confirm");
        confirmButton.setBounds(20, 260, 150, 30);
        add(confirmButton);

        backButton = new JButton("Back");
        backButton.setBounds(190, 260, 150, 30);
        add(backButton);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitSupportRequest();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToPreviousPanel();
            }
        });
    }

    private void submitSupportRequest() {
        int userId = Integer.parseInt(userIdField.getText());
        String content = contentField.getText();

        if (dbHandler.submitSupportRequest(userId, content)) {
            JOptionPane.showMessageDialog(this, "Support request submitted!");
        } else {
            JOptionPane.showMessageDialog(this, "Error submitting support request.");
        }
    }

    private void backToPreviousPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new LoginRegisterPanel(dbHandler));
        frame.revalidate();
        frame.repaint();
    }
}

