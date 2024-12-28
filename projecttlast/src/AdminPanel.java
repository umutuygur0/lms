import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

public class AdminPanel extends JPanel {
    
    private JButton addCourseButton;
    private JButton supportViewButton;
    private JButton backButton;
    private JLabel personalDataLabel;
    private JTable helpTable;
    private JScrollPane scrollPane;
    private JTextField accountIdField;
    private JButton deleteButton;

    private DatabaseHandler dbHandler;
    private int accountId;

    public AdminPanel(DatabaseHandler dbHandler, int accountId) {
        this.dbHandler = dbHandler;
        this.accountId = accountId;
        initComponents();
    }

    private void initComponents() {
        setLayout(null);

        JLabel titleLabel = new JLabel("Admin Panel");
        titleLabel.setBounds(20, 20, 200, 30);
        add(titleLabel);

        JLabel accountIdLabel = new JLabel("Account ID:");
        accountIdLabel.setBounds(20, 60, 100, 30);
        add(accountIdLabel);

        accountIdField = new JTextField();
        accountIdField.setBounds(140, 60, 200, 30);
        add(accountIdField);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(140, 100, 100, 30);
        add(deleteButton);



        addCourseButton = new JButton("Add Course");
        addCourseButton.setBounds(20, 163, 420, 30);
        add(addCourseButton);

        supportViewButton = new JButton("Support View");
        supportViewButton.setBounds(20, 204, 420, 30);
        add(supportViewButton);

        backButton = new JButton("Back");
        backButton.setBounds(20, 245, 420, 30);
        add(backButton);

        personalDataLabel = new JLabel();
        personalDataLabel.setBounds(363, 52, 350, 100);
        add(personalDataLabel);

        loadData();

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        addCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createCourse();
            }
        });

        supportViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelpRequests();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToLogin();
            }
        });

        helpTable = new JTable();
        scrollPane = new JScrollPane(helpTable);
        scrollPane.setBounds(10, 303, 600, 300);
        add(scrollPane);
    }

    private void loadData() {
        ResultSet rs = dbHandler.getAdminData(accountId);
        try {
            if (rs.next()) {
                StringBuilder data = new StringBuilder("<html>");
                data.append("Account ID: ").append(accountId).append("<br>");
                data.append("Username: ").append(rs.getString("Username")).append("<br>");
                data.append("Account Type: Admin").append("<br>");
                data.append("Name: ").append(rs.getString("Admin_Name")).append("<br>");
                data.append("</html>");
                personalDataLabel.setText(data.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteUser() {
        String accountIdText = accountIdField.getText();
        if (accountIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Account ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int accountId = Integer.parseInt(accountIdText);
        try {
            boolean success = dbHandler.deleteUserById(accountId);
            if (success) {
                JOptionPane.showMessageDialog(this, "User deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createCourse() {
        int courseId = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Course ID:"));
        String courseName = JOptionPane.showInputDialog(this, "Enter Course Name:");
        String courseAbstract = JOptionPane.showInputDialog(this, "Enter Course Abstract:");
        String semester = JOptionPane.showInputDialog(this, "Enter Semester:");
        String coursePageContent = JOptionPane.showInputDialog(this, "Enter Course Page Content:");
        String instructorIdsString = JOptionPane.showInputDialog(this, "Enter Instructor IDs (comma separated):");

        List<Integer> instructorIds = new ArrayList<>();
        for (String id : instructorIdsString.split(",")) {
            instructorIds.add(Integer.parseInt(id.trim()));
        }

        if (dbHandler.createCourse(courseId, courseName, courseAbstract, semester, coursePageContent, instructorIds)) {
            JOptionPane.showMessageDialog(this, "Course created!");
        } else {
            JOptionPane.showMessageDialog(this, "Error creating course.");
        }
    }

    private void showHelpRequests() {
        ResultSet rs = dbHandler.getAllSupportRequests();
        HelpTableModel model = new HelpTableModel(rs);
        helpTable.setModel(model);
    }

    private void backToLogin() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new LoginRegisterPanel(dbHandler));
        frame.revalidate();
        frame.repaint();
    }

    private class HelpTableModel extends AbstractTableModel {
        private List<String> columnNames;
        private List<List<Object>> data;

        public HelpTableModel(ResultSet rs) {
            columnNames = new ArrayList<>();
            data = new ArrayList<>();

            try {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(metaData.getColumnName(i));
                }

                while (rs.next()) {
                    List<Object> row = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getObject(i));
                    }
                    data.add(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

       
        public int getRowCount() {
            return data.size();
        }

        public int getColumnCount() {
            return columnNames.size();
        }

        
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data.get(rowIndex).get(columnIndex);
        }

        public String getColumnName(int column) {
            return columnNames.get(column);
        }
    }
    
}

