import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FeedbackPanel extends JPanel {
    private DatabaseHandler dbHandler;
    private int instructorId;
    private JTextField assignmentIdField;
    private JTextField studentIdField;
    private JTextField gradeField;
    private JTextArea feedbackArea;
    private JButton submitButton;
    private JButton backButton;

    public FeedbackPanel(DatabaseHandler dbHandler, int instructorId) {
        this.dbHandler = dbHandler;
        this.instructorId = instructorId;
        initComponents();
    }

    private void initComponents() {
        setLayout(null);

        JLabel assignmentIdLabel = new JLabel("Assignment ID:");
        assignmentIdLabel.setBounds(20, 20, 100, 30);
        add(assignmentIdLabel);

        assignmentIdField = new JTextField();
        assignmentIdField.setBounds(140, 20, 200, 30);
        add(assignmentIdField);

        JLabel studentIdLabel = new JLabel("Student ID:");
        studentIdLabel.setBounds(20, 60, 100, 30);
        add(studentIdLabel);

        studentIdField = new JTextField();
        studentIdField.setBounds(140, 60, 200, 30);
        add(studentIdField);

        JLabel gradeLabel = new JLabel("Grade:");
        gradeLabel.setBounds(20, 100, 100, 30);
        add(gradeLabel);

        gradeField = new JTextField();
        gradeField.setBounds(140, 100, 200, 30);
        add(gradeField);

        JLabel feedbackLabel = new JLabel("Feedback:");
        feedbackLabel.setBounds(20, 140, 100, 30);
        add(feedbackLabel);

        feedbackArea = new JTextArea();
        feedbackArea.setBounds(140, 140, 200, 100);
        add(feedbackArea);

        submitButton = new JButton("Submit");
        submitButton.setBounds(140, 260, 100, 30);
        add(submitButton);

        backButton = new JButton("Back");
        backButton.setBounds(260, 260, 100, 30);
        add(backButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitFeedback();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToInstructorPanel();
            }
        });
    }

    private void submitFeedback() {
        int Assignment_Assignment_ID = Integer.parseInt(assignmentIdField.getText());
        int Student_Student_ID = Integer.parseInt(studentIdField.getText());
        String feedback = feedbackArea.getText();
        String grade = gradeField.getText();
        
        System.out.println("Submitting feedback: assignmentId=" + Assignment_Assignment_ID + ", studentId=" + Student_Student_ID + ", feedback=" + feedback + ", grade=" + grade);
        boolean isSubmitted = dbHandler.submitFeedback(Assignment_Assignment_ID, Student_Student_ID, feedback, grade);
        System.out.println("Feedback submitted status: " + isSubmitted);
        if (isSubmitted) {
            JOptionPane.showMessageDialog(this, "Feedback submitted!");
        } else {
            JOptionPane.showMessageDialog(this, "Error submitting feedback.");
        }
    }

    private void backToInstructorPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new InstructorPanel(dbHandler, instructorId));
        frame.revalidate();
        frame.repaint();
    }
    
}
