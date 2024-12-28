import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseManagementPanel extends JPanel {
    private JTextField courseIdField;
    private JTextField courseNameField;
    private JTextField courseAbstractField; 
    private JTextField semesterField;
    private JTextArea coursePageContentField;
    private JButton updateButton;
    private JButton backButton;

    private DatabaseHandler dbHandler;
    private int accountId;

    public CourseManagementPanel(DatabaseHandler dbHandler, int accountId) {
        this.dbHandler = dbHandler;
        this.accountId = accountId;
        initComponents(); 
    }

    private void initComponents() {
        setLayout(null);

        JLabel titleLabel = new JLabel("Course Management");
        titleLabel.setBounds(20, 20, 200, 30);
        add(titleLabel);

        JLabel courseIdLabel = new JLabel("Course ID");
        courseIdLabel.setBounds(20, 60, 100, 30);
        add(courseIdLabel);

        courseIdField = new JTextField();
        courseIdField.setBounds(140, 60, 200, 30);
        add(courseIdField);

        JLabel courseNameLabel = new JLabel("Course Name");
        courseNameLabel.setBounds(20, 100, 100, 30);
        add(courseNameLabel);

        courseNameField = new JTextField();
        courseNameField.setBounds(140, 100, 200, 30);
        add(courseNameField);

        JLabel courseAbstractLabel = new JLabel("Abstract");
        courseAbstractLabel.setBounds(20, 140, 100, 30);
        add(courseAbstractLabel);

        courseAbstractField = new JTextField();
        courseAbstractField.setBounds(140, 140, 200, 30);
        add(courseAbstractField);

        JLabel semesterLabel = new JLabel("Semester");
        semesterLabel.setBounds(20, 180, 100, 30);
        add(semesterLabel);

        semesterField = new JTextField();
        semesterField.setBounds(140, 180, 200, 30);
        add(semesterField);

        JLabel coursePageContentLabel = new JLabel("Course Page Content");
        coursePageContentLabel.setBounds(20, 220, 200, 30);
        add(coursePageContentLabel);

        coursePageContentField = new JTextArea();
        coursePageContentField.setBounds(140, 260, 200, 100);
        add(coursePageContentField);

        updateButton = new JButton("Update Course");
        updateButton.setBounds(20, 380, 150, 30);
        add(updateButton);

        backButton = new JButton("Back");
        backButton.setBounds(180, 380, 150, 30);
        add(backButton);

        loadCourseData();

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCourse();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToPreviousPanel();
            }
        });
    }

    private void loadCourseData() {
        ResultSet rs = dbHandler.getCourseData(courseNameField.getText());
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

    private void updateCourse() {
        int courseId = Integer.parseInt(courseIdField.getText());
        String courseName = courseNameField.getText();
        String courseAbstract = courseAbstractField.getText();
        String semester = semesterField.getText();
        String coursePageContent = coursePageContentField.getText();

        if (dbHandler.updateCourseDetails(courseId, courseName, courseAbstract, semester, coursePageContent)) {
            JOptionPane.showMessageDialog(this, "Course updated!");
        } else {
            JOptionPane.showMessageDialog(this, "Error updating course.");
        }
    }

    private void backToPreviousPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new InstructorPanel(dbHandler, accountId));
        frame.revalidate();
        frame.repaint();
    }
}
