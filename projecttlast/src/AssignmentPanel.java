import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssignmentPanel extends JPanel {
    private DatabaseHandler dbHandler;
    private int courseId;
    private int studentId;
    private JComboBox<Integer> assignmentComboBox;
    private JTextField assignmentIdField;
    private JTextField titleField;
    private JTextField deadlineField;
    private JTextArea contentArea;
    private JTextField gradeField;
    private JTextArea feedbackArea;
    private JTextArea submissionArea;
    private JButton backButton;
    private JButton submitButton;

    public AssignmentPanel(DatabaseHandler dbHandler, int courseId, int studentId) {
        this.dbHandler = dbHandler;
        this.courseId = courseId;
        this.studentId = studentId;
        initComponents();
    }

    private void initComponents() {
        setLayout(null);

        JLabel assignmentLabel = new JLabel("Select Assignment");
        assignmentLabel.setBounds(20, 20, 150, 30);
        add(assignmentLabel);

        assignmentComboBox = new JComboBox<>();
        assignmentComboBox.setBounds(180, 20, 200, 30);
        add(assignmentComboBox);

        JLabel assignmentIdLabel = new JLabel("Assignment ID:");
        assignmentIdLabel.setBounds(20, 60, 100, 30);
        add(assignmentIdLabel);

        assignmentIdField = new JTextField();
        assignmentIdField.setBounds(180, 60, 200, 30);
        assignmentIdField.setEditable(false);
        add(assignmentIdField);

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setBounds(20, 100, 100, 30);
        add(titleLabel);

        titleField = new JTextField();
        titleField.setBounds(180, 100, 200, 30);
        titleField.setEditable(false);
        add(titleField);

        JLabel deadlineLabel = new JLabel("Deadline:");
        deadlineLabel.setBounds(20, 140, 100, 30);
        add(deadlineLabel);

        deadlineField = new JTextField();
        deadlineField.setBounds(180, 140, 200, 30);
        deadlineField.setEditable(false);
        add(deadlineField);

        JLabel contentLabel = new JLabel("Content:");
        contentLabel.setBounds(20, 180, 100, 30);
        add(contentLabel);

        contentArea = new JTextArea();
        contentArea.setBounds(180, 180, 200, 100);
        contentArea.setEditable(false);
        add(contentArea);

        JLabel gradeLabel = new JLabel("Grade:");
        gradeLabel.setBounds(20, 290, 100, 30);
        add(gradeLabel);

        gradeField = new JTextField();
        gradeField.setBounds(180, 290, 200, 30);
        gradeField.setEditable(false);
        add(gradeField);

        JLabel feedbackLabel = new JLabel("Feedback:");
        feedbackLabel.setBounds(20, 330, 100, 30);
        add(feedbackLabel);

        feedbackArea = new JTextArea();
        feedbackArea.setBounds(180, 330, 200, 100);
        feedbackArea.setEditable(false);
        add(feedbackArea);

        JLabel submissionLabel = new JLabel("Submission:");
        submissionLabel.setBounds(20, 440, 100, 30);
        add(submissionLabel);

        submissionArea = new JTextArea();
        submissionArea.setBounds(180, 440, 200, 100);
        add(submissionArea);

        submitButton = new JButton("Submit");
        submitButton.setBounds(180, 550, 100, 30);
        add(submitButton);

        backButton = new JButton("Back");
        backButton.setBounds(300, 550, 100, 30);
        add(backButton);

        loadAssignments();
        assignmentComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAssignmentDetails((Integer) assignmentComboBox.getSelectedItem());
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToStudentPanel();
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitAssignment();
            }
        });
    }

    private void loadAssignments() {
        try {
            ResultSet rs = dbHandler.getAssignmentsByCourseId(courseId);
            List<Integer> assignmentIds = new ArrayList<>();
            while (rs.next()) {
                assignmentIds.add(rs.getInt("Assignment_ID"));
            }
            for (int id : assignmentIds) {
                assignmentComboBox.addItem(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAssignmentDetails(int assignmentId) {
        try {
            ResultSet rs = dbHandler.getAssignmentDetails(assignmentId);
            if (rs != null && rs.next()) {
                assignmentIdField.setText(String.valueOf(rs.getInt("Assignment_ID")));
                titleField.setText(rs.getString("Title"));
                deadlineField.setText(rs.getString("Deadline"));
                contentArea.setText(rs.getString("Content"));
                gradeField.setText(rs.getString("Grade") != null ? rs.getString("Grade") : "");
                feedbackArea.setText(rs.getString("Feedback_Content") != null ? rs.getString("Feedback_Content") : "");
                submissionArea.setText(rs.getString("Submission_Content"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void submitAssignment() {
        String Submission_Content = submissionArea.getText();
        int assignmentId = (Integer) assignmentComboBox.getSelectedItem();
        if (dbHandler.submitAssignment(studentId, assignmentId, Submission_Content)) {
            JOptionPane.showMessageDialog(this, "Assignment submitted!");
        } else {
            JOptionPane.showMessageDialog(this, "Error submitting assignment.");
        }
        
    }


    private void backToStudentPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new StudentPanel(dbHandler, studentId));
        frame.revalidate();
        frame.repaint();
    }
}

