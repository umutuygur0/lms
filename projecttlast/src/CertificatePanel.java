import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CertificatePanel extends JPanel {
    private JLabel studentIdLabel;
    private JLabel completionStatusLabel;
    private JLabel issueDateLabel;
    private JButton backButton; 

    private DatabaseHandler dbHandler;
    private int accountId;

    public CertificatePanel(DatabaseHandler dbHandler, int accountId) {
        this.dbHandler = dbHandler;
        this.accountId = accountId;
        initComponents();
    }

    private void initComponents() {
        setLayout(null);

        JLabel titleLabel = new JLabel("Certificate");
        titleLabel.setBounds(20, 20, 200, 30);
        add(titleLabel);

        studentIdLabel = new JLabel();
        studentIdLabel.setBounds(20, 60, 300, 30);
        add(studentIdLabel);

        completionStatusLabel = new JLabel();
        completionStatusLabel.setBounds(20, 100, 300, 30);
        add(completionStatusLabel);

        issueDateLabel = new JLabel();
        issueDateLabel.setBounds(20, 140, 300, 30);
        add(issueDateLabel);

        backButton = new JButton("Back");
        backButton.setBounds(20, 180, 100, 30);
        add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToPreviousPanel();
            }
        });

        loadData();
    }

    private void loadData() {
        ResultSet rs = dbHandler.getStudentEnrollmentData(accountId);
        try {
            if (rs.next()) {
                int studentId = rs.getInt("Student_ID");
                LocalDate enrollmentDate = rs.getDate("Enrollment_Date").toLocalDate();
                LocalDate estimatedGraduationDate = enrollmentDate.plusYears(4);
                LocalDate currentDate = LocalDate.now();

                studentIdLabel.setText("Student ID: " + studentId);
                issueDateLabel.setText("Estimated Graduation Date: " + estimatedGraduationDate.format(DateTimeFormatter.ISO_DATE));

                if (currentDate.isAfter(estimatedGraduationDate)) {
                    completionStatusLabel.setText("Completion Status: Graduated");
                } else {
                    completionStatusLabel.setText("Completion Status: Not yet graduated");
                    issueDateLabel.setText("Certificate: Not available until graduation");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void backToPreviousPanel() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.getContentPane().removeAll();
        frame.setContentPane(new StudentPanel(dbHandler, accountId));
        frame.revalidate();
        frame.repaint();
    }
}
