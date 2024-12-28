import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoursePanel extends JPanel {
    private JTextField courseIdField;
    private JTextField courseNameField;
    private JTextField courseAbstractField;
    private JTextField semesterField;
    private JTextArea coursePageContentField;
    private JButton assignButton;
    private JButton backButton;
    private JButton assignmentButton;
    
    private DatabaseHandler dbHandler; 
    private int accountId;
    private String courseName;
    private boolean isInstructor;

    public CoursePanel(DatabaseHandler dbHandler, int accountId, String courseName, boolean isInstructor) {
        this.dbHandler = dbHandler;
        this.accountId = accountId;
        this.courseName = courseName;
        this.isInstructor = isInstructor;
        initComponents();
        loadCourseData();
    }

    private void initComponents() {
        setLayout(null);

        JLabel titleLabel = new JLabel("Course Panel");
        titleLabel.setBounds(20, 20, 200, 30);
        add(titleLabel);

        JLabel courseIdLabel = new JLabel("Course ID:");
        courseIdLabel.setBounds(20, 60, 100, 30);
        add(courseIdLabel);

        courseIdField = new JTextField();
        courseIdField.setBounds(140, 60, 200, 30);
        courseIdField.setEnabled(false);
        add(courseIdField);

        JLabel courseNameLabel = new JLabel("Course Name:");
        courseNameLabel.setBounds(20, 100, 100, 30);
        add(courseNameLabel);

        courseNameField = new JTextField();
        courseNameField.setBounds(140, 100, 200, 30);
        courseNameField.setEnabled(false);
        add(courseNameField);

        JLabel courseAbstractLabel = new JLabel("Abstract:");
        courseAbstractLabel.setBounds(20, 140, 100, 30);
        add(courseAbstractLabel);

        courseAbstractField = new JTextField();
        courseAbstractField.setBounds(140, 140, 200, 30);
        courseAbstractField.setEnabled(isInstructor);
        add(courseAbstractField);

        JLabel semesterLabel = new JLabel("Semester:");
        semesterLabel.setBounds(20, 180, 100, 30);
        add(semesterLabel);

        semesterField = new JTextField();
        semesterField.setBounds(140, 180, 200, 30);
        semesterField.setEnabled(isInstructor);
        add(semesterField);

        JLabel coursePageContentLabel = new JLabel("Course Page Content:");
        coursePageContentLabel.setBounds(20, 220, 200, 30);
        add(coursePageContentLabel);

        coursePageContentField = new JTextArea();
        coursePageContentField.setBounds(20, 260, 320, 100);
        coursePageContentField.setEnabled(isInstructor);
        add(coursePageContentField);

        assignmentButton = new JButton("assignment");
        assignmentButton.setBounds(20, 380, 150, 30);
        add(assignmentButton);

        if (isInstructor) {
            assignButton = new JButton("Assign");
            assignButton.setBounds(20, 420, 150, 30);
            add(assignButton);
        }

        backButton = new JButton("Back");
        backButton.setBounds(isInstructor ? 190 : 20, 420, 150, 30);
        add(backButton);

        if (isInstructor) {
            assignButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    assignCourse();
                }
            });
        }

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToPreviousPanel();
            }
        });

        assignmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int courseId = Integer.parseInt(courseIdField.getText());  // Assuming courseIdField is a JTextField with the course ID
                int studentId = accountId;  // Assuming accountId is the current student's ID
                navigateToAssignmentPanel(courseId, studentId);
            }
        });
    } 

    private void loadCourseData() {
        ResultSet rs = dbHandler.getCourseData(courseName);
        try {
            if (rs.next()) {
                courseIdField.setText(String.valueOf(rs.getInt("Course_ID")));
                courseNameField.setText(rs.getString("Course_Name"));
                courseAbstractField.setText(rs.getString("Abstract"));
                semesterField.setText(rs.getString("Semester"));
                coursePageContentField.setText(rs.getString("Coursepage_content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void assignCourse() {
        int courseId = Integer.parseInt(courseIdField.getText()); 
        String courseName = courseNameField.getText();
        String courseAbstract = courseAbstractField.getText();
        String semester = semesterField.getText();
        String coursePageContent = coursePageContentField.getText();
        String instructorIdsString = JOptionPane.showInputDialog(this, "Enter Instructor IDs (comma separated):");

        List<Integer> instructorIds = new ArrayList<>();
        for (String id : instructorIdsString.split(",")) {
            instructorIds.add(Integer.parseInt(id.trim()));
        }

        if (dbHandler.updateCourse(courseId, courseName, courseAbstract, semester, coursePageContent, instructorIds)) {
        	
            JOptionPane.showMessageDialog(this, "Course assigned!");
        } else {
            JOptionPane.showMessageDialog(this, "Error assigning course.");
        }
    }

    private void backToPreviousPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(isInstructor ? new InstructorPanel(dbHandler, accountId) : new StudentPanel(dbHandler, accountId));
        frame.revalidate();
        frame.repaint();
    }

    private void navigateToAssignmentPanel(int courseId, int studentId) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new AssignmentPanel(dbHandler, courseId, studentId));
        frame.revalidate();
        frame.repaint();
    }

}

