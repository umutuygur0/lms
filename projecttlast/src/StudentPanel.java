import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentPanel extends JPanel {
    private JButton selectCourseButton;
    private JButton timelineButton;
    private JButton certificateButton;
    private JButton helpButton;
    private JButton backButton;
    private JComboBox<String> courseSelectionComboBox;
    private JLabel personalDataLabel;
    private JTable timelineTable;
    private DefaultTableModel tableModel;


    private DatabaseHandler dbHandler;
    private int accountId;
    
    
    public StudentPanel(DatabaseHandler dbHandler, int accountId) {
        this.dbHandler = dbHandler;
        this.accountId = accountId;
        initComponents();
        loadCourses();
    }

    private void initComponents() {
        setLayout(null);

        JLabel titleLabel = new JLabel("Student Panel");
        titleLabel.setBounds(20, 20, 200, 30);
        add(titleLabel);

        selectCourseButton = new JButton("Select Course");
        selectCourseButton.setBounds(20, 60, 200, 30);
        add(selectCourseButton);


        certificateButton = new JButton("Certificate");
        certificateButton.setBounds(20, 101, 200, 30);
        add(certificateButton);

        helpButton = new JButton("Help");
        helpButton.setBounds(20, 142, 200, 30);
        add(helpButton);

        backButton = new JButton("Back");
        backButton.setBounds(20, 183, 200, 30);
        add(backButton);

        courseSelectionComboBox = new JComboBox<>();
        courseSelectionComboBox.setBounds(20, 224, 200, 30);
        add(courseSelectionComboBox);

        personalDataLabel = new JLabel();
        personalDataLabel.setBounds(250, 60, 350, 200); 
        add(personalDataLabel);




        loadData();

        selectCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateToCoursePanel();
            }
        });

        certificateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateToCertificatePanel();
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateToHelpPanel();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToLogin();
            }
        });
       
        
    }

    private void loadData() {
        ResultSet rs = dbHandler.getStudentData(accountId);
        try {
            if (rs.next()) {
                StringBuilder data = new StringBuilder("<html>");
                data.append("Account ID: ").append(accountId).append("<br>");
                data.append("Username: ").append(rs.getString("Username")).append("<br>");
                data.append("Account Type: Student").append("<br>");
                data.append("Name: ").append(rs.getString("Student_Name")).append("<br>");
                data.append("Email: ").append(rs.getString("Email")).append("<br>");
                data.append("Phone: ").append(rs.getString("Phone_num")).append("<br>");
                data.append("</html>");
                personalDataLabel.setText(data.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private void loadCourses() {
        ResultSet rs = dbHandler.getAllCourses();
        try {
            while (rs.next()) {
                courseSelectionComboBox.addItem(rs.getString("Course_Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void navigateToCoursePanel() {
        String selectedCourse = (String) courseSelectionComboBox.getSelectedItem();
        if (selectedCourse != null) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.getContentPane().removeAll();
            frame.setContentPane(new CoursePanel(dbHandler, accountId, selectedCourse, false));
            frame.revalidate();
            frame.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a course.");
        }
    } 

    private void navigateToCertificatePanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new CertificatePanel(dbHandler, accountId));
        frame.revalidate();
        frame.repaint();
    }

    private void navigateToHelpPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new HelpPanel(dbHandler, accountId));
        frame.revalidate();
        frame.repaint();
    }

    private void backToLogin() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new LoginRegisterPanel(dbHandler));
        frame.revalidate();
        frame.repaint();
    }
    
    
}
