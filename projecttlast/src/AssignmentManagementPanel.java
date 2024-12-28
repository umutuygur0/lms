import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AssignmentManagementPanel extends JPanel {
    private JTextField assignmentTitleField;
    private JTextField deadlineField;
    private JTextField assignmentIdField;
    private JTextArea contentField;
    private JButton confirmButton;
    private DatabaseHandler dbHandler;
    private int courseId;
    private JButton backButton;


    public AssignmentManagementPanel(DatabaseHandler dbHandler, int courseId) {
        this.dbHandler = dbHandler;
        this.courseId = courseId;
        initComponents();
    }

    private void initComponents() {
        setLayout(null);

        JLabel titleLabel = new JLabel("Assignment Title");
        titleLabel.setBounds(20, 20, 100, 30);
        add(titleLabel);

        assignmentTitleField = new JTextField();
        assignmentTitleField.setBounds(140, 20, 200, 30);
        add(assignmentTitleField);

        JLabel deadlineLabel = new JLabel("Deadline");
        deadlineLabel.setBounds(20, 60, 100, 30);
        add(deadlineLabel);

        deadlineField = new JTextField();
        deadlineField.setBounds(140, 60, 200, 30);
        add(deadlineField);

        JLabel assignmentIdLabel = new JLabel("Assignment ID");
        assignmentIdLabel.setBounds(20, 100, 100, 30);
        add(assignmentIdLabel);

        assignmentIdField = new JTextField();
        assignmentIdField.setBounds(140, 100, 200, 30);
        add(assignmentIdField);

        JLabel contentLabel = new JLabel("Content");
        contentLabel.setBounds(20, 140, 100, 30);
        add(contentLabel);

        contentField = new JTextArea();
        contentField.setBounds(140, 141, 200, 100);
        add(contentField);

        confirmButton = new JButton("Confirm");
        confirmButton.setBounds(20, 260, 261, 30);
        add(confirmButton);
        
        backButton = new JButton("Back");
        backButton.setBounds(314, 260, 126, 30);
        add(backButton);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAssignment();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToInstructorPanel();
            }
        });

    }

    private void createAssignment() {
        String title = assignmentTitleField.getText();
        String deadline = deadlineField.getText();
        int assignmentId = Integer.parseInt(assignmentIdField.getText());
        String content = contentField.getText();

        if (dbHandler.createAssignment(courseId, assignmentId, title, deadline, content)) {
            JOptionPane.showMessageDialog(this, "Assignment created!");
        } else {
            JOptionPane.showMessageDialog(this, "Error creating assignment.");
        }
    }
    private void backToInstructorPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new InstructorPanel(dbHandler, courseId));
        frame.revalidate();
        frame.repaint();
    }
}
