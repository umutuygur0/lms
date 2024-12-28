import java.sql.*;
import java.util.List;

public class DatabaseHandler {
    private Connection connection;

    public DatabaseHandler(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isUsernameUnique(String username) {
        try {
            String query = "SELECT COUNT(*) FROM Account WHERE Username = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(int accountId, String username, String password) {
        if (!isUsernameUnique(username)) {
            return false; // Username already exists
        }
        try {
            String query = "UPDATE Account SET Username = ?, Password = ? WHERE Account_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setInt(3, accountId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean loginUser(String username, String password) {
        try {
            String query = "SELECT COUNT(*) FROM Account WHERE Username = ? AND Password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getAccountId(String username, String password) {
        try {
            String query = "SELECT Account_ID FROM Account WHERE Username = ? AND Password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Account_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getAccountType(int accountId) {
        try {
            String query = "SELECT Account_TYPE FROM Account WHERE Account_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Account_TYPE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ResultSet getUserData(int accountId) {
        try {
            String query = "SELECT * FROM Account WHERE Account_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, accountId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet getStudentData(int accountId) {
        try {
            String query = "SELECT s.Student_Name, s.Email, s.Phone_num, a.Username " +
                           "FROM Student s JOIN Account a ON s.account_Account_ID = a.Account_ID " +
                           "WHERE s.account_Account_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, accountId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ResultSet getStudentEnrollmentData(int accountId) {
        try {
            String query = "SELECT Student_ID, Enrollment_Date FROM Student WHERE account_Account_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, accountId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getInstructorData(int accountId) {
        try {
            String query = "SELECT i.Instructor_Name, i.Email, i.Phone_num, a.Username " +
                           "FROM Instructor i JOIN Account a ON i.account_Account_ID = a.Account_ID " +
                           "WHERE i.account_Account_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, accountId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet getAdminData(int accountId) {
        try {
            String query = "SELECT a.Admin_Name, ac.Username " +
                           "FROM Administration a JOIN Account ac ON a.account_Account_ID = ac.Account_ID " +
                           "WHERE a.account_Account_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, accountId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserCredentials(int accountId, String username, String password) {
        try {
            String query = "UPDATE Account SET Username = ?, Password = ? WHERE Account_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setInt(3, accountId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createCourse(int courseId, String courseName, String courseAbstract, String semester, String coursePageContent, List<Integer> instructorIds) {
        try {
            connection.setAutoCommit(false);

            // Insert into Course table
            String courseQuery = "INSERT INTO Course (Course_ID, Course_Name, Abstract, Semester, Coursepage_content) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement courseStmt = connection.prepareStatement(courseQuery);
            courseStmt.setInt(1, courseId);
            courseStmt.setString(2, courseName);
            courseStmt.setString(3, courseAbstract);
            courseStmt.setString(4, semester);
            courseStmt.setString(5, coursePageContent);
            courseStmt.executeUpdate();

            // Insert into Course_Instructor table
            String instructorQuery = "INSERT INTO Course_Instructor (Course_ID, Instructor_ID) VALUES (?, ?)";
            PreparedStatement instructorStmt = connection.prepareStatement(instructorQuery);
            for (int instructorId : instructorIds) {
                instructorStmt.setInt(1, courseId);
                instructorStmt.setInt(2, instructorId);
                instructorStmt.executeUpdate();
            }

            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCourse(int courseId, String courseName, String courseAbstract, String semester, String coursePageContent, List<Integer> instructorIds) {
        try {
            connection.setAutoCommit(false);

            // Update Course table
            String courseQuery = "UPDATE Course SET Course_Name = ?, Abstract = ?, Semester = ?, Coursepage_content = ? WHERE Course_ID = ?";
            PreparedStatement courseStmt = connection.prepareStatement(courseQuery);
            courseStmt.setString(1, courseName);
            courseStmt.setString(2, courseAbstract);
            courseStmt.setString(3, semester);
            courseStmt.setString(4, coursePageContent);
            courseStmt.setInt(5, courseId);
            courseStmt.executeUpdate();

            // Delete existing instructor assignments for the course
            String deleteInstructorQuery = "DELETE FROM Course_Instructor WHERE Course_ID = ?";
            PreparedStatement deleteInstructorStmt = connection.prepareStatement(deleteInstructorQuery);
            deleteInstructorStmt.setInt(1, courseId);
            deleteInstructorStmt.executeUpdate();

            // Insert new instructor assignments
            String insertInstructorQuery = "INSERT INTO Course_Instructor (Course_ID, Instructor_ID) VALUES (?, ?)";
            PreparedStatement insertInstructorStmt = connection.prepareStatement(insertInstructorQuery);
            for (int instructorId : instructorIds) {
                insertInstructorStmt.setInt(1, courseId);
                insertInstructorStmt.setInt(2, instructorId);
                insertInstructorStmt.executeUpdate();
            }

            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateCourseDetails(int courseId, String courseName, String courseAbstract, String semester, String coursePageContent) {
        try {
            connection.setAutoCommit(false);

            // Update Course table
            String courseQuery = "UPDATE Course SET Course_Name = ?, Abstract = ?, Semester = ?, Coursepage_content = ? WHERE Course_ID = ?";
            PreparedStatement courseStmt = connection.prepareStatement(courseQuery);
            courseStmt.setString(1, courseName);
            courseStmt.setString(2, courseAbstract); 
            courseStmt.setString(3, semester);
            courseStmt.setString(4, coursePageContent);
            courseStmt.setInt(5, courseId);
            courseStmt.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        }
        return false;
    }



    
    public ResultSet getAllCourses() {
        try {
            String query = "SELECT Course_Name FROM Course";
            PreparedStatement stmt = connection.prepareStatement(query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet getCoursesByInstructor(int instructorId) {
        try {
            String query = "SELECT c.Course_ID, c.Course_Name " +
                           "FROM Course c JOIN Course_Instructor ci ON c.Course_ID = ci.Course_ID " +
                           "WHERE ci.Instructor_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, instructorId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ResultSet getCourseData(String courseName) {
        try {
            String query = "SELECT Course_ID, Course_Name, Abstract, Semester, Coursepage_content " +
                           "FROM Course WHERE Course_Name = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, courseName);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean submitSupportRequest(int userId, String content) {
        try {
            String query = "INSERT INTO Support (AC_Account_ID, Content) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, content);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet getAllSupportRequests() {
        try {
            String query = "SELECT * FROM Support";
            PreparedStatement stmt = connection.prepareStatement(query);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean addAssignment(int courseId, int assignmentId, String title, String deadline, String content) {
        try {
            String query = "INSERT INTO Assignment (Assignment_ID, Course_ID, Title, Deadline, Content) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, assignmentId);
            stmt.setInt(2, courseId);
            stmt.setString(3, title);
            stmt.setString(4, deadline);
            stmt.setString(5, content);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    

    
    
    

    public boolean addFeedback(int assignmentId, int studentId, String grade, String content) {
        try {
            String query = "UPDATE Assignment SET Grade = ?, Feedback_Content = ? WHERE Assignment_ID = ? AND Student_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, grade);
            stmt.setString(2, content);
            stmt.setInt(3, assignmentId);
            stmt.setInt(4, studentId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createAssignment(int courseId, int assignmentId, String title, String deadline, String content) {
        try {
            String query = "INSERT INTO Assignment (Course_ID, Assignment_ID, Title, Deadline, Content) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, courseId);
            stmt.setInt(2, assignmentId);
            stmt.setString(3, title);
            stmt.setString(4, deadline);
            stmt.setString(5, content);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public int getCourseIdByName(String courseName) {
        int courseId = -1;
        try {
            String query = "SELECT Course_ID FROM Course WHERE Course_Name = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                courseId = rs.getInt("Course_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseId;
    }
    public ResultSet getAssignmentsForInstructor(int instructorId) {
        ResultSet rs = null;
        String query = "SELECT * FROM Assignment WHERE Instructor_ID = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, instructorId);
            rs = pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
    
    public boolean submitFeedback(int assignmentId, int studentId, String feedback, String grade) {
        try {
            String query = "UPDATE Assignment SET Feedback_Content = ?, Grade = ? WHERE Assignment_ID = ? AND Student_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, feedback);
            stmt.setString(2, grade);
            stmt.setInt(3, assignmentId);
            stmt.setInt(4, studentId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteUserById(int accountId) throws SQLException {
        String deleteAccount = "DELETE FROM Account WHERE Account_ID = ?";

        try (PreparedStatement stmtAccount = connection.prepareStatement(deleteAccount)) {
            stmtAccount.setInt(1, accountId);
            int rowsAffected = stmtAccount.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public ResultSet getAssignmentsByCourseId(int courseId) {
        try {
            String query = "SELECT Assignment_ID, Student_ID, Submission_Content, Grade, Feedback_Content FROM Assignment WHERE Course_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, courseId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet getAssignmentDetails(int assignmentId) {
        try {
            String query = "SELECT Assignment_ID, Title, Deadline, Content, Grade, Feedback_Content, Submission_Content FROM Assignment WHERE Assignment_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, assignmentId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public boolean submitAssignment(int studentId, int assignmentId, String submissionContent) {
        try {
            // Check if the assignment entry exists
            String checkQuery = "SELECT * FROM Assignment WHERE Assignment_ID = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setInt(1, assignmentId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Entry exists, perform an update
                String query = "UPDATE Assignment SET Submission_Content = ?, Student_ID = ? WHERE Assignment_ID = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, submissionContent);
                stmt.setInt(2, studentId);
                stmt.setInt(3, assignmentId);
                int rowsAffected = stmt.executeUpdate();
                System.out.println("Rows affected: " + rowsAffected);
                return rowsAffected > 0;
            } else {
                // Entry does not exist, perform an insert
                String insertQuery = "INSERT INTO Assignment (Assignment_ID, Course_ID, Title, Deadline, Content, Student_ID, Submission_Content, Grade, Feedback_Content) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setInt(1, assignmentId);
                insertStmt.setInt(2, 1); // Assuming Course_ID, Title, Deadline, Content, Grade, Feedback_Content should be set to default or empty values. Adjust as needed.
                insertStmt.setString(3, ""); // Title
                insertStmt.setDate(4, null); // Deadline
                insertStmt.setString(5, ""); // Content
                insertStmt.setInt(6, studentId);
                insertStmt.setString(7, submissionContent);
                insertStmt.setString(8, ""); // Grade
                insertStmt.setString(9, ""); // Feedback_Content
                int rowsInserted = insertStmt.executeUpdate();
                System.out.println("Rows inserted: " + rowsInserted);
                return rowsInserted > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
 

    
    }


