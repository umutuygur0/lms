import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InstructorPanel extends JPanel {
    private JButton selectCourseButton;
    private JButton changeCourseButton;
    private JButton feedbackButton;
    private JButton addAssignmentButton;
    private JButton assignmentViewButton;
    private JButton helpButton;
    private JButton backButton;
    private JLabel personalDataLabel;
   
    private JTable assignmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> courseSelectionComboBox;

    private DatabaseHandler dbHandler;
    private int accountId;
    private int instructorId;

    public InstructorPanel(DatabaseHandler dbHandler, int accountId) {
        this.dbHandler = dbHandler;
        this.accountId = accountId;
        initComponents();
        
    }

    private void initComponents() {
        setLayout(null);

        JLabel titleLabel = new JLabel("Instructor Panel");
        titleLabel.setBounds(20, 20, 200, 30);
        add(titleLabel);

        selectCourseButton = new JButton("Select Course");
        selectCourseButton.setBounds(20, 60, 200, 30);
        add(selectCourseButton);

        changeCourseButton = new JButton("Change Course");
        changeCourseButton.setBounds(20, 100, 200, 30);
        changeCourseButton.setEnabled(false);
        add(changeCourseButton);

        feedbackButton = new JButton("Feedback");
        feedbackButton.setBounds(20, 140, 200, 30);
        feedbackButton.setEnabled(false);
        add(feedbackButton);

        addAssignmentButton = new JButton("Add Assignment");
        addAssignmentButton.setBounds(20, 180, 200, 30);
        addAssignmentButton.setEnabled(false);
        add(addAssignmentButton);

        assignmentViewButton = new JButton("Assignment View");
        assignmentViewButton.setBounds(20, 220, 200, 30);
        assignmentViewButton.setEnabled(false);
        add(assignmentViewButton);

        helpButton = new JButton("Help");
        helpButton.setBounds(20, 260, 200, 30);
        add(helpButton);

        backButton = new JButton("Back");
        backButton.setBounds(20, 300, 200, 30);
        add(backButton);

        personalDataLabel = new JLabel();
        personalDataLabel.setBounds(250, 60, 306, 149);
        add(personalDataLabel);

        courseSelectionComboBox = new JComboBox<>();
        courseSelectionComboBox.setBounds(20, 340, 200, 30);
        add(courseSelectionComboBox);
        
        tableModel = new DefaultTableModel(new String[]{"Assignment ID", "Student ID", "Submission Content", "Grade", "Feedback"}, 0);
        assignmentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(assignmentTable);
        scrollPane.setBounds(250, 220, 365, 194);
        add(scrollPane);

        loadData();
        loadCourses();
        assignmentViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAssignments();
            }
        });
        selectCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableCourseButtons();
            }
        });

        changeCourseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateToCourseManagementPanel();
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
        addAssignmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (courseSelectionComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(InstructorPanel.this, "Please select a course first.");
                    return;
                }
                navigateToAssignmentManagementPanel();
            }
        });
        feedbackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (courseSelectionComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(InstructorPanel.this, "Please select a course first.");
                    return;
                }
                navigateToFeedbackPanel();
            }
        });
        
    }

    private void loadData() {
        ResultSet rs = dbHandler.getInstructorData(accountId);
        try {
            if (rs.next()) {
                StringBuilder data = new StringBuilder("<html>");
                data.append("Account ID: ").append(accountId).append("<br>");
                data.append("Username: ").append(rs.getString("Username")).append("<br>");
                data.append("Account Type: Instructor").append("<br>");
                data.append("Name: ").append(rs.getString("Instructor_Name")).append("<br>");
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
        ResultSet rs = dbHandler.getCoursesByInstructor(accountId);
        try {
            while (rs.next()) {
                courseSelectionComboBox.addItem(rs.getString("Course_Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void enableCourseButtons() {
        changeCourseButton.setEnabled(true);
        feedbackButton.setEnabled(true);
        addAssignmentButton.setEnabled(true);
        assignmentViewButton.setEnabled(true);
    }

    private void navigateToCourseManagementPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new CourseManagementPanel(dbHandler, accountId));
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
    private void navigateToAssignmentManagementPanel() {
        String selectedCourse = (String) courseSelectionComboBox.getSelectedItem();
        int courseId = dbHandler.getCourseIdByName(selectedCourse);
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new AssignmentManagementPanel(dbHandler, courseId));
        frame.revalidate();
        frame.repaint();
    }
    private void navigateToFeedbackPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new FeedbackPanel(dbHandler, accountId));
        frame.revalidate();
        frame.repaint();
    }
    private void viewAssignments() {
        String selectedCourse = (String) courseSelectionComboBox.getSelectedItem();
        if (selectedCourse != null) {
            try {
                tableModel.setRowCount(0); // Clear existing rows
                int courseId = dbHandler.getCourseIdByName(selectedCourse);
                ResultSet rs = dbHandler.getAssignmentsByCourseId(courseId);
                while (rs.next()) {
                    int assignmentId = rs.getInt("Assignment_ID");
                    int studentId = rs.getInt("Student_ID");
                    String submissionContent = rs.getString("Submission_Content");
                    String grade = rs.getString("Grade");
                    String feedback = rs.getString("Feedback_Content");
                    tableModel.addRow(new Object[]{assignmentId, studentId, submissionContent, grade, feedback});
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a course.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    }
